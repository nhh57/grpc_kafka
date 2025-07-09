---
title: "Task 1.3: Triển khai lớp Domain và Repository cho Product"
type: "task"
status: "planned"
created: "2024-07-09T08:35:00"
updated: "2024-07-09T09:15:00"
id: "TASK-103"
priority: "medium"
dependencies: ["TASK-102"]
tags: ["domain", "entity", "repository", "jpa", "product-service"]
---

## Description
Nhiệm vụ này tập trung vào việc tạo ra các thành phần cốt lõi của lớp domain và persistence cho `Product Service`. Điều này bao gồm việc định nghĩa `Product` entity để ánh xạ với bảng trong cơ sở dữ liệu và `ProductRepository` để thực hiện các thao tác CRUD.

## Objectives
- Tạo một `Product` entity có các trường khớp với định nghĩa trong `product.proto`.
- Triển khai `ProductRepository` sử dụng Spring Data JPA.
- Cấu hình kết nối cơ sở dữ liệu (sử dụng H2 cho môi trường dev/test).

## Checklist

### Nhóm: JPA Entity
- [ ] **Bước 1:** Tạo package `com.example.productservice.domain`.
- [ ] **Bước 2:** Tạo lớp `Product` entity.
    - **Vị trí:** `product-service/src/main/java/com/example/productservice/domain/Product.java`
    - **Notes:** Sử dụng các annotation của JPA (`@Entity`, `@Id`, `@GeneratedValue`) và Lombok để giảm code boilerplate.
    - **Code:**
      ```java
      package com.example.productservice.domain;

      import jakarta.persistence.Entity;
      import jakarta.persistence.GeneratedValue;
      import jakarta.persistence.GenerationType;
      import jakarta.persistence.Id;
      import lombok.AllArgsConstructor;
      import lombok.Builder;
      import lombok.Data;
      import lombok.NoArgsConstructor;

      @Entity
      @Data
      @NoArgsConstructor
      @AllArgsConstructor
      @Builder
      public class Product {
          @Id
          @GeneratedValue(strategy = GenerationType.UUID)
          private String id;
          private String name;
          private String description;
          private double price;
          private int quantity;
      }
      ```

### Nhóm: Spring Data Repository
- [ ] **Bước 3:** Tạo package `com.example.productservice.repository`.
- [ ] **Bước 4:** Tạo interface `ProductRepository`.
    - **Vị trí:** `product-service/src/main/java/com/example/productservice/repository/ProductRepository.java`
    - **Notes:** Kế thừa từ `JpaRepository` sẽ cung cấp sẵn các phương thức CRUD cơ bản.
    - **Code:**
      ```java
      package com.example.productservice.repository;

      import com.example.productservice.domain.Product;
      import org.springframework.data.jpa.repository.JpaRepository;
      import org.springframework.stereotype.Repository;

      @Repository
      public interface ProductRepository extends JpaRepository<Product, String> {
      }
      ```

### Nhóm: Database Configuration
- [ ] **Bước 5:** Thêm dependency cho H2 Database.
    - **Vị trí:** `product-service/pom.xml` trong thẻ `<dependencies>`.
    - **Notes:** H2 là một CSDL in-memory, rất tiện cho việc phát triển và kiểm thử.
    - **Code:**
      ```xml
      <dependency>
          <groupId>com.h2database</groupId>
          <artifactId>h2</artifactId>
          <scope>runtime</scope>
      </dependency>
      ```

- [ ] **Bước 6:** Cấu hình `application.properties` để kết nối tới H2.
    - **Vị trí:** `product-service/src/main/resources/application.properties`
    - **Code:**
      ```properties
      spring.datasource.url=jdbc:h2:mem:productdb
      spring.datasource.driverClassName=org.h2.Driver
      spring.datasource.username=sa
      spring.datasource.password=
      spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
      spring.jpa.hibernate.ddl-auto=update
      spring.h2.console.enabled=true
      ```

### Nhóm: Verification
- [ ] **Bước 7:** Chạy ứng dụng `ProductServiceApplication`.
    - **Mục tiêu:** Xác minh rằng ứng dụng có thể khởi động, kết nối tới CSDL H2 và tự động tạo bảng `product` mà không gặp lỗi.
    - **Kiểm tra:** Console log không có lỗi `SQLGrammarException` hay `BeanCreationException`. Truy cập H2 console tại `http://localhost:8080/h2-console` để xem bảng đã được tạo.

## Key Considerations
- **`@GeneratedValue(strategy = GenerationType.UUID)`:** Sử dụng UUID làm khóa chính là một lựa chọn tốt cho các hệ thống phân tán vì nó giảm khả năng xung đột ID khi scale.
- **`spring.jpa.hibernate.ddl-auto=update`:** Chế độ `update` tự động cập nhật schema CSDL khi entity thay đổi. Rất tiện lợi cho môi trường dev, nhưng **tuyệt đối không** dùng cho production. Ở production, nên dùng các công cụ migration như Flyway hoặc Liquibase.

## Current Status
- **Not Started** 