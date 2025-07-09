---
title: "TASK-201: Chuan bi Co so ha tang & Phu thuoc"
type: "task"
status: "planned"
created: "2025-07-09T10:30:00"
updated: "2025-07-09T10:45:00"
id: "TASK-201"
priority: "high"
dependencies: []
tags: ["infrastructure", "dependency", "setup", "product-service"]
---

## Description
Chuẩn bị toàn bộ cơ sở hạ tầng và các phụ thuộc cần thiết cho Product Service, đảm bảo môi trường sẵn sàng cho việc phát triển các tính năng tiếp theo (caching, event, monitoring, ...).

## Objectives
- Đảm bảo tất cả dependency cần thiết đã được thêm vào project.
- Cấu hình Redis, Kafka, actuator/monitoring đúng chuẩn.
- Đảm bảo môi trường có thể build và chạy được.

## Checklist (Phân rã cực chi tiết)

### Nhóm: Thêm dependency vào pom.xml
- [ ] Xác định module cần thêm dependency (product-service, inventory-service, ...)
    - **Notes:** Thường chỉ cần thêm vào `product-service/pom.xml` nếu chỉ dùng cho Product Service.
- [ ] Mở file `product-service/pom.xml`.
- [ ] Tìm block `<dependencies>...</dependencies>` trong file.
- [ ] Thêm đoạn sau vào cuối block `<dependencies>`:
    ```xml
    <dependency>
      <groupId>com.google.guava</groupId>
      <artifactId>guava</artifactId>
      <version>32.1.2-jre</version> <!-- Kiểm tra version mới nhất tương thích với Spring Boot -->
    </dependency>
    ```
    - **Best practice:** Đặt đúng vị trí, không lồng vào dependency khác.
    - **Lỗi thường gặp:** Thêm sai vị trí, thiếu thẻ đóng, sai version gây lỗi build.
- [ ] Lưu file, chạy `mvn clean install` tại thư mục `product-service/` để xác nhận không lỗi build.
    - **Notes:** Nếu lỗi version, kiểm tra lại compatibility với Spring Boot.

- [ ] Lặp lại các bước trên cho các dependency:
    - `redisson-spring-boot-starter`
    - `spring-boot-starter-data-redis`
    - `spring-kafka`
    - `grpc-spring-boot-starter`
    - **Notes:** Có thể thêm từng dependency một, build lại sau mỗi lần để dễ phát hiện lỗi.

### Nhóm: Cấu hình Redis
- [ ] Mở file `product-service/src/main/resources/application.properties`.
- [ ] Thêm cấu hình:
    ```properties
    spring.redis.host=localhost
    spring.redis.port=6379
    spring.redis.timeout=2000
    spring.redis.jedis.pool.max-active=8
    spring.redis.jedis.pool.max-idle=8
    spring.redis.jedis.pool.min-idle=0
    spring.redis.jedis.pool.max-wait=-1ms
    # TTL sẽ cấu hình trong code cache
    ```
    - **Best practice:** Dùng biến môi trường cho host/port khi deploy thật.
    - **Lỗi thường gặp:** Sai key cấu hình, thiếu file, port Redis chưa mở.

### Nhóm: Cấu hình Kafka
- [ ] Thêm vào `application.properties`:
    ```properties
    spring.kafka.bootstrap-servers=localhost:9092
    spring.kafka.consumer.group-id=product-group
    spring.kafka.topic.inventory-updated=inventory-updated
    ```
    - **Best practice:** Đặt tên topic rõ ràng, group-id duy nhất cho service.
    - **Lỗi thường gặp:** Kafka chưa chạy, sai port, thiếu topic.

### Nhóm: Cấu hình actuator/monitoring
- [ ] Thêm vào `application.properties`:
    ```properties
    management.endpoints.web.exposure.include=*
    management.endpoint.health.show-details=always
    management.health.redis.enabled=true
    management.health.kafka.enabled=true
    ```
    - **Best practice:** Chỉ expose endpoint cần thiết ở môi trường production.
    - **Lỗi thường gặp:** Không truy cập được endpoint do cấu hình sai.

## Key Considerations
- Đảm bảo version dependency tương thích với Spring Boot và Java version của project.
- Cấu hình Redis/Kafka nên dùng biến môi trường để dễ dàng chuyển đổi môi trường dev/staging/prod.
- Actuator giúp theo dõi health, metrics, rất quan trọng cho vận hành production.
- Build lại sau mỗi thay đổi để phát hiện lỗi sớm.

## Discussion
- Việc chuẩn bị hạ tầng và dependency là nền tảng cho mọi tính năng phía sau. Nếu thiếu hoặc sai version sẽ gây lỗi build/run khó debug.
- Nên kiểm tra kỹ lại các cấu hình sau khi thêm, chạy thử build và start app.
- Có thể tham khảo tài liệu Spring Boot chính thức cho từng dependency.

## Current Status
- Chưa thực hiện (planned) 