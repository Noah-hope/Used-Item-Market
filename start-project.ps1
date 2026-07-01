$ErrorActionPreference = 'Stop'

$projectRoot = $PSScriptRoot
$backendScript = Join-Path $projectRoot 'backend\start-tomcat9.ps1'
$stopBackendScript = Join-Path $projectRoot 'backend\stop-tomcat9.ps1'
$frontendRoot = Join-Path $projectRoot 'frontend'
$preferredNpm = 'D:\software\nodejs\npm.cmd'

if (!(Test-Path $backendScript)) {
    throw "Backend start script not found: $backendScript"
}
if (!(Test-Path $frontendRoot)) {
    throw "Frontend directory not found: $frontendRoot"
}

if (Test-Path $preferredNpm) {
    $npmCmd = $preferredNpm
} else {
    $npmCommand = Get-Command npm.cmd -ErrorAction SilentlyContinue
    if (!$npmCommand) {
        throw 'npm.cmd not found. Please install Node.js or add npm.cmd to PATH.'
    }
    $npmCmd = $npmCommand.Source
}

# Stop any previous Tomcat 9 instance first so the new window can start cleanly.
if (Test-Path $stopBackendScript) {
    & $stopBackendScript
}

$backendCommand = @"
Set-Location -LiteralPath '$projectRoot'
`$host.UI.RawUI.WindowTitle = 'Used-Item-Market Backend'
Write-Host 'Starting backend Tomcat service...' -ForegroundColor Cyan
& '$backendScript'
"@

$frontendCommand = @"
Set-Location -LiteralPath '$frontendRoot'
`$host.UI.RawUI.WindowTitle = 'Used-Item-Market Frontend'
Write-Host 'Starting frontend dev server...' -ForegroundColor Green
& '$npmCmd' run dev
"@

Start-Process -FilePath 'powershell.exe' -ArgumentList @(
    '-NoExit',
    '-ExecutionPolicy', 'Bypass',
    '-Command', $backendCommand
)

Start-Sleep -Seconds 2

Start-Process -FilePath 'powershell.exe' -ArgumentList @(
    '-NoExit',
    '-ExecutionPolicy', 'Bypass',
    '-Command', $frontendCommand
)

Write-Host 'Frontend and backend startup windows have been opened.'
