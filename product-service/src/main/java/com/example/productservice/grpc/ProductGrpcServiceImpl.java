package com.example.productservice.grpc;

import com.example.product.grpc.GetProductRequest;
import com.example.product.grpc.ProductInfo;
import com.example.product.grpc.ProductServiceGrpc;
import com.example.product.grpc.ValidatePriceRequest;
import com.example.productservice.domain.Product;
import com.example.productservice.repository.ProductRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
@RequiredArgsConstructor
public class ProductGrpcServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductRepository productRepository;

    @Override
    public void getProductInfo(GetProductRequest request, StreamObserver<ProductInfo> responseObserver) {
        String productId = request.getProductId();

        productRepository.findById(productId)
                .map(this::toProductInfo)
                .ifPresentOrElse(
                        responseObserver::onNext,
                        () -> responseObserver.onError(Status.NOT_FOUND
                                .withDescription("Product not found with id: " + productId)
                                .asRuntimeException())
                );
        responseObserver.onCompleted();
    }

    @Override
    public void validatePriceWithVersion(ValidatePriceRequest request, StreamObserver<ProductInfo> responseObserver) {
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
