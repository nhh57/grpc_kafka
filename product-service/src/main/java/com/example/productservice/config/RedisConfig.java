package com.example.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Cấu hình cho Redis (L2 Cache).
 * <p>
 * Lớp này chịu trách nhiệm cấu hình RedisTemplate, một thành phần cốt lõi của Spring Data Redis
 * để tương tác với Redis server. Việc tùy chỉnh các Serializer giúp đảm bảo dữ liệu được
 * lưu trữ dưới định dạng có thể đọc được (JSON), thuận tiện cho việc debug.
 */
@Configuration
public class RedisConfig {

    /**
     * Tạo và cấu hình một bean RedisTemplate<String, Object>.
     * <p>
     * Luồng hoạt động:
     * 1. Tạo một instance mới của RedisTemplate.
     * 2. Thiết lập connection factory để kết nối đến Redis.
     * 3. Cấu hình Key Serializer: Sử dụng StringRedisSerializer để các key trong Redis là chuỗi UTF-8, dễ đọc.
     * 4. Cấu hình Value Serializer: Sử dụng GenericJackson2JsonRedisSerializer để chuyển đổi các đối tượng Java
     *    thành định dạng JSON trước khi lưu vào Redis. Điều này giúp dữ liệu trở nên minh bạch và dễ kiểm tra.
     *
     * @param connectionFactory factory để tạo kết nối đến Redis, được Spring Boot tự động cấu hình.
     * @return một instance của RedisTemplate đã được cấu hình.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
} 