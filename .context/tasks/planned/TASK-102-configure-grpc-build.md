---
title: "Task 1.2: Cấu hình product-service để build code gRPC"
type: "task"
status: "planned"
created: "2024-07-09T08:35:00"
updated: "2024-07-09T09:15:00"
id: "TASK-102"
priority: "high"
dependencies: ["TASK-101"]
tags: ["grpc", "build", "maven", "product-service"]
---

## Description
Sau khi project `common-protos` đã được thiết lập, nhiệm vụ này tập trung vào việc cấu hình `product-service` để có thể sử dụng các "hợp đồng" gRPC đã định nghĩa. Service cần được trang bị khả năng tự động sinh code client-side và server-side từ artifact của `common-protos`.

## Objectives
- Tích hợp dependency `common-protos` vào `product-service`.
- Cấu hình `product-service` để sử dụng các thư viện gRPC và Spring Boot starter cho gRPC.
- Đảm bảo project build thành công mà không có lỗi liên quan đến gRPC.

## Checklist

### Nhóm: Maven Dependencies
- [ ] **Bước 1:** Thêm dependency `common-protos` vào `product-service/pom.xml`.
    - **Vị trí:** `product-service/pom.xml` trong thẻ `<dependencies>`.
    - **Code:**
      ```xml
      <dependency>
          <groupId>com.example</groupId>
          <artifactId>common-protos</artifactId>
          <version>1.0-SNAPSHOT</version>
      </dependency>
      ```

- [ ] **Bước 2:** Thêm gRPC starter của LogNet vào `product-service/pom.xml`.
    - **Vị trí:** `product-service/pom.xml` trong thẻ `<dependencies>`.
    - **Notes:** Starter này giúp tự động cấu hình gRPC server trong Spring Boot.
    - **Code:**
      ```xml
      <dependency>
          <groupId>io.github.lognet</groupId>
          <artifactId>grpc-spring-boot-starter</artifactId>
          <version>5.1.5</version>
      </dependency>
      ```

- [ ] **Bước 3:** Thêm các dependency gRPC cơ bản.
    - **Vị trí:** `product-service/pom.xml` trong thẻ `<dependencies>`.
    - **Notes:** Mặc dù `common-protos` có chúng, việc khai báo ở đây đảm bảo tính rõ ràng.
    - **Code:**
      ```xml
      <dependency>
          <groupId>io.grpc</groupId>
          <artifactId>grpc-stub</artifactId>
          <version>${grpc.version}</version> <!-- Đảm bảo property grpc.version đã có ở parent pom -->
      </dependency>
      <dependency>
          <groupId>io.grpc</groupId>
          <artifactId>grpc-protobuf</artifactId>
          <version>${grpc.version}</version> <!-- Đảm bảo property grpc.version đã có ở parent pom -->
      </dependency>
      ```

### Nhóm: Verification
- [ ] **Bước 4:** Chạy `mvn clean install` trên `product-service`.
    - **Vị trí (Terminal):** `grpc_kafka/product-service`
    - **Lệnh:** `mvn clean install`
    - **Mục tiêu:** Xác minh rằng `product-service` có thể build thành công sau khi thêm các dependency mới và có thể truy cập được các lớp Java đã được sinh ra từ `common-protos`.
    - **Kiểm tra:** Build phải thành công (BUILD SUCCESS) và không có lỗi `ClassNotFoundException` liên quan đến các lớp gRPC.

## Key Considerations
- **`grpc-spring-boot-starter`:** Thư viện này của LogNet là một lựa chọn phổ biến để tích hợp gRPC với Spring Boot. Nó sử dụng annotation (`@GRpcService`) để tự động phát hiện và đăng ký các gRPC services, giúp giảm đáng kể việc cấu hình thủ công.
- **Dependency Scope:** Tất cả các dependency gRPC nên có scope là `compile`.

## Discussion
- Việc lựa chọn một starter (như của LogNet) thay vì cấu hình thủ công giúp tăng tốc độ phát triển và giảm khả năng mắc lỗi. Nó cũng tuân thủ nguyên tắc "Convention over Configuration" của Spring.

## Current Status
- **Not Started** 