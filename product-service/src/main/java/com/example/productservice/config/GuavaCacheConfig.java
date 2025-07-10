package com.example.productservice.config;

import com.example.product.grpc.ProductInfo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cấu hình cho Guava Cache (L1 Cache - In-memory).
 * <p>
 * Lớp này chịu trách nhiệm tạo và cấu hình một bean Cache để lưu trữ thông tin sản phẩm
 * trong bộ nhớ của mỗi instance service. Điều này giúp giảm thiểu độ trễ truy cập
 * cho các sản phẩm thường xuyên được yêu cầu.
 */
@Configuration
public class GuavaCacheConfig {

    /**
     * Tạo một bean Cache<String, ProductInfo> cho L1 caching.
     * <p>
     * Cache này được cấu hình với:
     * - Kích thước tối đa: 10,000 entries. Khi vượt quá, các entry cũ nhất sẽ bị loại bỏ.
     * - Thời gian hết hạn: 10 phút sau khi được ghi. Entry sẽ bị xóa khỏi cache sau khoảng thời gian này.
     *
     * @return một instance của Guava Cache đã được cấu hình.
     */
    @Bean
    public Cache<String, ProductInfo> productInfoCache() {
        // Luồng hoạt động:
        // 1. Sử dụng CacheBuilder để bắt đầu quá trình xây dựng cache.
        // 2. Thiết lập kích thước tối đa của cache để tránh tiêu thụ quá nhiều bộ nhớ.
        // 3. Thiết lập chính sách hết hạn để đảm bảo dữ liệu không quá cũ.
        // 4. Build và trả về đối tượng Cache.
        return CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }
} 