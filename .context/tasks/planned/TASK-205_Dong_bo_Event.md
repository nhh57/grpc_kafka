---
title: "TASK-205: Dong bo & Event"
type: "task"
status: "planned"
created: "2025-07-09T10:56:00"
updated: "2025-07-09T10:56:00"
id: "TASK-205"
priority: "high"
dependencies: ["TASK-204"]
tags: ["event", "kafka", "pubsub", "product-service"]
---

## Description
Triển khai đồng bộ dữ liệu và event-driven cho Product Service, đảm bảo cache luôn nhất quán giữa các instance.

## Objectives
- Lắng nghe event Kafka, update cache.
- Lắng nghe Redis Pub/Sub để invalidate L1 cache.
- Publish event khi update tồn kho.

## Checklist (Phân rã cực chi tiết)

### Nhóm: Lắng nghe event Kafka
- [ ] Mở folder: `product-service/src/main/java/com/example/productservice/event/`
- [ ] Tạo class consumer Kafka: `InventoryUpdatedListener.java`.
- [ ] Lắng nghe topic `inventory-updated`, parse message.
- [ ] Update L2 cache khi nhận event.
    - **Best practice:** Xử lý message idempotent, log event.
    - **Lỗi thường gặp:** Lỗi parse message, consumer group sai, duplicate event.

### Nhóm: Lắng nghe Redis Pub/Sub
- [ ] Tạo class listener Redis Pub/Sub: `RedisCacheInvalidationListener.java` tại `productservice/cache/`.
- [ ] Lắng nghe channel invalidate, gọi invalidate L1 cache.
    - **Best practice:** Đảm bảo chỉ invalidate đúng key, log event.
    - **Lỗi thường gặp:** Miss event, invalidate nhầm key.

### Nhóm: Publish event khi update tồn kho
- [ ] Tạo publisher Kafka: `InventoryUpdatePublisher.java` tại `productservice/event/`.
- [ ] Publish event khi update tồn kho thành công.
    - **Best practice:** Đảm bảo event gửi đúng format, log event gửi đi.
    - **Lỗi thường gặp:** Không gửi được event, thiếu thông tin message.

## Key Considerations
- Đảm bảo event idempotent, không gây race condition.
- Log đầy đủ event để dễ debug.

## Discussion
- Event-driven giúp đồng bộ cache nhanh, giảm độ trễ, nhưng cần kiểm soát duplicate và mất event.

## Current Status
- Chưa thực hiện (planned) 