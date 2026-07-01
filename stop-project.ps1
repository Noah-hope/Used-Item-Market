$ErrorActionPreference = 'SilentlyContinue'

$projectRoot = $PSScriptRoot
$stopBackendScript = Join-Path $projectRoot 'backend\stop-tomcat9.ps1'
$frontendRoot = Join-Path $projectRoot 'frontend'

if (Test-Path $stopBackendScript) {
    & $stopBackendScript
}

$frontendProcesses = Get-CimInstance Win32_Process |
    Where-Object {
        $_.Name -in @('node.exe', 'npm.cmd', 'cmd.exe', 'powershell.exe') -and
        $_.CommandLine -match [regex]::Escape($frontendRoot) -and
        $_.CommandLine -match 'vite'
    } |
    Select-Object -ExpandProperty ProcessId -Unique

if ($frontendProcesses) {
    Stop-Process -Id $frontendProcesses -Force
    Write-Host "Stopped frontend process(es): $($frontendProcesses -join ', ')"
} else {
    Write-Host 'No running frontend dev server process found.'
}

Write-Host 'Project stop command completed.'
