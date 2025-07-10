package com.example.productservice.grpc;

import com.example.product.grpc.GetProductRequest;
import com.example.product.grpc.ProductInfo;
import com.example.product.grpc.ProductServiceGrpc;
import com.example.product.grpc.ValidatePriceRequest;
import com.example.productservice.domain.Product;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.caching.ProductL1CacheService;
import com.example.productservice.service.caching.ProductL2CacheService;
import com.example.productservice.service.lock.DistributedLockService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.util.Optional;

@GRpcService
@RequiredArgsConstructor
@Slf4j
public class ProductGrpcServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductRepository productRepository;
    private final ProductL1CacheService l1CacheService;
    private final ProductL2CacheService l2CacheService;
    private final DistributedLockService lockService;

    /**
     * Lấy thông tin sản phẩm, áp dụng luồng caching 2 lớp và distributed lock.
     *
     * @param request          Yêu cầu chứa ID sản phẩm.
     * @param responseObserver Observer để trả về kết quả hoặc lỗi.
     */
    @Override
    public void getProductInfo(GetProductRequest request, StreamObserver<ProductInfo> responseObserver) {
        final String productId = request.getProductId();

        // Step 1: Check L1 Cache (Guava)
        Optional<ProductInfo> l1Result = l1CacheService.get(productId);
        if (l1Result.isPresent()) {
            log.info("L1 Cache HIT for product: {}", productId);
            responseObserver.onNext(l1Result.get());
            responseObserver.onCompleted();
            return;
        }
        log.info("L1 Cache MISS for product: {}", productId);

        // Step 2: Check L2 Cache (Redis)
        Optional<ProductInfo> l2Result = l2CacheService.get(productId);
        if (l2Result.isPresent()) {
            log.info("L2 Cache HIT for product: {}", productId);
            l1CacheService.put(productId, l2Result.get()); // Populate L1
            responseObserver.onNext(l2Result.get());
            responseObserver.onCompleted();
            return;
        }
        log.info("L2 Cache MISS for product: {}", productId);

        // Step 3: Cache miss, use distributed lock to fetch from DB
        Optional<ProductInfo> dbResult = lockService.executeWithLock(productId, () -> {
            // Re-check L2 cache inside lock to prevent redundant DB calls (Double-checked locking)
            Optional<ProductInfo> recheckedL2 = l2CacheService.get(productId);
            if (recheckedL2.isPresent()) {
                log.info("L2 Cache HIT (inside lock) for product: {}", productId);
                return recheckedL2.get();
            }

            // Fetch from database
            log.info("Fetching from DB for product: {}", productId);
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                return null; // Will be handled outside
            }
            Product product = productOpt.get();
            ProductInfo productInfo = toProductInfo(product);

            // Populate caches
            l2CacheService.put(productId, productInfo);
            l1CacheService.put(productId, productInfo);

            return productInfo;
        });

        if (dbResult.isPresent()) {
            responseObserver.onNext(dbResult.get());
            responseObserver.onCompleted();
        } else {
            // This can happen if product not in DB, or lock not acquired
            // A more specific error could be returned if lockService provided more details
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Product not found with id: " + productId + " or failed to acquire lock.")
                    .asRuntimeException());
        }
    }


    @Override
    public void validatePriceWithVersion(ValidatePriceRequest request, StreamObserver<ProductInfo> responseObserver) {
        // Ghi chú: Logic này thường không cần cache vì nó mang tính giao dịch và cần dữ liệu mới nhất.
        // Việc thêm caching vào đây có thể dẫn đến xác thực sai lệch nếu cache không được cập nhật tức thì.
        String productId = request.getProductId();
        double price = request.getPrice();
        long version = request.getVersion();

        productRepository.findById(productId)
                .ifPresentOrElse(product -> {
                            if (product.getVersion() != version) {
                                responseObserver.onError(Status.FAILED_PRECONDITION
                                        .withDescription("Version mismatch for product id: " + productId)
                                        .asRuntimeException());
                                return;
                            }
                            if (Double.compare(product.getPrice(), price) != 0) {
                                responseObserver.onError(Status.INVALID_ARGUMENT
                                        .withDescription("Price mismatch for product id: " + productId)
                                        .asRuntimeException());
                                return;
                            }
                            responseObserver.onNext(toProductInfo(product));
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(Status.NOT_FOUND
                                .withDescription("Product not found with id: " + productId)
                                .asRuntimeException()));
    }

    private ProductInfo toProductInfo(Product product) {
        return ProductInfo.newBuilder()
                .setId(product.getId())
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setPrice(product.getPrice())
                .setQuantity(product.getQuantity())
                .setVersion(product.getVersion())
                .build();
    }
}
