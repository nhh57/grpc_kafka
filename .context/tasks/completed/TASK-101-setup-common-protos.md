---
title: "Task 1.1: Thiết lập project common-protos và định nghĩa product.proto"
type: "task"
status: "completed"
created: "2024-07-09T08:35:00"
updated: "2025-07-09T10:14:09"
id: "TASK-101"
priority: "high"
dependencies: []
tags: ["grpc", "protobuf", "setup", "product-service"]
---

## Description
Nhiệm vụ này bao gồm việc tạo ra một project Maven riêng biệt (`common-protos`) để quản lý tập trung tất cả các file định nghĩa Protobuf (`.proto`). Việc này đảm bảo tính nhất quán của API trên toàn bộ hệ thống microservice.

## Objectives
- Tạo một project Maven có thể đóng gói và chia sẻ các file `.proto` và code Java được sinh ra.
- Định nghĩa `ProductService` và các message liên quan trong file `product.proto`.

## Checklist

### Nhóm: Project Setup & Maven Configuration
- [x] **Bước 1:** Tạo thư mục cho module `common-protos` tại thư mục gốc của dự án. ✅
    - **Vị trí:** `grpc_kafka/common-protos`
    - **Completed:** 2025-07-09T10:01:00

- [x] **Bước 2:** Thêm `common-protos` như một module vào file `pom.xml` ở thư mục gốc (`grpc_kafka/pom.xml`). ✅
    - **Vị trí:** `pom.xml`
    - **Completed:** 2025-07-09T10:02:00
    - **Result:** Module đã được thêm vào đầu danh sách modules

- [x] **Bước 3:** Tạo file `pom.xml` cho module `common-protos`. ✅
    - **Vị trí:** `common-protos/pom.xml`
    - **Completed:** 2025-07-09T10:05:00
    - **Notes:** Đã cấu hình thành công `protobuf-maven-plugin` và `os-maven-plugin`. Loại bỏ Spring Boot parent để tránh conflict.
    - **Final Configuration:**
      - `protobuf.version`: 3.25.1
      - `grpc.version`: 1.60.0
      - Java version: 21
      - Packaging: jar (không phải executable jar)

### Nhóm: Protobuf Definition
- [x] **Bước 4:** Tạo cấu trúc thư mục cho file proto. ✅
    - **Vị trí:** `common-protos/src/main/proto`
    - **Completed:** 2025-07-09T10:01:00

- [x] **Bước 5:** Tạo file `product.proto`. ✅
    - **Vị trí:** `common-protos/src/main/proto/product.proto`
    - **Completed:** 2025-07-09T10:03:00
    - **Notes:** File định nghĩa "hợp đồng" (contract) cho ProductService với method GetProductInfo.
    - **Generated Classes:**
      - `GetProductRequest.java` (18,446 bytes)
      - `ProductInfo.java` (29,525 bytes)  
      - `ProductServiceGrpc.java` (11,501 bytes)

### Nhóm: Verification
- [x] **Bước 6:** Chạy `mvn clean install` từ thư mục gốc. ✅
    - **Completed:** 2025-07-09T10:10:51
    - **Result:** BUILD SUCCESS
    - **Generated Files Verified:**
      - `common-protos/target/generated-sources/protobuf/java/com/example/product/grpc/`
        - GetProductRequest.java
        - GetProductRequestOrBuilder.java  
        - ProductInfo.java
        - ProductInfoOrBuilder.java
        - ProductProto.java
      - `common-protos/target/generated-sources/protobuf/grpc-java/com/example/product/grpc/`
        - ProductServiceGrpc.java
    - **JAR Created:** `common-protos-0.0.1-SNAPSHOT.jar`
    - **Local Repository:** Successfully installed to Maven local repository

## Key Considerations
- **Centralized Protos:** Giữ tất cả các file `.proto` ở một nơi giúp tránh xung đột phiên bản và đảm bảo tất cả các microservices đều sử dụng cùng một định nghĩa API.
- **`os-maven-plugin`:** Plugin này rất quan trọng để build gRPC trên các hệ điều hành khác nhau (Windows, Mac, Linux) vì nó tự động phát hiện và sử dụng protoc binary phù hợp.

## Discussion
- Việc tách `common-protos` ra một module riêng là một best practice trong các hệ thống microservice. Nó tuân thủ nguyên tắc DRY (Don't Repeat Yourself) và thúc đẩy thiết kế API-first.

## Current Status
- **Completed Successfully** ✅ (2025-07-09T10:11:22)

## Implementation Summary
- ✅ Tạo thành công module `common-protos` với cấu trúc Maven chuẩn
- ✅ Cấu hình `protobuf-maven-plugin` và `os-maven-plugin` để build gRPC
- ✅ Định nghĩa `ProductService` và các message trong `product.proto`
- ✅ Build thành công và tạo ra các file Java từ protobuf:
  - `GetProductRequest.java`, `ProductInfo.java`
  - `ProductServiceGrpc.java` (gRPC service stub)
- ✅ Module đã được install vào local Maven repository

## Files Created
- `common-protos/pom.xml` - Maven configuration
- `common-protos/src/main/proto/product.proto` - Protobuf definition
- Generated Java files in `target/generated-sources/protobuf/`

## Verification Results
- Maven build: ✅ SUCCESS
- Protobuf compilation: ✅ SUCCESS  
- JAR packaging: ✅ SUCCESS
- Local repository install: ✅ SUCCESS 