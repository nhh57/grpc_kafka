---
title: "TASK-202: Thiet ke & Trien khai Domain"
type: "task"
status: "planned"
created: "2025-07-09T10:50:00"
updated: "2025-07-09T10:50:00"
id: "TASK-202"
priority: "high"
dependencies: ["TASK-201"]
tags: ["domain", "entity", "repository", "product-service"]
---

## Description
Thiết kế và triển khai domain model cho Product Service, đảm bảo chuẩn hóa entity, repository, và các annotation cần thiết cho concurrency.

## Objectives
- Tạo entity Product với đầy đủ trường và annotation.
- Đảm bảo index, version, và repository đúng chuẩn JPA.

## Checklist (Phân rã cực chi tiết)

### Nhóm: Tạo entity Product
- [ ] Mở file/folder: `product-service/src/main/java/com/example/productservice/domain/`
- [ ] Tạo file mới `Product.java`.
- [ ] Khai báo class `Product` với annotation `@Entity`, `@Table(name = "product")`.
- [ ] Thêm trường `id` (UUID, annotation `@Id`, `@Column`, `@GeneratedValue`).
- [ ] Thêm trường `name` (`@Column`).
- [ ] Thêm trường `description` (`@Column`).
- [ ] Thêm trường `price` (`@Column`).
- [ ] Thêm trường `quantity` (`@Column`).
- [ ] Thêm trường `version` (`@Version`, `@Column`).
    - **Best practice:** Sử dụng `@Version` để hỗ trợ optimistic locking.
    - **Lỗi thường gặp:** Thiếu annotation `@Version` sẽ không kiểm soát được concurrent update.
- [ ] Thêm getter/setter, constructor, toString, equals/hashCode (có thể dùng Lombok).
    - **Notes:** Nếu dùng Lombok, thêm annotation `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`.

### Nhóm: Đánh index
- [ ] Thêm annotation `@Index` cho trường `id` và `version` nếu dùng JPA 2.1+ hoặc cấu hình index trong migration DB.
    - **Best practice:** Index giúp truy vấn nhanh, đặc biệt với trường khóa chính và version.
    - **Lỗi thường gặp:** Thiếu index sẽ làm giảm hiệu suất truy vấn.

### Nhóm: Tạo repository
- [ ] Mở folder: `product-service/src/main/java/com/example/productservice/repository/`
- [ ] Tạo file mới `ProductRepository.java`.
- [ ] Khai báo interface `ProductRepository` extends `JpaRepository<Product, UUID>`.
- [ ] Thêm annotation `@Repository` nếu cần.
- [ ] (Tùy chọn) Thêm custom query nếu có logic đặc biệt.
    - **Best practice:** Đặt tên method theo chuẩn Spring Data JPA.
    - **Lỗi thường gặp:** Sai generic type, thiếu annotation, lỗi import.

## Key Considerations
- Đảm bảo entity mapping đúng chuẩn JPA, không thiếu annotation.
- Sử dụng Lombok để giảm boilerplate code.
- Index giúp tăng hiệu suất truy vấn, đặc biệt với hệ thống lớn.

## Discussion
- Entity là nền tảng cho mọi thao tác nghiệp vụ và persistence.
- Nên kiểm tra lại mapping, chạy thử migration/schema validate trước khi phát triển logic nghiệp vụ.

## Current Status
- Chưa thực hiện (planned) 