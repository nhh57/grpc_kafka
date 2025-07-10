-- Ghi chú: File này sẽ được Spring Boot tự động thực thi khi khởi động.
-- Thêm một sản phẩm mẫu để kiểm tra gRPC endpoint.

-- Tạo một UUID ngẫu nhiên cho sản phẩm, ví dụ: 40e6215d-b5c6-489a-983c-64265a98f895
INSERT INTO product (id, name, description, price, quantity, version) VALUES ('40e6215d-b5c6-489a-983c-64265a98f895', 'Sample Laptop', 'A powerful laptop for developers', 1500.75, 50, 0);

INSERT INTO product (id, name, description, price, quantity, version) VALUES ('a1b2c3d4-e5f6-7890-1234-567890abcdef', 'Mechanical Keyboard', 'A great keyboard for typing', 120.50, 100, 0);