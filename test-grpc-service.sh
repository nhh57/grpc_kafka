#!/bin/bash

# Test script cho Product gRPC Service
echo "=== Testing Product gRPC Service với grpcurl ==="

# Kiểm tra grpcurl có sẵn không
if ! command -v grpcurl &> /dev/null; then
    echo "grpcurl chưa được cài đặt. Vui lòng cài đặt:"
    echo ""
    echo "Trên macOS với Homebrew:"
    echo "  brew install grpcurl"
    echo ""
    echo "Trên Linux:"
    echo "  # Download từ GitHub releases"
    echo "  wget https://github.com/fullstorydev/grpcurl/releases/download/v1.8.9/grpcurl_1.8.9_linux_x86_64.tar.gz"
    echo "  tar -xzf grpcurl_1.8.9_linux_x86_64.tar.gz"
    echo "  sudo mv grpcurl /usr/local/bin/"
    echo ""
    exit 1
fi

# Kiểm tra gRPC server có đang chạy không
echo "Kiểm tra gRPC server có đang chạy không..."
if grpcurl -plaintext localhost:6565 list > /dev/null 2>&1; then
    echo "✅ gRPC server đang chạy trên localhost:6565"
else
    echo "❌ gRPC server không chạy hoặc không thể kết nối"
    echo ""
    echo "🚀 Khởi động product-service..."
    echo "Vui lòng chạy lệnh sau trong terminal khác:"
    echo "cd product-service && mvn spring-boot:run"
    echo ""
    echo "Sau khi service khởi động, chạy lại script này."
    exit 1
fi

echo ""
echo "=== Bắt đầu Test Cases ==="

# Test 1: Liệt kê các services có sẵn
echo ""
echo "1. 📋 Liệt kê các gRPC services:"
grpcurl -plaintext localhost:6565 list

# Test 2: Mô tả ProductService
echo ""
echo "2. 📖 Mô tả ProductService:"
grpcurl -plaintext localhost:6565 describe product.ProductService

# Test 3: Test với product ID không tồn tại (sẽ trả về NOT_FOUND)
echo ""
echo "3. ❌ Test với product ID không tồn tại:"
echo 'Request: {"product_id": "non-existent-id"}'
grpcurl -plaintext -d '{"product_id": "non-existent-id"}' localhost:6565 product.ProductService/GetProductInfo

# Test 4: Test với product ID rỗng
echo ""
echo "4. ⚠️ Test với product ID rỗng:"
echo 'Request: {"product_id": ""}'
grpcurl -plaintext -d '{"product_id": ""}' localhost:6565 product.ProductService/GetProductInfo

# Test 5: Test với product ID hợp lệ (giả sử có data)
echo ""
echo "5. ✅ Test với product ID hợp lệ (test-123):"
echo 'Request: {"product_id": "test-123"}'
grpcurl -plaintext -d '{"product_id": "test-123"}' localhost:6565 product.ProductService/GetProductInfo

echo ""
echo "=== Test hoàn thành ==="
echo ""
echo "💡 Lưu ý:"
echo "- Tất cả requests hiện tại sẽ trả về NOT_FOUND vì chưa có test data trong database"
echo "- Để có data test, cần chạy Task 105 để tạo sample data"
echo "- gRPC service đang hoạt động bình thường nếu bạn thấy error messages có format đúng" 