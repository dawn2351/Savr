# Verifies the keystore passwords/alias BEFORE fixing the GitHub secrets.
# Run with the passwords you believe are correct. If a check fails, the
# correct values come from wherever you created the keystore.
#
# Usage:
#   powershell -ExecutionPolicy Bypass -File .\scripts\verify-keystore.ps1 `
#     -StorePassword "your-store-password" -KeyPassword "your-key-password" -Alias savr_key

param(
    [Parameter(Mandatory = $true)][string]$StorePassword,
    [Parameter(Mandatory = $true)][string]$KeyPassword,
    [string]$Alias = "savr_key",
    [string]$KeystorePath = "$PSScriptRoot\..\keystore\savr-release.jks"
)

$Keytool = if ($env:JAVA_HOME) {
    Join-Path $env:JAVA_HOME "bin\keytool.exe"
} else {
    "keytool"
}

Write-Host "== 1/2 Store password + alias check =="
& $Keytool -list -keystore $KeystorePath -storepass $StorePassword -alias $Alias -v *> $null
if ($LASTEXITCODE -ne 0) {
    Write-Host "FAIL: this STORE password does not open the keystore (or the alias is wrong)."
    Write-Host "   If it says 'password was incorrect' -> ANDROID_KEYSTORE_PASSWORD in GitHub is wrong."
    Write-Host "   If it says 'alias' not found          -> ANDROID_KEY_ALIAS in GitHub is wrong."
    exit 1
}
Write-Host "OK: store password works and alias '$Alias' exists."

Write-Host ""
Write-Host "== 2/2 Key password check =="
$TempStore = Join-Path $env:TEMP ("verify-" + [guid]::NewGuid().ToString("N") + ".p12")
try {
    & $Keytool -importkeystore -noprompt `
        -srckeystore $KeystorePath `
        -srcstorepass $StorePassword `
        -srckeypass $KeyPassword `
        -destkeystore $TempStore `
        -deststoretype PKCS12 `
        -deststorepass "temp123" `
        -destkeypass "temp123" *> $null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "FAIL: this KEY password is wrong => ANDROID_KEY_PASSWORD in GitHub is wrong."
        exit 1
    }
    Write-Host "OK: key password works."
} finally {
    if (Test-Path -LiteralPath $TempStore) { Remove-Item -LiteralPath $TempStore -Force }
}

Write-Host ""
Write-Host "ALL CHECKS PASSED."
Write-Host "Re-enter these EXACT values in GitHub (Settings > Secrets and variables > Actions):"
Write-Host "   ANDROID_KEYSTORE_PASSWORD = $StorePassword"
Write-Host "   ANDROID_KEY_ALIAS         = $Alias"
Write-Host "   ANDROID_KEY_PASSWORD      = $KeyPassword"
Write-Host ""
Write-Host "TIP: GitHub secrets are literal strings. Copy the password with no leading/trailing"
Write-Host "space and no trailing newline - that is the #1 cause of 'password was incorrect'."
Write-Host ""