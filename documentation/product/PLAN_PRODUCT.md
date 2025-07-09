# Kế hoạch Triển khai Module: Product Service

## 1. Tổng quan và Mục tiêu

- **Mô tả:** Service này chịu trách nhiệm quản lý toàn bộ vòng đời và thông tin của sản phẩm. Nó là "nguồn sự thật" (source of truth) cho giá, mô tả và trạng thái sản phẩm. Đồng thời, nó được thiết kế để có hiệu suất đọc cực cao thông qua kiến trúc caching phức tạp.
- **Mục tiêu chính:**
    1.  **Cung cấp API ổn định:** Xây dựng các gRPC endpoints để các services khác (như `Order Service`) có thể truy vấn thông tin sản phẩm một cách đáng tin cậy.
    2.  **Hiệu suất cực cao:** Triển khai kiến trúc caching 2 lớp (L1/L2) để đáp ứng lượng truy cập lớn vào các ngày khuyến mãi mà không làm quá tải cơ sở dữ liệu.
    3.  **Đảm bảo tính nhất quán:** Dữ liệu sản phẩm trong cache phải luôn được cập nhật nhất quán với dữ liệu gốc thông qua một luồng xử lý sự kiện bất đồng bộ (sử dụng Kafka và Redis Pub/Sub).

## 2. Thiết kế Kỹ thuật Chi tiết

### 2.1. API gRPC (`product.proto`)

Định nghĩa "hợp đồng" API cho service.

```proto
// protos/product.proto
syntax = "proto3";

package product;

option java_package = "com.example.productservice.grpc";
option java_multiple_files = true;

// Service để quản lý sản phẩm
service ProductService {
  // Lấy thông tin chi tiết của sản phẩm
  rpc GetProductInfo(GetProductRequest) returns (ProductInfo);
  // (Nâng cao) Validate giá và phiên bản để chống race condition
  rpc ValidatePriceWithVersion(ValidatePriceRequest) returns (ProductInfo);
}

message GetProductRequest {
  string productId = 1;
}

message ValidatePriceRequest {
  string productId = 1;
  double expectedPrice = 2;
  int64 version = 3;
}

message ProductInfo {
  string id = 1;
  string name = 2;
  string description = 3;
  double price = 4;
  int64 version = 5; // Dùng cho optimistic locking
}
```

### 2.2. Kiến trúc Caching 2 Lớp

-   **L1 Cache (Local Cache - Guava):** Cache trong bộ nhớ của mỗi instance `Product Service`.
    -   **Ưu điểm:** Tốc độ truy cập nhanh nhất (nano giây), không có độ trễ mạng.
    -   **Nhược điểm:** Dữ liệu không được chia sẻ giữa các instance.
-   **L2 Cache (Distributed Cache - Redis):** Cache phân tán, dùng chung cho tất cả các instance.
    -   **Ưu điểm:** Dữ liệu nhất quán cho toàn bộ service.
    -   **Nhược điểm:** Có độ trễ mạng (mili giây).
-   **Chống Cache Stampede:** Khi một key hết hạn, nhiều request có thể cùng lúc "miss" cache và tấn công vào CSDL.
    -   **Giải pháp:** Sử dụng cơ chế `LoadingCache` của Guava hoặc `Lock` để chỉ một luồng duy nhất được phép đi tiếp và load dữ liệu từ CSDL. Các luồng khác sẽ chờ luồng này hoàn thành và đọc dữ liệu mới từ cache.

### 2.3. Luồng Đồng bộ và Vô hiệu hóa Cache (Kafka & Redis Pub/Sub)

Đây là cơ chế đảm bảo cache luôn tươi mới.
1.  **Cập nhật dữ liệu (DB -> L2):**
    -   `Inventory Service` sau khi cập nhật kho sẽ publish `InventoryUpdatedEvent`.
    -   Một worker trong `Product Service` sẽ lắng nghe sự kiện này và cập nhật dữ liệu mới vào L2 Cache (Redis).
2.  **Vô hiệu hóa L1 Cache (L2 -> L1 Invalidation):**
    -   Ngay sau khi cập nhật L2 Cache, worker sẽ `PUBLISH` một thông điệp nhỏ (chỉ chứa `productId`) vào một Redis Channel.
    -   Tất cả các instance `Product Service` đều `SUBSCRIBE` channel này.
    -   Khi nhận thông điệp, mỗi instance sẽ tự động xóa (invalidate) `productId` tương ứng khỏi L1 Cache (Guava) của chính nó.

## 3. Lộ trình Triển khai (Phân rã Nhiệm vụ)

### Giai đoạn 1: Triển khai gRPC Core (Get It Working First)
*   **Mục tiêu:** Xây dựng chức năng gRPC cơ bản để các service khác có thể hoạt động.

-   [ ] **Nhiệm vụ 1.1:** Thiết lập project `common-protos` và định nghĩa `product.proto`.
-   [ ] **Nhiệm vụ 1.2:** Cấu hình `product-service` để có thể build code gRPC từ file `.proto`.
-   [ ] **Nhiệm vụ 1.3:** Triển khai lớp Domain (Entity `Product`) và Repository (`ProductRepository`).
-   [ ] **Nhiệm vụ 1.4:** Triển khai logic cho `ProductGrpcService` để xử lý các cuộc gọi RPC.
-   [ ] **Nhiệm vụ 1.5:** Viết Unit & Integration Test để xác minh gRPC endpoint hoạt động chính xác.

### Giai đoạn 2: Triển khai Caching (Make It Fast)
*   **Mục tiêu:** Tăng tốc độ phản hồi và giảm tải cho hệ thống.

-   [ ] **Nhiệm vụ 2.1:** Tích hợp Redis và triển khai L2 Cache theo mẫu cache-aside.
-   [ ] **Nhiệm vụ 2.2:** Tích hợp Guava và triển khai L1 Cache, cùng với cơ chế chống cache stampede.
-   [ ] **Nhiệm vụ 2.3:** Tạo Kafka consumer để lắng nghe `InventoryUpdatedEvent` và cập nhật L2 Cache.
-   [ ] **Nhiệm vụ 2.4:** Triển khai cơ chế invalidate L1 Cache thông qua Redis Pub/Sub.
-   [ ] **Nhiệmvụ 2.5:** Viết test cho các kịch bản cache (hit, miss, invalidation). 