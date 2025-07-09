# Test script cho Product gRPC Service
Write-Host "=== Testing Product gRPC Service với grpcurl ===" -ForegroundColor Green

# Kiểm tra grpcurl có sẵn không
if (-not (Get-Command grpcurl -ErrorAction SilentlyContinue)) {
    Write-Host "grpcurl chưa được cài đặt. Đang tải xuống..." -ForegroundColor Yellow
    
    # Tạo thư mục grpcurl nếu chưa có
    if (-not (Test-Path "grpcurl")) {
        New-Item -ItemType Directory -Name "grpcurl"
    }
    
    # Download grpcurl cho Windows
    $downloadUrl = "https://github.com/fullstorydev/grpcurl/releases/download/v1.8.9/grpcurl_1.8.9_windows_x86_64.tar.gz"
    $outputFile = "grpcurl/grpcurl.tar.gz"
    
    Write-Host "Downloading grpcurl từ GitHub..." -ForegroundColor Yellow
    Invoke-WebRequest -Uri $downloadUrl -OutFile $outputFile
    
    # Extract file
    Write-Host "Extracting grpcurl..." -ForegroundColor Yellow
    tar -xzf $outputFile -C grpcurl/
    
    Write-Host "grpcurl đã được tải xuống thành công!" -ForegroundColor Green
}

# Đường dẫn tới grpcurl
$grpcurlPath = if (Get-Command grpcurl -ErrorAction SilentlyContinue) { "grpcurl" } else { "./grpcurl/grpcurl.exe" }

Write-Host "`nKiểm tra gRPC server có đang chạy không..." -ForegroundColor Yellow
$serverRunning = $false
try {
    $result = & $grpcurlPath -plaintext localhost:6565 list 2>$null
    if ($LASTEXITCODE -eq 0) {
        $serverRunning = $true
        Write-Host "✅ gRPC server đang chạy trên localhost:6565" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ gRPC server không chạy hoặc không thể kết nối" -ForegroundColor Red
}

if (-not $serverRunning) {
    Write-Host "`n🚀 Khởi động product-service..." -ForegroundColor Yellow
    Write-Host "Vui lòng chạy lệnh sau trong terminal khác:" -ForegroundColor Cyan
    Write-Host "cd product-service && mvn spring-boot:run" -ForegroundColor White
    Write-Host "`nSau khi service khởi động, chạy lại script này." -ForegroundColor Yellow
    exit 1
}

Write-Host "`n=== Bắt đầu Test Cases ===" -ForegroundColor Green

# Test 1: Liệt kê các services có sẵn
Write-Host "`n1. 📋 Liệt kê các gRPC services:" -ForegroundColor Yellow
& $grpcurlPath -plaintext localhost:6565 list

# Test 2: Mô tả ProductService
Write-Host "`n2. 📖 Mô tả ProductService:" -ForegroundColor Yellow
& $grpcurlPath -plaintext localhost:6565 describe product.ProductService

# Test 3: Test với product ID không tồn tại (sẽ trả về NOT_FOUND)
Write-Host "`n3. ❌ Test với product ID không tồn tại:" -ForegroundColor Yellow
$testRequest1 = @{
    product_id = "non-existent-id"
} | ConvertTo-Json -Compress

Write-Host "Request: $testRequest1" -ForegroundColor Cyan
& $grpcurlPath -plaintext -d $testRequest1 localhost:6565 product.ProductService/GetProductInfo

# Test 4: Test với product ID rỗng
Write-Host "`n4. ⚠️ Test với product ID rỗng:" -ForegroundColor Yellow
$testRequest2 = @{
    product_id = ""
} | ConvertTo-Json -Compress

Write-Host "Request: $testRequest2" -ForegroundColor Cyan
& $grpcurlPath -plaintext -d $testRequest2 localhost:6565 product.ProductService/GetProductInfo

# Test 5: Test với product ID hợp lệ (giả sử có data)
Write-Host "`n5. ✅ Test với product ID hợp lệ (test-123):" -ForegroundColor Yellow
$testRequest3 = @{
    product_id = "test-123"
} | ConvertTo-Json -Compress

Write-Host "Request: $testRequest3" -ForegroundColor Cyan
& $grpcurlPath -plaintext -d $testRequest3 localhost:6565 product.ProductService/GetProductInfo

Write-Host "`n=== Test hoàn thành ===" -ForegroundColor Green
Write-Host "`n💡 Lưu ý:" -ForegroundColor Yellow
Write-Host "- Tất cả requests hiện tại sẽ trả về NOT_FOUND vì chưa có test data trong database" -ForegroundColor White
Write-Host "- Để có data test, cần chạy Task 105 để tạo sample data" -ForegroundColor White
Write-Host "- gRPC service dang hoat dong binh thuong neu ban thay error messages co format dung" -ForegroundColor White 