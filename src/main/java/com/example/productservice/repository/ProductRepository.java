package com.example.productservice.repository;

import com.example.productservice.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository cho entity Product, hỗ trợ các thao tác CRUD và query mở rộng.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    // (Tùy chọn) Thêm custom query method nếu cần, ví dụ:
    // List<Product> findByName(String name);
} 