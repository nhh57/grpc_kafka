---
title: "TASK-208: Kiem thu"
type: "task"
status: "planned"
created: "2025-07-09T10:59:00"
updated: "2025-07-09T10:59:00"
id: "TASK-208"
priority: "high"
dependencies: ["TASK-202", "TASK-204", "TASK-205", "TASK-206"]
tags: ["test", "unit-test", "integration-test", "performance-test", "product-service"]
---

## Description
Viết kiểm thử đơn vị, kiểm thử tích hợp và kiểm thử hiệu năng cho Product Service, đảm bảo chất lượng và độ tin cậy của hệ thống.

## Objectives
- Viết unit test cho service, cache logic, lock, event handler.
- Viết integration test cho full flow gRPC → cache → DB.
- Viết performance test đo throughput, latency, cache hit rate, lock contention.

## Checklist (Phân rã cực chi tiết)

### Nhóm: Unit Test
- [ ] Mở folder: `product-service/src/test/java/com/example/productservice/`
- [ ] Viết unit test cho service chính (ProductService, ProductGrpcServiceImpl).
- [ ] Viết unit test cho cache logic (L1, L2).
- [ ] Viết unit test cho lock (mock Redisson).
- [ ] Viết unit test cho event handler (mock Redis/Kafka).
    - **Best practice:** Test cả case thành công và thất bại, mock external dependency.
    - **Lỗi thường gặp:** Không cover hết case, test phụ thuộc vào môi trường ngoài.

### Nhóm: Integration Test
- [ ] Viết integration test cho full flow gRPC → cache → DB.
- [ ] Test cache hit/miss, invalidation, concurrent access.
    - **Best practice:** Dùng testcontainer hoặc embedded Redis/Kafka để test tích hợp.
    - **Lỗi thường gặp:** Test fail do môi trường, thiếu setup/teardown.

### Nhóm: Performance Test
- [ ] Viết performance test đo throughput, latency.
- [ ] Đo cache hit rate, lock contention.
    - **Best practice:** Đặt ngưỡng performance hợp lý, log kết quả test.
    - **Lỗi thường gặp:** Không đo được chính xác do thiếu tool hoặc môi trường test yếu.

## Key Considerations
- Test kỹ các case concurrent, cache invalidation, event.
- Đảm bảo test độc lập, có thể chạy lặp lại nhiều lần.

## Discussion
- Kiểm thử là bắt buộc để đảm bảo chất lượng hệ thống, đặc biệt với hệ thống phân tán.

## Current Status
- Chưa thực hiện (planned) 