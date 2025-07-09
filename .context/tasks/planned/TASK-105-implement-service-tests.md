---
title: "Task 1.5: Viết Unit & Integration Test cho Product Service"
type: "task"
status: "planned"
created: "2024-07-09T08:35:00"
updated: "2024-07-09T09:15:00"
id: "TASK-105"
priority: "medium"
dependencies: ["TASK-104"]
tags: ["test", "unittest", "integrationtest", "junit", "mockito", "product-service"]
---

## Description
Để đảm bảo chất lượng và sự ổn định, nhiệm vụ này tập trung vào việc viết hai loại test quan trọng: Unit Test để kiểm tra logic của `ProductGrpcServiceImpl` một cách riêng rẽ, và Integration Test để xác minh sự tương tác của toàn bộ service từ gRPC call đến lớp cơ sở dữ liệu.

## Objectives
- Viết Unit Test cho `ProductGrpcServiceImpl`, mock các dependency để cô lập logic cần kiểm tra.
- Viết Integration Test để kiểm tra toàn bộ luồng hoạt động của service trong một môi trường gần giống production.
- Đảm bảo các kịch bản thành công và thất bại đều được bao phủ.

## Checklist

### Nhóm: Unit Test
- [ ] **Bước 1:** Thêm dependency `grpc-testing`.
    - **Vị trí:** `product-service/pom.xml` trong thẻ `<dependencies>`.
    - **Code:**
      ```xml
      <dependency>
          <groupId>io.grpc</groupId>
          <artifactId>grpc-testing</artifactId>
          <version>${grpc.version}</version>
          <scope>test</scope>
      </dependency>
      ```
- [ ] **Bước 2:** Tạo file test `ProductGrpcServiceImplTest.java`.
    - **Vị trí:** `product-service/src/test/java/com/example/productservice/grpc/ProductGrpcServiceImplTest.java`
    - **Notes:** Sử dụng Mockito để mock `ProductRepository`.
    - **Code Mẫu:**
      ```java
      package com.example.productservice.grpc;

      import com.example.product.grpc.GetProductRequest;
      import com.example.product.grpc.ProductInfo;
      import com.example.productservice.domain.Product;
      import com.example.productservice.repository.ProductRepository;
      import io.grpc.stub.StreamObserver;
      import org.junit.jupiter.api.Test;
      import org.junit.jupiter.api.extension.ExtendWith;
      import org.mockito.InjectMocks;
      import org.mockito.Mock;
      import org.mockito.junit.jupiter.MockitoExtension;

      import java.util.Optional;

      import static org.mockito.Mockito.*;

      @ExtendWith(MockitoExtension.class)
      class ProductGrpcServiceImplTest {

          @Mock
          private ProductRepository productRepository;

          @Mock
          private StreamObserver<ProductInfo> responseObserver;

          @InjectMocks
          private ProductGrpcServiceImpl productService;

          @Test
          void getProductInfo_whenProductExists_shouldReturnProductInfo() {
              // Given
              String productId = "test-id";
              Product product = new Product(productId, "Test Product", "Desc", 100.0, 10);
              GetProductRequest request = GetProductRequest.newBuilder().setProductId(productId).build();

              when(productRepository.findById(productId)).thenReturn(Optional.of(product));

              // When
              productService.getProductInfo(request, responseObserver);

              // Then
              verify(responseObserver, times(1)).onNext(any(ProductInfo.class));
              verify(responseObserver, never()).onError(any());
              verify(responseObserver, times(1)).onCompleted();
          }

          @Test
          void getProductInfo_whenProductNotFound_shouldReturnError() {
              // Given
              String productId = "non-existent-id";
              GetProductRequest request = GetProductRequest.newBuilder().setProductId(productId).build();

              when(productRepository.findById(productId)).thenReturn(Optional.empty());

              // When
              productService.getProductInfo(request, responseObserver);

              // Then
              verify(responseObserver, never()).onNext(any());
              verify(responseObserver, times(1)).onError(any());
              verify(responseObserver, times(1)).onCompleted();
          }
      }
      ```

### Nhóm: Integration Test
- [ ] **Bước 3:** Tạo gRPC client cho việc test.
    - **Notes:** Sử dụng `ManagedChannel` để kết nối đến gRPC server đang chạy trong môi trường test.
- [ ] **Bước 4:** Tạo file test `ProductGrpcServiceIT.java`.
    - **Vị trí:** `product-service/src/test/java/com/example/productservice/grpc/ProductGrpcServiceIT.java`
    - **Notes:** Sử dụng `@SpringBootTest` để khởi chạy toàn bộ context của Spring. `@GrpcTest` của LogNet cũng là một lựa chọn tốt.
    - **Code Mẫu:**
      ```java
      package com.example.productservice.grpc;

      import com.example.product.grpc.GetProductRequest;
      import com.example.product.grpc.ProductInfo;
      import com.example.product.grpc.ProductServiceGrpc;
      import com.example.productservice.domain.Product;
      import com.example.productservice.repository.ProductRepository;
      import io.grpc.ManagedChannel;
      import io.grpc.inprocess.InProcessChannelBuilder;
      import io.grpc.inprocess.InProcessServerBuilder;
      import io.grpc.testing.GrpcCleanupRule;
      import org.junit.Rule;
      import org.junit.jupiter.api.BeforeEach;
      import org.junit.jupiter.api.Test;
      import org.springframework.beans.factory.annotation.Autowired;
      import org.springframework.boot.test.context.SpringBootTest;
      import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

      import static org.junit.jupiter.api.Assertions.*;

      @SpringBootTest
      class ProductGrpcServiceIT {

          @Autowired
          private ProductRepository productRepository;
          
          @Autowired
          private ProductGrpcServiceImpl productGrpcService;

          private ProductServiceGrpc.ProductServiceBlockingStub client;

          @Rule
          public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

          @BeforeEach
          void setUp() throws Exception {
              String serverName = InProcessServerBuilder.generateName();
              grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(productGrpcService).build().start());
              ManagedChannel channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
              client = ProductServiceGrpc.newBlockingStub(channel);
              
              productRepository.deleteAll();
          }

          @Test
          void getProductInfo_integrationTest() {
              // Given
              Product savedProduct = productRepository.save(new Product(null, "Integration Test Product", "Desc", 150.0, 50));
              GetProductRequest request = GetProductRequest.newBuilder().setProductId(savedProduct.getId()).build();

              // When
              ProductInfo response = client.getProductInfo(request);

              // Then
              assertNotNull(response);
              assertEquals(savedProduct.getId(), response.getId());
              assertEquals("Integration Test Product", response.getName());
          }
      }
      ```

## Key Considerations
- **Unit vs. Integration:** Hiểu rõ sự khác biệt. Unit test nhanh, cô lập, và kiểm tra logic của một lớp duy nhất (với các dependency được mock). Integration test chậm hơn, nhưng nó xác minh sự tương tác giữa nhiều lớp (service, repository, CSDL) trong một môi trường thực tế hơn.
- **In-process Server:** `grpc-testing` cung cấp `InProcessServerBuilder` cho phép chạy một gRPC server thật sự ngay trong quá trình test mà không cần mở port TCP/IP. Điều này làm cho integration test nhanh và đáng tin cậy hơn.

## Current Status
- **Not Started** 