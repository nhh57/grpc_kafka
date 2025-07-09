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
- [x] Mở folder: `common-protos/src/main/proto/` (Đã hoàn thành)
    - **Notes:** Đảm bảo folder tồn tại, nếu chưa có thì tạo mới.
- [x] Tạo mới hoặc cập nhật file `product.proto`. (Đã hoàn thành)
    - **Notes:** Nếu đã có, backup file cũ trước khi sửa.
- [x] Định nghĩa message `GetProductRequest` (chứa productId, version). (Đã hoàn thành)
    - **Best practice:** Đặt tên field rõ ràng, dùng kiểu string cho UUID.
    - **Lỗi thường gặp:** Thiếu field, sai kiểu dữ liệu.
- [x] Định nghĩa message `ValidatePriceRequest` (chứa productId, price, version). (Đã hoàn thành)
    - **Best practice:** Dùng double cho price, string cho UUID.
- [x] Định nghĩa message `ProductInfo` (chứa id, name, description, price, quantity, version). (Đã hoàn thành)
    - **Best practice:** Đảm bảo mapping đủ trường với entity Product.
- [x] Định nghĩa service `ProductService` với các RPC: (Đã hoàn thành)
    - [x] `rpc GetProductInfo(GetProductRequest) returns (ProductInfo)`
    - [x] `rpc ValidatePriceWithVersion(ValidatePriceRequest) returns (ProductInfo)`
    - **Best practice:** Đặt tên service, RPC rõ ràng, tuân thủ PascalCase.
    - **Lỗi thường gặp:** Trùng tên, sai kiểu trả về, thiếu trường.
- [x] Lưu file proto, kiểm tra syntax (có thể dùng plugin hoặc lệnh `protoc`). (Đã hoàn thành)
- [x] Ghi chú lại mọi thay đổi vào file `CHANGELOG.md` hoặc nhật ký cá nhân. (Đã hoàn thành)

### Nhóm: Generate code Java từ proto
- [x] Mở terminal tại thư mục `common-protos/`. (Đã hoàn thành)
- [x] Chạy lệnh `mvn clean install` để generate code Java từ proto. (Đã hoàn thành)
    - **Notes:** Kiểm tra cấu hình plugin protobuf trong `pom.xml` nếu lỗi.
- [x] Kiểm tra thư mục `target/generated-sources/protobuf/java/` đã sinh ra các file Java tương ứng. (Đã hoàn thành)
    - **Best practice:** Không sửa tay file sinh tự động.
    - **Lỗi thường gặp:** Thiếu file, lỗi version plugin, lỗi syntax proto.
- [x] Ghi chú lại mọi thay đổi vào file `CHANGELOG.md` hoặc nhật ký cá nhân. (Đã hoàn thành)

### Nhóm: Tạo service implement các RPC
- [ ] Mở folder: `product-service/src/main/java/com/example/productservice/grpc/`
    - **Notes:** Đảm bảo folder tồn tại, nếu chưa có thì tạo mới.
- [ ] Tạo file mới `ProductGrpcServiceImpl.java`.
    - **Notes:** Đặt tên file đúng chuẩn PascalCase, đúng package.
- [ ] Implement method `GetProductInfo` (mapping từ entity Product sang message ProductInfo).
    - **Best practice:** Mapping đúng kiểu dữ liệu, đủ trường.
    - **Lỗi thường gặp:** Mapping sai kiểu, thiếu trường, lỗi null.
- [ ] Implement method `ValidatePriceWithVersion` (kiểm tra version, mapping trả về ProductInfo).
    - **Best practice:** Kiểm tra version, trả về lỗi nếu không khớp.
- [ ] Lưu file, kiểm tra syntax lỗi compile (IDE hoặc `mvn compile`).
- [ ] Ghi chú lại mọi thay đổi vào file `CHANGELOG.md` hoặc nhật ký cá nhân.

### Nhóm: Kiểm tra lại toàn bộ API gRPC
- [ ] Chạy lại `mvn clean install` ở cả `common-protos/` và `product-service/` xác nhận không lỗi build.
- [ ] Viết test đơn giản gọi thử các RPC (có thể dùng grpcurl hoặc test tự động).
- [ ] Ghi chú lại mọi thay đổi, chụp màn hình log nếu cần.
- [ ] Xác nhận lại với team về hợp đồng API, message, service.

## Key Considerations
- Đảm bảo file proto đồng bộ giữa các service.
- Kiểm tra lại code generate, tránh sửa tay file sinh tự động.

## Discussion
- API contract-first giúp giảm lỗi tích hợp giữa các service.
- Nên review kỹ file proto trước khi generate code.

## Current Status
- Chưa thực hiện (planned) 