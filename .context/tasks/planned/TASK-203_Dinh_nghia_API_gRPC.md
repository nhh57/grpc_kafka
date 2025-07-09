---
title: "TASK-203: Dinh nghia API gRPC"
type: "task"
status: "planned"
created: "2025-07-09T10:51:00"
updated: "2025-07-09T10:51:00"
id: "TASK-203"
priority: "high"
dependencies: ["TASK-201"]
tags: ["grpc", "api", "protobuf", "product-service"]
---

## Description
Định nghĩa hợp đồng API gRPC cho Product Service, đảm bảo chuẩn hóa message, service, và generate code đúng quy trình.

## Objectives
- Định nghĩa file proto chuẩn hóa.
- Generate code Java từ proto.
- Tạo service implement các RPC.

## Checklist (Phân rã cực chi tiết)

### Nhóm: Định nghĩa file proto
- [ ] Mở folder: `common-protos/src/main/proto/`
- [ ] Tạo hoặc cập nhật file `product.proto`.
- [ ] Định nghĩa message `GetProductRequest`.
- [ ] Định nghĩa message `ValidatePriceRequest`.
- [ ] Định nghĩa message `ProductInfo`.
- [ ] Định nghĩa service `ProductService` với các RPC: `GetProductInfo`, `ValidatePriceWithVersion`.
    - **Best practice:** Đặt tên message/service rõ ràng, dùng kiểu dữ liệu phù hợp.
    - **Lỗi thường gặp:** Thiếu field, sai kiểu dữ liệu, trùng tên.

### Nhóm: Generate code Java từ proto
- [ ] Chạy lệnh `mvn clean install` tại thư mục `common-protos/`.
- [ ] Kiểm tra thư mục `target/generated-sources/protobuf/java/` đã sinh ra các file Java tương ứng.
    - **Notes:** Nếu lỗi, kiểm tra lại cấu hình plugin protobuf trong `pom.xml`.

### Nhóm: Tạo service implement các RPC
- [ ] Mở folder: `product-service/src/main/java/com/example/productservice/grpc/`
- [ ] Tạo file mới `ProductGrpcServiceImpl.java`.
- [ ] Implement method `GetProductInfo`.
- [ ] Implement method `ValidatePriceWithVersion`.
    - **Best practice:** Mapping đúng giữa entity và message proto.
    - **Lỗi thường gặp:** Mapping sai kiểu dữ liệu, thiếu trường, lỗi import.

## Key Considerations
- Đảm bảo file proto đồng bộ giữa các service.
- Kiểm tra lại code generate, tránh sửa tay file sinh tự động.

## Discussion
- API contract-first giúp giảm lỗi tích hợp giữa các service.
- Nên review kỹ file proto trước khi generate code.

## Current Status
- Chưa thực hiện (planned) 