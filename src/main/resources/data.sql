INSERT INTO member (name, city, street, zipcode) VALUES ('John Doe', 'New York', '123 Main St', '10001');
INSERT INTO member (name, city, street, zipcode) VALUES ('Jane Smith', 'Los Angeles', '456 Oak Ave', '90001');

INSERT INTO product (name, price, stock_quantity) VALUES ('Laptop', 1200, 10);
INSERT INTO product (name, price, stock_quantity) VALUES ('Mouse', 25, 100);
INSERT INTO product (name, price, stock_quantity) VALUES ('Keyboard', 75, 50);

INSERT INTO orders (member_id, order_date, status) VALUES (1, NOW(), 'ORDER');
INSERT INTO orders (member_id, order_date, status) VALUES (2, NOW(), 'ORDER');

INSERT INTO order_item (order_id, product_id, order_price, count) VALUES (1, 1, 1200, 1);
INSERT INTO order_item (order_id, product_id, order_price, count) VALUES (1, 2, 25, 2);
INSERT INTO order_item (order_id, product_id, order_price, count) VALUES (2, 3, 75, 1);
