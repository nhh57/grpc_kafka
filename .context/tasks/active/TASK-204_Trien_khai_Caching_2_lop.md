---
title: "TASK-204: Trien khai Caching 2 lop (Chi tiet)"
type: "task"
status: "active"
created: "2025-07-10T14:00:00"
updated: "2025-07-10T14:30:00"
id: "TASK-204"
priority: "high"
dependencies: ["TASK-202", "TASK-203"]
tags: ["caching", "redis", "guava", "performance", "product-service"]
---

## Description
Triển khai kiến trúc caching 2 lớp (L1: Guava, L2: Redis) cho Product Service để tối ưu hóa hiệu suất đọc, cùng với cơ chế xử lý đồng bộ để chống cache stampede. Phiên bản này được phân rã cực kỳ chi tiết để kiểm soát từng bước.

## Objectives
- Implement L1 cache (in-memory) sử dụng Guava Cache.
- Implement L2 cache (distributed) sử dụng Redis.
- Implement distributed lock (Redisson) để xử lý cache miss và ngăn chặn cache stampede.
- Tích hợp caching logic vào `ProductService` một cách an toàn và có thể kiểm chứng.

## Checklist (Phân rã cực kỳ chi tiết)

### Nhóm: L1 Cache - Guava (In-memory)

- [x] **1.1. Kiểm tra Dependency:** (Đã hoàn thành)
    - [x] Mở file `product-service/pom.xml`. (Đã hoàn thành)
    - [x] **Xác minh:** Dependency `com.google.guava:guava` đã tồn tại. (Đã hoàn thành)
    - **Notes:** Nếu chưa có, thêm vào. Phiên bản đề xuất trong TDD là `32.1.1-jre`.

- [x] **1.2. Tạo GuavaCacheConfig:** (Đã hoàn thành)
    - **Vị trí:** `product-service/src/main/java/com/example/productservice/config/GuavaCacheConfig.java`
    - **Mục tiêu:** Cung cấp một bean `Cache<String, ProductInfo>` cho toàn bộ ứng dụng.
    - **Checklist:**
        - [x] Tạo file `GuavaCacheConfig.java`. (Đã hoàn thành)
        - [x] Thêm annotation `@Configuration` cho class. (Đã hoàn thành)
        - [x] Tạo method `public Cache<String, ProductInfo> productInfoCache()` và chú thích bằng `@Bean`. (Đã hoàn thành)
        - [x] Trong method, dùng `CacheBuilder.newBuilder()`. (Đã hoàn thành)
        - [x] Chain các method: `.maximumSize(10000)`. (Đã hoàn thành)
        - [x] Chain tiếp: `.expireAfterWrite(10, TimeUnit.MINUTES)`. (Đã hoàn thành)
        - [x] Gọi `.build()` và trả về instance. (Đã hoàn thành)
    - **Verification:**
        - [ ] Khởi động ứng dụng và kiểm tra log. Đảm bảo không có lỗi `NoSuchBeanDefinitionException` liên quan đến `Cache<String, ProductInfo>`.

- [x] **1.3. Tạo ProductL1CacheService:** (Đã hoàn thành)
    - [x] Tạo file `ProductL1CacheService.java`. (Đã hoàn thành)
    - [x] Thêm annotation `@Service`. (Đã hoàn thành)
    - [x] Tiêm `Cache<String, ProductInfo>` qua constructor. (Đã hoàn thành)
    - [x] Implement `get(String productId)`: trả về `Optional<ProductInfo>` bằng cách dùng `Optional.ofNullable(cache.getIfPresent(productId))`. (Đã hoàn thành)
    - [x] Implement `put(String productId, ProductInfo productInfo)`: gọi `cache.put(...)`. (Đã hoàn thành)
    - [x] Implement `invalidate(String productId)`: gọi `cache.invalidate(...)`. (Đã hoàn thành)
    - **Verification:**
        - [ ] Viết Unit Test cho `ProductL1CacheService` sử dụng Mockito để mock `Cache` bean. Kiểm tra các kịch bản get hit, get miss, put, và invalidate.

### Nhóm: L2 Cache - Redis (Distributed)

- [x] **2.1. Kiểm tra Dependency:** (Đã hoàn thành)
    - [x] Mở `product-service/pom.xml`. (Đã hoàn thành)
    - [x] **Xác minh:** Dependency `org.springframework.boot:spring-boot-starter-data-redis` đã tồn tại. (Đã hoàn thành)

- [x] **2.2. Cấu hình Redis Connection:** (Đã hoàn thành)
    - [x] Mở file và thêm/xác minh dòng `spring.redis.host=localhost`. (Đã hoàn thành)
    - [x] Thêm/xác minh dòng `spring.redis.port=6379`. (Đã hoàn thành)
    - **Verification:**
        - [ ] Khởi động ứng dụng. Log khởi động của Spring Boot sẽ báo lỗi `Unable to connect to Redis` nếu cấu hình sai hoặc Redis server không chạy.

- [x] **2.3. Tạo RedisConfig:** (Đã hoàn thành)
    - [x] Tạo file `RedisConfig.java`. (Đã hoàn thành)
    - [x] Thêm `@Configuration`. (Đã hoàn thành)
    - [x] Tạo `@Bean` cho `RedisTemplate<String, Object>`. (Đã hoàn thành)
    - [x] Cấu hình `KeySerializer` là `StringRedisSerializer`. (Đã hoàn thành)
    - [x] Cấu hình `ValueSerializer` là `GenericJackson2JsonRedisSerializer`. (Đã hoàn thành)
    - **Verification:**
        - [ ] Sau khi có service, ghi một object vào Redis và dùng `redis-cli` (hoặc một UI tool) để kiểm tra, đảm bảo dữ liệu được lưu dưới dạng JSON.

