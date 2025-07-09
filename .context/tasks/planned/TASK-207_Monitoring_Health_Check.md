---
title: "TASK-207: Monitoring & Health Check"
type: "task"
status: "planned"
created: "2025-07-09T10:58:00"
updated: "2025-07-09T10:58:00"
id: "TASK-207"
priority: "high"
dependencies: ["TASK-201", "TASK-204"]
tags: ["monitoring", "actuator", "health", "product-service"]
---

## Description
Triển khai monitoring và health check cho Product Service, đảm bảo hệ thống luôn được giám sát và cảnh báo kịp thời.

## Objectives
- Expose actuator endpoint cho cache stats, Redis/Kafka/DB health, lock metrics.
- Thiết lập alert khi cache hit rate thấp, lock contention cao.

## Checklist (Phân rã cực chi tiết)

### Nhóm: Expose actuator endpoint
- [ ] Mở file `application.properties` và xác nhận đã cấu hình actuator (xem lại TASK-201).
- [ ] Đảm bảo endpoint `/actuator/health`, `/actuator/metrics`, `/actuator/caches` đã expose.
- [ ] Expose custom metrics cho cache stats, lock metrics nếu cần (tạo class config/metrics).
    - **Best practice:** Chỉ expose endpoint cần thiết ở môi trường production.
    - **Lỗi thường gặp:** Không truy cập được endpoint do cấu hình sai, thiếu quyền.

### Nhóm: Thiết lập alert
- [ ] Thiết lập alert khi cache hit rate thấp (có thể dùng Prometheus/Grafana hoặc log alert).
- [ ] Thiết lập alert khi lock contention cao.
    - **Best practice:** Đặt ngưỡng alert hợp lý, tránh alert spam.
    - **Lỗi thường gặp:** Thiếu alert, không phát hiện sự cố kịp thời.

## Key Considerations
- Monitoring giúp phát hiện sớm sự cố, giảm downtime.
- Chỉ expose thông tin cần thiết để tránh lộ thông tin nhạy cảm.

## Discussion
- Health check và alert là bắt buộc cho hệ thống production.

## Current Status
- Chưa thực hiện (planned) 