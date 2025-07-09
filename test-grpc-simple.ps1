# Simple gRPC test script
Write-Host "=== Testing gRPC Service ===" -ForegroundColor Green

$grpcurl = "C:\Users\hainh\go\bin\grpcurl.exe"

# Test 1: Test GetProductInfo with ID=1
Write-Host "`nTest 1: Testing GetProductInfo with ID=1" -ForegroundColor Yellow
$result = & $grpcurl -plaintext -import-path "common-protos/src/main/proto" -proto "product.proto" -d '{"product_id": "1"}' localhost:6565 product.ProductService/GetProductInfo

if ($LASTEXITCODE -eq 0) {
    Write-Host "Success: Test passed!" -ForegroundColor Green
    Write-Host "Response: $result"
} else {
    Write-Host "Error: Test failed!" -ForegroundColor Red
    Write-Host "Error details: $result"
}

# Test 2: Test with non-existent ID
Write-Host "`nTest 2: Testing GetProductInfo with non-existent ID=999" -ForegroundColor Yellow
$result2 = & $grpcurl -plaintext -import-path "common-protos/src/main/proto" -proto "product.proto" -d '{"product_id": "999"}' localhost:6565 product.ProductService/GetProductInfo

if ($LASTEXITCODE -eq 0) {
    Write-Host "Response: $result2"
} else {
    Write-Host "Expected error (product not found): $result2" -ForegroundColor Yellow
}

Write-Host "`n=== Test Complete ===" -ForegroundColor Green 