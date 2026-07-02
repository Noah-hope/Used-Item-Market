$ErrorActionPreference = 'SilentlyContinue'

$projectRoot = Split-Path -Parent $PSScriptRoot
$backendRoot = Join-Path $projectRoot 'backend'
$tomcatBase = Join-Path $backendRoot 'tomcat9-base'
$tomcatHome = 'D:\software\Tomcat\apache-tomcat-9.0.119'
$serverXml = Join-Path $tomcatBase 'conf\server.xml'

$tomcatProcesses = Get-CimInstance Win32_Process |
    Where-Object {
        $_.Name -eq 'java.exe' -and
        $_.CommandLine -match [regex]::Escape($tomcatBase)
    } |
    Select-Object -ExpandProperty ProcessId

if (Test-Path $serverXml) {
    $env:CATALINA_HOME = $tomcatHome
    $env:CATALINA_BASE = $tomcatBase
    & (Join-Path $tomcatHome 'bin\catalina.bat') stop | Out-Null
    Start-Sleep -Seconds 2
}

if ($tomcatProcesses) {
    Stop-Process -Id $tomcatProcesses -Force
    Write-Host "Stopped backend Tomcat process(es): $($tomcatProcesses -join ', ')"
} else {
    Write-Host 'No running backend Tomcat process found.'
}
