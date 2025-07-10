package com.example.productservice.service.caching;

import com.example.product.grpc.ProductInfo;
import com.google.common.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service để quản lý L1 Cache (Guava).
 * <p>
 * Lớp này đóng gói các thao tác cơ bản với Guava Cache (get, put, invalidate)
 * để cung cấp một API rõ ràng và dễ sử dụng cho các service khác.
 * Việc sử dụng Optional<ProductInfo> giúp xử lý trường hợp cache miss một cách an toàn.
 */
@Service
@RequiredArgsConstructor
public class ProductL1CacheService {

    private final Cache<String, ProductInfo> productInfoCache;

    /**
     * Lấy thông tin sản phẩm từ L1 cache.
     *
     * @param productId ID của sản phẩm cần tìm.
     * @return một {@link Optional} chứa {@link ProductInfo} nếu tìm thấy,
     * hoặc {@link Optional#empty()} nếu không tìm thấy (cache miss).
     */
    public Optional<ProductInfo> get(String productId) {
        // Luồng hoạt động:
        // 1. Sử dụng cache.getIfPresent() để tránh việc phải load dữ liệu nếu key không tồn tại.
        //    Đây là phương thức non-blocking và an toàn.
        // 2. Bọc kết quả trong Optional.ofNullable() để xử lý cả trường hợp trả về là null (key không có).
        return Optional.ofNullable(productInfoCache.getIfPresent(productId));
    }

    /**
     * Đặt (thêm hoặc cập nhật) thông tin sản phẩm vào L1 cache.
     *
     * @param productId   ID của sản phẩm để làm key.
     * @param productInfo đối tượng ProductInfo để cache.
     */
    public void put(String productId, ProductInfo productInfo) {
        productInfoCache.put(productId, productInfo);
    }

    /**
     * Xóa một entry khỏi L1 cache.
     * <p>
     * Thường được gọi khi dữ liệu sản phẩm trong DB bị thay đổi (update/delete)
     * để đảm bảo tính nhất quán.
     *
     * @param productId ID của sản phẩm cần xóa khỏi cache.
     */
    public void invalidate(String productId) {
        productInfoCache.invalidate(productId);
    }
}
