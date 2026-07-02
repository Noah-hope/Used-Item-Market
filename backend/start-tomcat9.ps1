$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$backendRoot = Join-Path $projectRoot 'backend'
$maven = 'D:\software\Maven\apache-maven-3.9.16\bin\mvn.cmd'
$tomcatHome = 'D:\software\Tomcat\apache-tomcat-9.0.119'
$tomcatBase = Join-Path $backendRoot 'tomcat9-base'
$warPath = Join-Path $backendRoot 'target\usedItemMarket.war'
$webappsPath = Join-Path $tomcatBase 'webapps'
$rootWarPath = Join-Path $webappsPath 'ROOT.war'
$rootDirPath = Join-Path $webappsPath 'ROOT'

function Invoke-MavenPackage {
    Write-Host 'Building backend WAR with local Maven cache...' -ForegroundColor Cyan
    & $maven -o -DskipTests package
    if ($LASTEXITCODE -eq 0) {
        return
    }

    Write-Warning 'Offline Maven build failed. Falling back to online dependency resolution...'
    & $maven -DskipTests package
    if ($LASTEXITCODE -ne 0) {
        throw "Maven build failed with exit code $LASTEXITCODE"
    }
}

if (!(Test-Path $maven)) {
    throw "Maven not found: $maven"
}
if (!(Test-Path $tomcatHome)) {
    throw "Tomcat not found: $tomcatHome"
}

$stopScript = Join-Path $backendRoot 'stop-tomcat9.ps1'
if (Test-Path $stopScript) {
    & $stopScript
    Start-Sleep -Seconds 2
}

Invoke-MavenPackage

if (!(Test-Path $warPath)) {
    throw "WAR not built: $warPath"
}

if (!(Test-Path $tomcatBase)) {
    New-Item -ItemType Directory -Path $tomcatBase | Out-Null
    foreach ($dir in 'conf','logs','temp','webapps','work') {
        New-Item -ItemType Directory -Path (Join-Path $tomcatBase $dir) | Out-Null
    }
    Copy-Item -Path (Join-Path $tomcatHome 'conf\*') -Destination (Join-Path $tomcatBase 'conf') -Recurse -Force
}

if (Test-Path $rootDirPath) {
    try {
        Remove-Item -LiteralPath $rootDirPath -Recurse -Force
    } catch {
        Start-Sleep -Seconds 2
        Remove-Item -LiteralPath $rootDirPath -Recurse -Force
    }
}
if (Test-Path $rootWarPath) {
    Remove-Item -LiteralPath $rootWarPath -Force
}

Copy-Item -LiteralPath $warPath -Destination $rootWarPath -Force

$existing = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique
if ($existing) {
    Write-Host "Port 8080 is already in use by PID(s): $($existing -join ', ')"
}

$env:CATALINA_HOME = $tomcatHome
$env:CATALINA_BASE = $tomcatBase

& (Join-Path $tomcatHome 'bin\catalina.bat') run
