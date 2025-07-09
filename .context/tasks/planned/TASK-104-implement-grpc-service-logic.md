---
title: "Task 1.4: Triển khai logic cho ProductGrpcService"
type: "task"
status: "planned"
created: "2024-07-09T08:35:00"
updated: "2024-07-09T09:15:00"
id: "TASK-104"
priority: "high"
dependencies: ["TASK-103"]
tags: ["grpc", "service", "logic", "product-service"]
---

## Description
Đây là nhiệm vụ cốt lõi, nơi "hợp đồng" gRPC được hiện thực hóa bằng logic nghiệp vụ. Chúng ta sẽ tạo ra một gRPC service bean, tiêm `ProductRepository` vào, và triển khai phương thức `getProductInfo` để trả về dữ liệu sản phẩm từ cơ sở dữ liệu.

## Objectives
- Tạo một lớp service được Spring Boot nhận diện là một gRPC endpoint.
- Triển khai logic để xử lý `GetProductRequest`, truy vấn CSDL và trả về `ProductInfo`.
- Xử lý các trường hợp ngoại lệ (ví dụ: sản phẩm không tồn tại).

## Checklist

### Nhóm: Service Implementation
- [ ] **Bước 1:** Tạo package `com.example.productservice.grpc`.
- [ ] **Bước 2:** Tạo lớp `ProductGrpcServiceImpl`.
    - **Vị trí:** `product-service/src/main/java/com/example/productservice/grpc/ProductGrpcServiceImpl.java`
    - **Notes:** Lớp này kế thừa từ lớp base được gRPC sinh ra (`ProductServiceImplBase`). Annotation `@GRpcService` của LogNet sẽ tự động đăng ký nó như một gRPC service.
    - **Code:**
      ```java
      package com.example.productservice.grpc;

      import com.example.product.grpc.GetProductRequest;
      import com.example.product.grpc.ProductInfo;
      import com.example.product.grpc.ProductServiceGrpc;
      import com.example.productservice.domain.Product;
      import com.example.productservice.repository.ProductRepository;
      import io.grpc.Status;
      import io.grpc.stub.StreamObserver;
      import lombok.RequiredArgsConstructor;
      import io.github.lognet.grpc.grpcservice.GRpcService;

      @GRpcService
      @RequiredArgsConstructor
      public class ProductGrpcServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

          private final ProductRepository productRepository;

          @Override
          public void getProductInfo(GetProductRequest request, StreamObserver<ProductInfo> responseObserver) {
              String productId = request.getProductId();

              productRepository.findById(productId)
                  .map(this::toProductInfo)
                  .ifPresentOrElse(
                      responseObserver::onNext,
                      () -> responseObserver.onError(Status.NOT_FOUND
                          .withDescription("Product not found with id: " + productId)
                          .asRuntimeException())
                  );
              responseObserver.onCompleted();
          }

          private ProductInfo toProductInfo(Product product) {
              return ProductInfo.newBuilder()
                  .setId(product.getId())
                  .setName(product.getName())
                  .setDescription(product.getDescription())
                  .setPrice(product.getPrice())
                  .setQuantity(product.getQuantity())
                  .build();
          }
      }
      ```

- [ ] **Bước 3:** Thêm Lombok dependency để sử dụng `@RequiredArgsConstructor`.
    - **Vị trí:** `product-service/pom.xml` (nếu chưa có).
    - **Code:**
      ```xml
      <dependency>
          <groupId>org.projectlombok</groupId>
          <artifactId>lombok</artifactId>
          <optional>true</optional>
      </dependency>
      ```

### Nhóm: Verification
- [ ] **Bước 4:** Chạy ứng dụng `ProductServiceApplication`.
- [ ] **Bước 5:** Sử dụng một gRPC client (như `grpcurl` hoặc một client viết bằng Java/Python) để gọi đến service.
    - **Mục tiêu:** Xác minh endpoint `getProductInfo` hoạt động đúng.
    - **Kịch bản 1 (Thành công):** Tạo một sản phẩm trong CSDL (qua H2 console), sau đó dùng client gọi với ID của sản phẩm đó và kiểm tra thông tin trả về.
    - **Kịch bản 2 (Thất bại):** Dùng client gọi với một ID không tồn tại và kiểm tra service trả về lỗi `NOT_FOUND`.

## Key Considerations
- **Error Handling:** Trả về lỗi bằng `responseObserver.onError()` là cách chuẩn trong gRPC. Sử dụng các mã trạng thái tiêu chuẩn (như `Status.NOT_FOUND`) giúp các client xử lý lỗi một cách nhất quán.
- **Mapping (DTO Pattern):** Phương thức `toProductInfo` đóng vai trò như một mapper, chuyển đổi từ JPA entity (`Product`) sang gRPC DTO (`ProductInfo`). Đây là một pattern quan trọng để tách biệt lớp domain và lớp API.
- **Immutability:** Các message của Protobuf sau khi được build (`.build()`) là immutable. Điều này giúp đảm bảo tính toàn vẹn của dữ liệu khi truyền qua các service.

## Current Status
- **Not Started** 