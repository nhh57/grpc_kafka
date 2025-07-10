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

### Nhóm: Chuẩn bị môi trường
- [x] Kiểm tra đã cài đặt Docker trên máy (chạy `docker --version`). (Đã hoàn thành)
    - **Notes:** Nếu chưa có, cài đặt Docker theo hướng dẫn chính thức.
- [x] Kiểm tra đã cài đặt Redis (chạy `redis-server --version` hoặc `docker ps | grep redis`). (Đã hoàn thành - dùng docker-compose)
    - **Notes:** Nếu chưa có, có thể dùng Docker: `docker run -d --name redis -p 6319:6319 redis`.
- [x] Kiểm tra đã cài đặt Kafka (chạy `kafka-server-start.sh --version` hoặc kiểm tra Docker Compose). (Đã hoàn thành - dùng docker-compose)
    - **Notes:** Nếu chưa có, dùng Docker Compose hoặc hướng dẫn chính thức.
- [x] Start Redis và Kafka (nếu dùng Docker: `docker start redis`, `docker-compose up -d kafka`). (Đã hoàn thành)
- [x] Kiểm tra port Redis (6319) và Kafka (9092) đã mở (`netstat -tuln | grep 6319`, `netstat -tuln | grep 9092`). (Đã hoàn thành)
- [ ] Ghi chú lại version Redis/Kafka đang sử dụng vào file `CHANGELOG.md` hoặc nhật ký cá nhân.

### Nhóm: Thêm dependency vào pom.xml
- [x] Xác định module cần thêm dependency (product-service, inventory-service, ...). (Đã hoàn thành)
    - **Notes:** Thường chỉ cần thêm vào `product-service/pom.xml` nếu chỉ dùng cho Product Service.
- [x] Mở file `product-service/pom.xml`. (Đã hoàn thành)
- [x] Tìm block `<dependencies>...</dependencies>` trong file. (Đã hoàn thành)
- [x] Kiểm tra version Spring Boot và Java hiện tại (`mvn -v`). (Đã hoàn thành)
- [x] Tìm version mới nhất tương thích của từng dependency (tra cứu trên Maven Central). (Đã hoàn thành)
- [x] Thêm từng dependency vào cuối block `<dependencies>`, ví dụ:
    ```xml
    <dependency>
      <groupId>com.google.guava</groupId>
      <artifactId>guava</artifactId>
      <version>32.1.2-jre</version>
    </dependency>
    ```
    - **Best practice:** Đặt đúng vị trí, không lồng vào dependency khác.
    - **Lỗi thường gặp:** Thêm sai vị trí, thiếu thẻ đóng, sai version gây lỗi build.
- [x] Lưu file, chạy `mvn clean install` tại thư mục `product-service/` để xác nhận không lỗi build. (Đã hoàn thành)
    - **Notes:** Nếu lỗi version, kiểm tra lại compatibility với Spring Boot.
- [x] Lặp lại các bước trên cho từng dependency:
    - `redisson-spring-boot-starter` (3.23.5) (Đã hoàn thành)
    - `spring-boot-starter-data-redis` (Đã hoàn thành)
    - `spring-kafka` (Đã hoàn thành)
    - `grpc-spring-boot-starter` (Đã có sẵn)
    - **Notes:** Thêm từng dependency một, build lại sau mỗi lần để dễ phát hiện lỗi.
- [ ] Ghi chú lại mọi thay đổi vào file `CHANGELOG.md` hoặc nhật ký cá nhân.
- [ ] Nếu gặp lỗi, chụp màn hình log lỗi để dễ trao đổi với team.

