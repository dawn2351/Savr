# Creates the release signing keystore for CI/CD and prints the GitHub Secrets
# values you need to add. Run once, locally, then back the keystore up offline.
#
# Usage:
#   powershell -ExecutionPolicy Bypass -File .\scripts\create-keystore.ps1

param(
    [string]$Alias = "savr",
    [string]$KeystorePath = "$PSScriptRoot\..\keystore\savr-release.jks"
)

function New-RandomPassword([int]$Length = 24) {
    $chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    $bytes = New-Object byte[] $Length
    [System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
    -join ($bytes | ForEach-Object { $chars[$_ % $chars.Length] })
}

$StorePass = New-RandomPassword
$KeyPass = New-RandomPassword

$Parent = Split-Path -Parent $KeystorePath
if (-not (Test-Path -LiteralPath $Parent)) {
    New-Item -ItemType Directory -Path $Parent | Out-Null
}

$Keytool = if ($env:JAVA_HOME) {
    Join-Path $env:JAVA_HOME "bin\keytool.exe"
} else {
    "keytool"
}

& $Keytool -genkeypair -v `
    -keystore $KeystorePath `
    -alias $Alias `
    -keyalg RSA `
    -keysize 2048 `
    -validity 10000 `
    -storepass $StorePass `
    -keypass $KeyPass `
    -dname "CN=Savr, OU=Mobile, O=Zarnth, C=BD"

if ($LASTEXITCODE -ne 0) {
    Write-Error "keytool failed. Make sure a JDK is installed and JAVA_HOME is set."
    exit $LASTEXITCODE
}

$Base64 = [Convert]::ToBase64String([IO.File]::ReadAllBytes($KeystorePath))

Write-Host ""
Write-Host "== Keystore created: $KeystorePath =="
Write-Host "BACK THIS FILE UP OFFLINE. If you lose it you cannot sign future releases."
Write-Host ""
Write-Host "Add these as GitHub repo Secrets (Settings > Secrets and variables > Actions):"
Write-Host ""
Write-Host "  ANDROID_KEYSTORE_BASE64    = $Base64"
Write-Host "  ANDROID_KEYSTORE_PASSWORD  = $StorePass"
Write-Host "  ANDROID_KEY_ALIAS          = $Alias"
Write-Host "  ANDROID_KEY_PASSWORD       = $KeyPass"
Write-Host ""