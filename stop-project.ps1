$ErrorActionPreference = 'SilentlyContinue'

$projectRoot = $PSScriptRoot
$stopBackendScript = Join-Path $projectRoot 'backend\stop-tomcat9.ps1'
$frontendRoot = Join-Path $projectRoot 'frontend'
$windowTitles = @('Used-Item-Market Backend', 'Used-Item-Market Frontend')

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

$windowProcesses = Get-CimInstance Win32_Process |
    Where-Object {
        $_.Name -eq 'powershell.exe' -and
        $_.CommandLine -and (
            $_.CommandLine -match [regex]::Escape($windowTitles[0]) -or
            $_.CommandLine -match [regex]::Escape($windowTitles[1])
        )
    } |
    Select-Object -ExpandProperty ProcessId -Unique

if ($windowProcesses) {
    Stop-Process -Id $windowProcesses -Force
    Write-Host "Closed project PowerShell window(s): $($windowProcesses -join ', ')"
}

Write-Host 'Project stop command completed.'
