package com.example.productservice.domain;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity đại diện cho sản phẩm trong hệ thống.
 * Bao gồm các trường cơ bản và hỗ trợ optimistic locking qua version.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product",
    indexes = {
        @Index(name = "idx_product_id", columnList = "id"),
        @Index(name = "idx_product_version", columnList = "version")
    }
)
public class Product {
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Version
    @Column(name = "version")
    private Integer version;
} 