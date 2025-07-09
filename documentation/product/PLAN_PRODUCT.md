# Kế hoạch Triển khai Module: Product Service (gRPC + Multi-layer Caching)

## 1. Tổng quan và Mục tiêu

- **Mô tả:** Service quản lý toàn bộ vòng đời và thông tin sản phẩm, là nguồn sự thật (source of truth) cho giá, mô tả, trạng thái sản phẩm. Thiết kế để đạt hiệu suất đọc cực cao qua caching 2 lớp.
- **Mục tiêu chính:**
    1.  Cung cấp API gRPC ổn định, strongly-typed, hiệu suất cao.
    2.  Đảm bảo nhất quán dữ liệu giữa cache và DB, đồng bộ hóa qua Kafka/Redis PubSub.
    3.  Tối ưu hóa hiệu suất đọc với kiến trúc caching 2 lớp (L1: Guava, L2: Redis).

---

# Checklist Task Triển khai Product Service (gRPC + Multi-layer Caching)

## 1. Chuẩn bị Cơ sở hạ tầng & Phụ thuộc
- [ ] Task 1.1: Thêm dependency `guava`, `redisson-spring-boot-starter`, `spring-boot-starter-data-redis`, `spring-kafka`, `grpc-spring-boot-starter` vào `pom.xml`.
- [ ] Task 1.2: Cấu hình Redis (host, port, pool, TTL) trong `application.properties`.
- [ ] Task 1.3: Cấu hình Kafka (bootstrap servers, topic, group) trong `application.properties`.
- [ ] Task 1.4: Cấu hình actuator/monitoring cho cache, Redis, Kafka, DB.

## 2. Thiết kế & Triển khai Domain
- [ ] Task 2.1: Tạo entity `Product` với các trường: id, name, description, price, quantity, version.
- [ ] Task 2.2: Đánh index trên `id`, `version` trong entity và DB.
- [ ] Task 2.3: Thêm annotation `@Version` cho trường version (optimistic locking).
- [ ] Task 2.4: Tạo repository `ProductRepository` extends JpaRepository.

## 3. Định nghĩa API gRPC
- [ ] Task 3.1: Định nghĩa file `product.proto` với các message và service như tài liệu.
- [ ] Task 3.2: Generate Java code từ proto.
- [ ] Task 3.3: Tạo lớp `ProductGrpcServiceImpl` implement các RPC (GetProductInfo, ValidatePriceWithVersion).

## 4. Triển khai Caching 2 lớp
### L1: Guava Cache
- [ ] Task 4.1: Tạo cấu hình Guava Cache (maxSize, expireAfterWrite).
- [ ] Task 4.2: Tạo service `L1ProductCacheService` (get, put, invalidate, expose stats).
- [ ] Task 4.3: Expose cache stats qua actuator.
### L2: Redis Cache
- [ ] Task 4.4: Tạo cấu hình Redis cache (TTL, key pattern).
- [ ] Task 4.5: Tạo service `L2ProductCacheService` (get, put, invalidate, expose stats).
- [ ] Task 4.6: Expose Redis health qua actuator.
### Cache Logic & Invalidation
- [ ] Task 4.7: Triển khai logic cache-aside: L1 → L2 → DB.
- [ ] Task 4.8: Triển khai distributed lock (Redisson) khi cache miss.
- [ ] Task 4.9: Triển khai cache invalidation: khi update, invalidate L2, publish Redis Pub/Sub để các instance invalidate L1.
- [ ] Task 4.10: Định nghĩa key pattern `product:{id}` cho cache.

## 5. Đồng bộ & Event
- [ ] Task 5.1: Lắng nghe event Kafka `InventoryUpdated`, update L2 cache.
- [ ] Task 5.2: Lắng nghe Redis Pub/Sub để invalidate L1 cache trên các instance.
- [ ] Task 5.3: Publish event khi update tồn kho.

## 6. Xử lý concurrency
- [ ] Task 6.1: Áp dụng optimistic locking khi update product (kiểm tra version).
- [ ] Task 6.2: Triển khai distributed lock (Redisson) khi cache miss.
- [ ] Task 6.3: Thêm retry logic (exponential backoff) khi acquire lock thất bại.
- [ ] Task 6.4: Đảm bảo lock TTL, release lock trong finally để tránh deadlock.

## 7. Monitoring & Health Check
- [ ] Task 7.1: Expose actuator endpoint cho cache stats, Redis/Kafka/DB health, lock metrics.
- [ ] Task 7.2: Thiết lập alert khi cache hit rate thấp, lock contention cao.

## 8. Kiểm thử
- [ ] Task 8.1: Viết unit test cho service, cache logic, lock, event handler (mock Redis/Kafka).
- [ ] Task 8.2: Viết integration test cho full flow gRPC → cache → DB, test cache hit/miss, invalidation, concurrent access.
- [ ] Task 8.3: Viết performance test đo throughput, latency, cache hit rate, lock contention.

## 9. Tài liệu hóa
- [ ] Task 9.1: Cập nhật tài liệu hướng dẫn cấu hình cache, Redis, Kafka, actuator.
- [ ] Task 9.2: Cập nhật README về kiến trúc caching 2 lớp, flow chính, các lưu ý concurrency. 