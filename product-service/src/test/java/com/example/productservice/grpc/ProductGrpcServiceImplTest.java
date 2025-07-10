//package com.example.productservice.grpc;
//
//import com.example.product.grpc.GetProductRequest;
//import com.example.product.grpc.ProductInfo;
//import com.example.product.grpc.ValidatePriceRequest;
//import com.example.productservice.domain.Product;
//import com.example.productservice.repository.ProductRepository;
//import io.grpc.Status;
//import io.grpc.StatusRuntimeException;
//import io.grpc.stub.StreamObserver;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ProductGrpcServiceImplTest {
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private StreamObserver<ProductInfo> responseObserver;
//
//    private ProductGrpcServiceImpl productGrpcService;
//
//    private Product testProduct;
//
//    @BeforeEach
//    void setUp() {
//        productGrpcService = new ProductGrpcServiceImpl(productRepository);
//        testProduct = new Product();
//        testProduct.setId("test-id");
//        testProduct.setName("Test Product");
//        testProduct.setDescription("Test Description");
//        testProduct.setPrice(100.0);
//        testProduct.setQuantity(10);
//        testProduct.setVersion(1L);
//    }
//
//    @Test
//    void getProductInfo_Success() {
//        // Given
//        when(productRepository.findById("test-id")).thenReturn(Optional.of(testProduct));
//        GetProductRequest request = GetProductRequest.newBuilder()
//            .setProductId("test-id")
//            .setVersion(1)
//            .build();
//
//        // When
//        productGrpcService.getProductInfo(request, responseObserver);
//
//        // Then
//        ArgumentCaptor<ProductInfo> responseCaptor = ArgumentCaptor.forClass(ProductInfo.class);
//        verify(responseObserver).onNext(responseCaptor.capture());
//        verify(responseObserver).onCompleted();
//
//        ProductInfo response = responseCaptor.getValue();
//        assertEquals("test-id", response.getId());
//        assertEquals("Test Product", response.getName());
//        assertEquals("Test Description", response.getDescription());
//        assertEquals(100.0, response.getPrice());
//        assertEquals(10, response.getQuantity());
//        assertEquals(1, response.getVersion());
//    }
//
//    @Test
//    void getProductInfo_NotFound() {
//        // Given
//        when(productRepository.findById("non-existent")).thenReturn(Optional.empty());
//        GetProductRequest request = GetProductRequest.newBuilder()
//            .setProductId("non-existent")
//            .build();
//
//        // When
//        productGrpcService.getProductInfo(request, responseObserver);
//
//        // Then
//        ArgumentCaptor<StatusRuntimeException> errorCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
//        verify(responseObserver).onError(errorCaptor.capture());
//        verify(responseObserver, never()).onNext(any());
//        verify(responseObserver, never()).onCompleted();
//
//        StatusRuntimeException error = errorCaptor.getValue();
//        assertEquals(Status.NOT_FOUND.getCode(), error.getStatus().getCode());
//        assertTrue(error.getStatus().getDescription().contains("non-existent"));
//    }
//
//    @Test
//    void getProductInfo_VersionMismatch() {
//        // Given
//        when(productRepository.findById("test-id")).thenReturn(Optional.of(testProduct));
//        GetProductRequest request = GetProductRequest.newBuilder()
//            .setProductId("test-id")
//            .setVersion(2)
//            .build();
//
//        // When
//        productGrpcService.getProductInfo(request, responseObserver);
//
//        // Then
//        ArgumentCaptor<StatusRuntimeException> errorCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
//        verify(responseObserver).onError(errorCaptor.capture());
//        verify(responseObserver, never()).onNext(any());
//        verify(responseObserver, never()).onCompleted();
//
//        StatusRuntimeException error = errorCaptor.getValue();
//        assertEquals(Status.FAILED_PRECONDITION.getCode(), error.getStatus().getCode());
//        assertTrue(error.getStatus().getDescription().contains("Version mismatch"));
//    }
//
//    @Test
//    void validatePriceWithVersion_Success() {
//        // Given
//        when(productRepository.findById("test-id")).thenReturn(Optional.of(testProduct));
//        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
//
//        ValidatePriceRequest request = ValidatePriceRequest.newBuilder()
//            .setProductId("test-id")
//            .setVersion(1)
//            .setPrice(150.0)
//            .build();
//
//        // When
//        productGrpcService.validatePriceWithVersion(request, responseObserver);
//
//        // Then
//        ArgumentCaptor<ProductInfo> responseCaptor = ArgumentCaptor.forClass(ProductInfo.class);
//        verify(responseObserver).onNext(responseCaptor.capture());
//        verify(responseObserver).onCompleted();
//
//        ProductInfo response = responseCaptor.getValue();
//        assertEquals("test-id", response.getId());
//        assertEquals(150.0, response.getPrice());
//    }
//
//    @Test
//    void validatePriceWithVersion_InvalidPrice() {
//        // Given
//        ValidatePriceRequest request = ValidatePriceRequest.newBuilder()
//            .setProductId("test-id")
//            .setVersion(1)
//            .setPrice(-10.0)
//            .build();
//
//        // When
//        productGrpcService.validatePriceWithVersion(request, responseObserver);
//
//        // Then
//        ArgumentCaptor<StatusRuntimeException> errorCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
//        verify(responseObserver).onError(errorCaptor.capture());
//        verify(responseObserver, never()).onNext(any());
//        verify(responseObserver, never()).onCompleted();
//
//        StatusRuntimeException error = errorCaptor.getValue();
//        assertEquals(Status.INVALID_ARGUMENT.getCode(), error.getStatus().getCode());
//        assertTrue(error.getStatus().getDescription().contains("Price must be greater than 0"));
//    }
//
//    @Test
//    void validatePriceWithVersion_VersionMismatch() {
//        // Given
//        when(productRepository.findById("test-id")).thenReturn(Optional.of(testProduct));
//        ValidatePriceRequest request = ValidatePriceRequest.newBuilder()
//            .setProductId("test-id")
//            .setVersion(2)
//            .setPrice(150.0)
//            .build();
//
//        // When
//        productGrpcService.validatePriceWithVersion(request, responseObserver);
//
//        // Then
//        ArgumentCaptor<StatusRuntimeException> errorCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
//        verify(responseObserver).onError(errorCaptor.capture());
//        verify(responseObserver, never()).onNext(any());
//        verify(responseObserver, never()).onCompleted();
//
//        StatusRuntimeException error = errorCaptor.getValue();
//        assertEquals(Status.FAILED_PRECONDITION.getCode(), error.getStatus().getCode());
//        assertTrue(error.getStatus().getDescription().contains("Version mismatch"));
//    }
//}