- [x] **2.4. Tạo ProductL2CacheService:** (Đã hoàn thành)
    - [x] Tạo file `ProductL2CacheService.java`. (Đã hoàn thành)
    - [x] Thêm `@Service`. (Đã hoàn thành)
    - [x] Tiêm `RedisTemplate<String, Object>`. (Đã hoàn thành)
    - [x] Định nghĩa `private static final String KEY_PREFIX = "product:"`. (Đã hoàn thành)
    - [x] Implement `get(String productId)`: lấy data, ép kiểu về `ProductInfo` và trả về `Optional`. (Đã hoàn thành)
    - [x] Implement `put(String productId, ProductInfo productInfo)`: dùng `opsForValue().set` với TTL là 1 giờ (`1, TimeUnit.HOURS`). (Đã hoàn thành)
    - [x] Implement `invalidate(String productId)`: dùng `redisTemplate.delete(...)`. (Đã hoàn thành)
    - **Verification:**
        - [ ] Viết Integration Test sử dụng Testcontainers để khởi tạo một Redis container, kiểm tra các hoạt động get/put/invalidate và TTL.

### Nhóm: Xử lý Đồng bộ - Redisson

- [x] **3.1. Kiểm tra Dependency:** (Đã hoàn thành)
    - [x] Mở `product-service/pom.xml`. (Đã hoàn thành)
    - [x] **Xác minh:** Dependency `org.redisson:redisson-spring-boot-starter` đã tồn tại. (Đã hoàn thành)

- [x] **3.2. Tạo DistributedLockService:** (Đã hoàn thành)
    - [x] Tạo file `DistributedLockService.java`. (Đã hoàn thành)
    - [x] Thêm `@Service`. (Đã hoàn thành)
    - [x] Tiêm `RedissonClient`. (Đã hoàn thành)
    - [x] Implement `executeWithLock(String lockKey, Supplier<T> supplier)`: (Đã hoàn thành)
        - [x] Lấy `RLock`. (Đã hoàn thành)
        - [x] Gọi `lock.tryLock()` trong `try-catch(InterruptedException)`. (Đã hoàn thành)
        - [x] Nếu lock thành công, thực thi `supplier` trong `try-finally` để đảm bảo `lock.unlock()` luôn được gọi. (Đã hoàn thành)
        - [x] Nếu thất bại, log warning và trả về `Optional.empty()`. (Đã hoàn thành)
    - **Verification:**
        - [ ] Viết Integration Test với 2 a thread cùng truy cập một tài nguyên được bảo vệ bởi service này để xác nhận chỉ một thread được thực thi.

### Nhóm: Tích hợp vào gRPC Service

- [x] **4.1. Cập nhật ProductGrpcServiceImpl:** (Đã hoàn thành)
    - [x] **Dependency Injection:** Tiêm `ProductL1CacheService`, `ProductL2CacheService`, `DistributedLockService` vào qua constructor. (Đã hoàn thành)
    - [x] **Refactor `getProductInfo`:** (Đã hoàn thành)
        - [x] **Step 1 (Check L1):** Gọi `l1CacheService.get()`. Nếu có, trả về ngay. (Đã hoàn thành)
        - [x] **Step 2 (Check L2):** Gọi `l2CacheService.get()`. Nếu có, ghi vào L1 và trả về. (Đã hoàn thành)
        - [x] **Step 3 (Handle Cache Miss):** Gọi `distributedLockService.executeWithLock(...)`. (Đã hoàn thành)
        - [x] **Bên trong Lock:** (Đã hoàn thành)
            - [x] **Re-check L2 (quan trọng!):** Kiểm tra lại L2 cache. Nếu có (do thread khác đã ghi), trả về giá trị đó. (Đã hoàn thành)
            - [x] Nếu vẫn miss, gọi `productRepository.findById(...)`. (Đã hoàn thành)
            - [x] Map entity sang `ProductInfo`. (Đã hoàn thành)
            - [x] Ghi kết quả vào L2, rồi L1. (Đã hoàn thành)
            - [x] Trả về `ProductInfo`. (Đã hoàn thành)
        - [x] **Xử lý kết quả cuối cùng:** Dựa vào `Optional` trả về từ `executeWithLock` để trả `ProductInfo` hoặc lỗi `NOT_FOUND`/`UNAVAILABLE`. (Đã hoàn thành)
    - **Verification:**
        - [ ] Viết Integration Test cấp cao cho `ProductGrpcServiceImpl`, bao phủ toàn bộ các kịch bản: L1 hit, L2 hit, Cache miss (có và không có lock), Product not found.

## Key Considerations
- **Cache Invalidation:** Task này tập trung vào luồng đọc. Invalidation sẽ được xử lý ở `TASK-205`.
- **Error Handling:** Phải xử lý trường hợp Redis không khả dụng, fallback về đọc DB (có thể bỏ qua lock nếu Redis down).

## Discussion
- Chiến lược retry khi không lấy được lock: Tạm thời thất bại nhanh. Sẽ cải tiến sau nếu cần.

## Current Status
- Chưa bắt đầu. 