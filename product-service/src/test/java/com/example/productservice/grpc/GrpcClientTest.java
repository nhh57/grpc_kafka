package com.example.productservice.grpc;

import com.example.product.grpc.GetProductRequest;
import com.example.product.grpc.ProductInfo;
import com.example.product.grpc.ProductServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/**
 * Simple gRPC client to test ProductService
 * Run this manually to test the gRPC service
 */
public class GrpcClientTest {

    public static void main(String[] args) {
        System.out.println("=== Testing Product gRPC Service ===");
        
        // Create channel to gRPC server
        ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost", 6565)
            .usePlaintext()
            .build();
        
        // Create blocking stub
        ProductServiceGrpc.ProductServiceBlockingStub stub = 
            ProductServiceGrpc.newBlockingStub(channel);
        
        try {
            // Test 1: Test with non-existent product ID
            System.out.println("\n1. Testing with non-existent product ID:");
            testGetProduct(stub, "non-existent-id");
            
            // Test 2: Test with empty product ID
            System.out.println("\n2. Testing with empty product ID:");
            testGetProduct(stub, "");
            
            // Test 3: Test with valid product ID
            System.out.println("\n3. Testing with valid product ID:");
            testGetProduct(stub, "test-123");
            
        } finally {
            // Shutdown channel
            channel.shutdown();
            System.out.println("\n=== Test completed ===");
        }
    }
    
    private static void testGetProduct(ProductServiceGrpc.ProductServiceBlockingStub stub, String productId) {
        try {
            GetProductRequest request = GetProductRequest.newBuilder()
                .setProductId(productId)
                .build();
                
            System.out.printf("Request: product_id = \"%s\"%n", productId);
            
            ProductInfo response = stub.getProductInfo(request);
            
            System.out.println("✅ Success - Response:");
            System.out.printf("  ID: %s%n", response.getId());
            System.out.printf("  Name: %s%n", response.getName());
            System.out.printf("  Description: %s%n", response.getDescription());
            System.out.printf("  Price: %.2f%n", response.getPrice());
            System.out.printf("  Quantity: %d%n", response.getQuantity());
            
        } catch (StatusRuntimeException e) {
            System.out.printf("❌ Error: %s - %s%n", e.getStatus().getCode(), e.getStatus().getDescription());
        } catch (Exception e) {
            System.out.printf("❌ Unexpected error: %s%n", e.getMessage());
        }
    }
} 