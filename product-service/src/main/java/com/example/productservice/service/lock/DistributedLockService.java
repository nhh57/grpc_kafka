package com.example.productservice.service.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Service để quản lý Distributed Lock sử dụng Redisson.
 * <p>
 * Cung cấp một phương thức an toàn để thực thi một khối logic được bảo vệ bởi một lock
 * phân tán, nhằm ngăn chặn các vấn đề về đồng bộ hóa trong môi trường microservice,
 * đặc biệt là cache stampede.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DistributedLockService {

    private final RedissonClient redissonClient;
    private static final long WAIT_TIME_SECONDS = 5;
    private static final long LEASE_TIME_SECONDS = 10;
    private static final String LOCK_PREFIX = "lock:product:";

    /**
     * Thực thi một hành động (supplier) bên trong một distributed lock.
     *
     * @param lockKey  key định danh cho lock (thường là ID của tài nguyên).
     * @param supplier hành động cần thực thi (trả về một giá trị).
     * @param <T>      kiểu dữ liệu trả về của hành động.
     * @return một {@link Optional} chứa kết quả từ supplier nếu lock thành công và hành động được thực thi,
     * hoặc {@link Optional#empty()} nếu không thể lấy được lock.
     */
    public <T> Optional<T> executeWithLock(String lockKey, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + lockKey);
        try {
            // Luồng hoạt động:
            // 1. Cố gắng lấy lock trong một khoảng thời gian chờ (WAIT_TIME).
            // 2. Nếu lock được, nó sẽ được giữ trong một khoảng thời gian LEASE_TIME
            //    để tránh deadlock nếu instance bị crash.
            boolean isLocked = lock.tryLock(WAIT_TIME_SECONDS, LEASE_TIME_SECONDS, TimeUnit.SECONDS);

            if (isLocked) {
                // 3. Nếu lock thành công, thực thi logic nghiệp vụ trong khối try-finally
                //    để đảm bảo lock luôn được giải phóng.
                try {
                    // SỬA LỖI: Thay thế Optional.of() bằng Optional.ofNullable().
                    // Lý do: supplier.get() có thể trả về null (ví dụ: khi không tìm thấy product trong DB).
                    // Optional.of(null) sẽ gây ra NullPointerException.
                    // Optional.ofNullable(null) sẽ trả về Optional.empty() một cách an toàn.
                    return Optional.ofNullable(supplier.get());
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            } else {
                // 4. Nếu không lấy được lock sau thời gian chờ, log lại và trả về empty.
                log.warn("Could not acquire lock for key: {}", lockKey);
                return Optional.empty();
            }
        } catch (InterruptedException e) {
            log.error("Thread was interrupted while waiting for lock on key: {}", lockKey, e);
            Thread.currentThread().interrupt(); // Restore the interrupted status
            return Optional.empty();
        }
    }
} 