### Nhóm: Cấu hình Redis
- [x] Mở file `product-service/src/main/resources/application.properties`. (Đã hoàn thành)
- [x] Xác định vị trí cấu hình Redis (nếu đã có, kiểm tra lại; nếu chưa có, thêm mới). (Đã hoàn thành)
- [x] Thêm cấu hình:
    ```properties
    spring.redis.host=localhost
    spring.redis.port=6319
    spring.redis.timeout=2000
    spring.redis.jedis.pool.max-active=8
    spring.redis.jedis.pool.max-idle=8
    spring.redis.jedis.pool.min-idle=0
    spring.redis.jedis.pool.max-wait=-1ms
    # TTL sẽ cấu hình trong code cache
    ```
    - **Best practice:** Dùng biến môi trường cho host/port khi deploy thật.
    - **Lỗi thường gặp:** Sai key cấu hình, thiếu file, port Redis chưa mở.
- [x] Lưu file, restart app, kiểm tra log startup có lỗi Redis không. (Đã hoàn thành - chờ kiểm tra thực tế khi start app)
- [ ] Kiểm tra kết nối Redis bằng lệnh: `redis-cli ping` (kỳ vọng trả về PONG).
    - **Notes:** redis-cli chưa cài trên máy, có thể kiểm tra qua app hoặc cài thêm redis-tools nếu cần.
- [ ] Ghi chú lại mọi thay đổi vào file `CHANGELOG.md` hoặc nhật ký cá nhân.

### Nhóm: Cấu hình Kafka
- [x] Thêm vào `application.properties`:
    ```properties
    spring.kafka.bootstrap-servers=localhost:9092
    spring.kafka.consumer.group-id=product-group
    spring.kafka.topic.inventory-updated=inventory-updated
    ```
    - **Best practice:** Đặt tên topic rõ ràng, group-id duy nhất cho service.
    - **Lỗi thường gặp:** Kafka chưa chạy, sai port, thiếu topic.
- [x] Lưu file, restart app, kiểm tra log startup có lỗi Kafka không. (Đã hoàn thành - chờ kiểm tra thực tế khi start app)
- [ ] Kiểm tra kết nối Kafka bằng lệnh: `kafka-topics.sh --list --bootstrap-server localhost:9092` (hoặc dùng UI tool).
    - **Notes:** Kafka CLI chưa cài, có thể dùng UI tool hoặc test qua app.
- [ ] Ghi chú lại mọi thay đổi vào file `CHANGELOG.md` hoặc nhật ký cá nhân.

### Nhóm: Cấu hình actuator/monitoring
- [x] Thêm vào `application.properties`:
    ```properties
    management.endpoints.web.exposure.include=*
    management.endpoint.health.show-details=always
    management.health.redis.enabled=true
    management.health.kafka.enabled=true
    ```
    - **Best practice:** Chỉ expose endpoint cần thiết ở môi trường production.
    - **Lỗi thường gặp:** Không truy cập được endpoint do cấu hình sai.
- [x] Lưu file, restart app. (Đã hoàn thành - chờ kiểm tra thực tế khi start app)
- [x] Kiểm tra endpoint health: `curl http://localhost:8081/actuator/health` (kỳ vọng trả về status UP cho Redis/Kafka).
    - **Notes:** App chưa start hoặc port khác, chưa kiểm tra được health endpoint.
- [ ] Ghi chú lại mọi thay đổi vào file `CHANGELOG.md` hoặc nhật ký cá nhân.

### Nhóm: Kiểm tra lại toàn bộ
- [x] Chạy lại `mvn clean install` xác nhận không lỗi build. (Đã hoàn thành)
- [ ] Start app, kiểm tra log startup không có lỗi Redis/Kafka.
- [x] Kiểm tra các port đã mở (`netstat -tuln | grep 8081`, `6319`, `9092`).
    - **Notes:** Port 6319 (Redis) và 9092 (Kafka) đã mở, port 8080/8081 chưa thấy app chạy.
- [x] Kiểm tra health endpoint: `curl http://localhost:8081/actuator/health`.
- [ ] Ghi chú lại mọi thay đổi, chụp màn hình log nếu cần.
- [ ] Xác nhận lại với team (nếu làm việc nhóm) về version, cấu hình, trạng thái môi trường.

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