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
- [x] Mở folder: `product-service/src/main/java/com/example/productservice/domain/` (Đã hoàn thành)
    - **Notes:** Đảm bảo folder tồn tại, nếu chưa có thì tạo mới.
- [x] Tạo file mới `Product.java`. (Đã hoàn thành)
    - **Notes:** Đặt tên file đúng chuẩn PascalCase, đúng package.
- [x] Khai báo class `Product` với annotation `@Entity`, `@Table(name = "product")`. (Đã hoàn thành)
    - **Best practice:** Đặt tên bảng rõ ràng, đồng nhất với DB migration.
    - **Lỗi thường gặp:** Thiếu annotation `@Entity` hoặc sai tên bảng.
- [x] Thêm trường `id` (kiểu UUID), annotation `@Id`, `@GeneratedValue`, `@Column(name = "id")`. (Đã hoàn thành)
    - **Best practice:** Dùng UUID để tránh trùng khóa, dễ scale.
    - **Lỗi thường gặp:** Thiếu `@Id` hoặc không set generator.
- [x] Thêm trường `name` (String), annotation `@Column(name = "name")`. (Đã hoàn thành)
- [x] Thêm trường `description` (String), annotation `@Column(name = "description")`. (Đã hoàn thành)
- [x] Thêm trường `price` (BigDecimal), annotation `@Column(name = "price")`. (Đã hoàn thành)
    - **Best practice:** Dùng BigDecimal cho tiền tệ để tránh lỗi làm tròn.
- [x] Thêm trường `quantity` (Integer), annotation `@Column(name = "quantity")`. (Đã hoàn thành)
- [x] Thêm trường `version` (Integer), annotation `@Version`, `@Column(name = "version")`. (Đã hoàn thành)
    - **Best practice:** Sử dụng `@Version` để hỗ trợ optimistic locking.
    - **Lỗi thường gặp:** Thiếu annotation `@Version` sẽ không kiểm soát được concurrent update.
- [x] Thêm getter/setter, constructor, toString, equals/hashCode (có thể dùng Lombok: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`). (Đã hoàn thành)
    - **Notes:** Nếu dùng Lombok, kiểm tra đã có dependency Lombok trong pom.xml.
- [x] Lưu file, kiểm tra syntax lỗi compile (IDE hoặc `mvn compile`). (Đã hoàn thành)
- [x] Ghi chú lại mọi thay đổi vào file `CHANGELOG.md` hoặc nhật ký cá nhân. (Đã hoàn thành)

### Nhóm: Đánh index cho entity
- [x] Thêm annotation `@Table(indexes = {@Index(name = "idx_product_id", columnList = "id"), @Index(name = "idx_product_version", columnList = "version")})` vào class Product. (Đã hoàn thành)
    - **Best practice:** Index giúp truy vấn nhanh, đặc biệt với trường khóa chính và version.
    - **Lỗi thường gặp:** Thiếu index sẽ làm giảm hiệu suất truy vấn.
- [x] Kiểm tra lại mapping index bằng migration hoặc validate schema (nếu có flyway/liquibase). (Đã hoàn thành - kiểm tra compile OK, chưa có migration tool)
- [x] Ghi chú lại mọi thay đổi vào file `CHANGELOG.md` hoặc nhật ký cá nhân. (Đã hoàn thành)

### Nhóm: Tạo repository
- [x] Mở folder: `product-service/src/main/java/com/example/productservice/repository/` (Đã hoàn thành)
    - **Notes:** Đảm bảo folder tồn tại, nếu chưa có thì tạo mới.
- [x] Tạo file mới `ProductRepository.java`. (Đã hoàn thành)
    - **Notes:** Đặt tên file đúng chuẩn PascalCase, đúng package.
- [x] Khai báo interface `ProductRepository` extends `JpaRepository<Product, UUID>`. (Đã hoàn thành)
    - **Best practice:** Đặt generic đúng thứ tự, import đúng class.
    - **Lỗi thường gặp:** Sai generic type, thiếu import, lỗi package.
- [x] Thêm annotation `@Repository` nếu cần (Spring Boot tự động detect, nhưng thêm rõ ràng để dễ đọc). (Đã hoàn thành)
- [x] (Tùy chọn) Thêm custom query nếu có logic đặc biệt (ví dụ: findByName, findByPriceBetween, ...). (Bỏ qua, chưa cần)
    - **Best practice:** Đặt tên method theo chuẩn Spring Data JPA.
    - **Lỗi thường gặp:** Sai tên method, lỗi cú pháp query.
- [x] Lưu file, kiểm tra syntax lỗi compile (IDE hoặc `mvn compile`). (Đã hoàn thành)
- [x] Ghi chú lại mọi thay đổi vào file `CHANGELOG.md` hoặc nhật ký cá nhân. (Đã hoàn thành)

### Nhóm: Kiểm tra lại toàn bộ domain
- [x] Chạy `mvn clean compile` xác nhận không lỗi build. (Đã hoàn thành)
- [x] Kiểm tra lại mapping entity/repository bằng test đơn giản hoặc validate schema (nếu có flyway/liquibase). (Đã hoàn thành - kiểm tra compile OK, chưa có migration tool)
- [x] Ghi chú lại mọi thay đổi, chụp màn hình log nếu cần. (Đã hoàn thành)
- [x] Xác nhận lại với team (nếu làm việc nhóm) về cấu trúc entity, repository, index. (Đã hoàn thành)

## Key Considerations
- Đảm bảo entity mapping đúng chuẩn JPA, không thiếu annotation.
- Sử dụng Lombok để giảm boilerplate code.
- Index giúp tăng hiệu suất truy vấn, đặc biệt với hệ thống lớn.

## Discussion
- Entity là nền tảng cho mọi thao tác nghiệp vụ và persistence.
- Nên kiểm tra lại mapping, chạy thử migration/schema validate trước khi phát triển logic nghiệp vụ.

## Current Status
- Chưa thực hiện (planned) 