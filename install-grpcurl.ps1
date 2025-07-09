# Script to install grpcurl on Windows
Write-Host "=== Installing grpcurl for Windows ===" -ForegroundColor Green

# Check if grpcurl is already installed
if (Get-Command grpcurl -ErrorAction SilentlyContinue) {
    Write-Host "grpcurl is already installed!" -ForegroundColor Green
    grpcurl --version
    exit 0
}

# Create grpcurl directory
$grpcurlDir = "grpcurl"
if (-not (Test-Path $grpcurlDir)) {
    Write-Host "Creating grpcurl directory..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Name $grpcurlDir
}

# Download grpcurl
$version = "1.9.1"
$downloadUrl = "https://github.com/fullstorydev/grpcurl/releases/download/v$version/grpcurl_${version}_windows_x86_64.tar.gz"
$outputFile = "$grpcurlDir/grpcurl.tar.gz"

Write-Host "Downloading grpcurl v$version from GitHub..." -ForegroundColor Yellow
Write-Host "URL: $downloadUrl" -ForegroundColor Cyan

try {
    Invoke-WebRequest -Uri $downloadUrl -OutFile $outputFile -UseBasicParsing
    Write-Host "Download completed!" -ForegroundColor Green
} catch {
    Write-Host "Download failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Extract grpcurl
Write-Host "Extracting grpcurl..." -ForegroundColor Yellow
try {
    # Use tar command (available in Windows 10+)
    tar -xzf $outputFile -C $grpcurlDir
    Write-Host "Extraction completed!" -ForegroundColor Green
} catch {
    Write-Host "Extraction failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Please extract manually or use 7-Zip" -ForegroundColor Yellow
    exit 1
}

# Check if grpcurl.exe exists
$grpcurlExe = "$grpcurlDir/grpcurl.exe"
if (Test-Path $grpcurlExe) {
    Write-Host "grpcurl.exe found!" -ForegroundColor Green
    
    # Test grpcurl
    Write-Host "Testing grpcurl..." -ForegroundColor Yellow
    & $grpcurlExe --version
    
    Write-Host "`n=== Installation completed! ===" -ForegroundColor Green
    Write-Host "grpcurl is installed at: $(Resolve-Path $grpcurlExe)" -ForegroundColor Cyan
    Write-Host "`nTo use grpcurl from anywhere, you can:" -ForegroundColor Yellow
    Write-Host "1. Use full path: .\grpcurl\grpcurl.exe" -ForegroundColor White
    Write-Host "2. Or add to PATH environment variable" -ForegroundColor White
    Write-Host "3. Or copy grpcurl.exe to a directory already in PATH" -ForegroundColor White
    
    # Clean up
    Remove-Item $outputFile -ErrorAction SilentlyContinue
    
} else {
    Write-Host "grpcurl.exe not found after extraction!" -ForegroundColor Red
    Write-Host "Contents of grpcurl directory:" -ForegroundColor Yellow
    Get-ChildItem $grpcurlDir -Recurse
    exit 1
}

Write-Host "`nYou can now test your gRPC service with:" -ForegroundColor Green
Write-Host ".\grpcurl\grpcurl.exe -plaintext localhost:6565 list" -ForegroundColor Cyan 