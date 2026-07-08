$ErrorActionPreference = 'Stop'

$projectRoot = $PSScriptRoot
$backendScript = Join-Path $projectRoot 'backend\start-tomcat9.ps1'
$stopProjectScript = Join-Path $projectRoot 'stop-project.ps1'
$frontendRoot = Join-Path $projectRoot 'frontend'
$preferredNpm = 'D:\software\nodejs\npm.cmd'
$backendWindowTitle = 'Used-Item-Market Backend'
$frontendWindowTitle = 'Used-Item-Market Frontend'

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

# Stop any previous frontend/backend windows and processes first so restart is predictable.
if (Test-Path $stopProjectScript) {
    & $stopProjectScript
    Start-Sleep -Seconds 2
}

$backendCommand = @"
Set-Location -LiteralPath '$projectRoot'
`$host.UI.RawUI.WindowTitle = '$backendWindowTitle'
Write-Host 'Starting backend Tomcat service...' -ForegroundColor Cyan
& '$backendScript'
"@

$frontendCommand = @"
Set-Location -LiteralPath '$frontendRoot'
`$host.UI.RawUI.WindowTitle = '$frontendWindowTitle'
Write-Host 'Starting frontend dev server...' -ForegroundColor Green
& '$npmCmd' run dev -- --host 0.0.0.0
"@

$backendProcess = Start-Process -FilePath 'powershell.exe' -ArgumentList @(
    '-NoExit',
    '-ExecutionPolicy', 'Bypass',
    '-Command', $backendCommand
) -PassThru

Start-Sleep -Seconds 2

$frontendProcess = Start-Process -FilePath 'powershell.exe' -ArgumentList @(
    '-NoExit',
    '-ExecutionPolicy', 'Bypass',
    '-Command', $frontendCommand
) -PassThru

Write-Host "Backend window started (PID: $($backendProcess.Id))." -ForegroundColor Cyan
Write-Host "Frontend window started (PID: $($frontendProcess.Id))." -ForegroundColor Green
Write-Host 'Frontend and backend startup windows have been opened.'
