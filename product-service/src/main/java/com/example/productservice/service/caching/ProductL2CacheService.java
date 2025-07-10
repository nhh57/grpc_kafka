package com.example.productservice.service.caching;

import com.example.product.grpc.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Service để quản lý L2 Cache (Redis).
 * <p>
 * Lớp này chịu trách nhiệm đóng gói các thao tác với Redis,
 * bao gồm việc xây dựng key, lưu trữ với TTL, và xóa cache.
 */
@Service
@RequiredArgsConstructor
public class ProductL2CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Tiền tố cho các cache key của sản phẩm trong Redis.
     * Giúp tránh xung đột key và dễ dàng quản lý.
     */
    private static final String KEY_PREFIX = "product:";

    /**
     * Lấy thông tin sản phẩm từ L2 cache.
     *
     * @param productId ID của sản phẩm.
     * @return một {@link Optional} chứa {@link ProductInfo} nếu tìm thấy,
     * hoặc {@link Optional#empty()} nếu không tìm thấy.
     */
    public Optional<ProductInfo> get(String productId) {
        // Luồng hoạt động:
        // 1. Xây dựng cache key hoàn chỉnh từ prefix và productId.
        // 2. Lấy dữ liệu từ Redis bằng key.
        // 3. Ép kiểu dữ liệu trả về sang ProductInfo.
        // 4. Trả về Optional để xử lý an toàn.
        try {
            Object value = redisTemplate.opsForValue().get(buildKey(productId));
            return Optional.ofNullable((ProductInfo) value);
        } catch (Exception e) {
            // Log lỗi nếu có vấn đề với Redis (ví dụ: không kết nối được)
            // và trả về empty để luồng xử lý có thể fallback về DB.
            // logger.error("Error getting from L2 cache for key {}: {}", buildKey(productId), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Đặt (thêm hoặc cập nhật) thông tin sản phẩm vào L2 cache với TTL là 1 giờ.
     *
     * @param productId   ID của sản phẩm.
     * @param productInfo đối tượng ProductInfo để cache.
     */
    public void put(String productId, ProductInfo productInfo) {
        try {
            redisTemplate.opsForValue().set(buildKey(productId), productInfo, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            // Log lỗi nhưng không re-throw để không làm gián đoạn luồng chính.
            // logger.error("Error putting to L2 cache for key {}: {}", buildKey(productId), e.getMessage());
        }
    }

    /**
     * Xóa một entry khỏi L2 cache.
     *
     * @param productId ID của sản phẩm cần xóa.
     */
    public void invalidate(String productId) {
        try {
            redisTemplate.delete(buildKey(productId));
        } catch (Exception e) {
            // logger.error("Error invalidating L2 cache for key {}: {}", buildKey(productId), e.getMessage());
        }
    }

    /**
     * Xây dựng cache key hoàn chỉnh.
     *
     * @param productId ID sản phẩm.
     * @return Cache key (ví dụ: "product:123e4567-e89b-12d3-a456-426614174000").
     */
    private String buildKey(String productId) {
        return KEY_PREFIX + productId;
    }
} 