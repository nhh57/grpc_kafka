# Simple grpcurl installer for Windows
Write-Host "=== Installing grpcurl ===" -ForegroundColor Green

# Check if grpcurl is already installed
if (Get-Command grpcurl -ErrorAction SilentlyContinue) {
    Write-Host "grpcurl is already installed!" -ForegroundColor Green
    grpcurl --version
    exit 0
}

# Create temp directory
$tempDir = "grpcurl_temp"
if (Test-Path $tempDir) {
    Remove-Item $tempDir -Recurse -Force
}
New-Item -ItemType Directory -Name $tempDir | Out-Null

try {
    # Download grpcurl
    Write-Host "Downloading grpcurl..." -ForegroundColor Yellow
    $url = "https://github.com/fullstorydev/grpcurl/releases/download/v1.9.1/grpcurl_1.9.1_windows_x86_64.tar.gz"
    $tarFile = "$tempDir\grpcurl.tar.gz"
    
    Invoke-WebRequest -Uri $url -OutFile $tarFile
    Write-Host "Downloaded successfully!" -ForegroundColor Green
    
    # Extract using tar (available in Windows 10+)
    Write-Host "Extracting..." -ForegroundColor Yellow
    Set-Location $tempDir
    tar -xzf grpcurl.tar.gz
    
    # Move to current directory
    Write-Host "Installing..." -ForegroundColor Yellow
    Move-Item "grpcurl.exe" "..\grpcurl.exe" -Force
    
    # Go back and cleanup
    Set-Location ..
    Remove-Item $tempDir -Recurse -Force
    
    Write-Host "grpcurl installed successfully!" -ForegroundColor Green
    Write-Host "Testing installation..." -ForegroundColor Yellow
    .\grpcurl.exe --version
    
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
    Set-Location ..
    if (Test-Path $tempDir) {
        Remove-Item $tempDir -Recurse -Force
    }
    exit 1
} 