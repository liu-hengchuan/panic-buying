-- Create goods table
CREATE TABLE IF NOT EXISTS goods (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    title TEXT NOT NULL,
    image TEXT NOT NULL,
    price REAL NOT NULL,
    seckill_price REAL NOT NULL,
    total_stock INTEGER NOT NULL,
    available_stock INTEGER NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status INTEGER NOT NULL,
    countdown INTEGER NOT NULL
);

-- Create order table
CREATE TABLE IF NOT EXISTS orders (
    order_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    goods_id TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    price REAL NOT NULL,
    status INTEGER NOT NULL,
    create_time DATETIME NOT NULL,
    pay_time DATETIME
);

-- Create sold users table to track which users have purchased which goods
CREATE TABLE IF NOT EXISTS sold_users (
    goods_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    PRIMARY KEY (goods_id, user_id)
);

-- Insert test data
INSERT OR IGNORE INTO goods (id, name, title, image, price, seckill_price, total_stock, available_stock, start_time, end_time, status, countdown)
VALUES
('goods001', 'iPhone 15', 'iPhone 15 128GB 秒杀', 'iphone15.jpg', 5999.0, 4999.0, 100, 100, '2024-01-01 10:00:00', '2024-12-31 23:59:59', 1, 0),
('goods002', 'iPad Pro', 'iPad Pro 11英寸 秒杀', 'ipadpro.jpg', 6999.0, 5999.0, 50, 50, '2024-01-01 10:00:00', '2024-12-31 23:59:59', 1, 0),
('goods003', 'MacBook Air', 'MacBook Air M2 秒杀', 'macbookair.jpg', 7999.0, 6999.0, 30, 30, '2024-01-01 10:00:00', '2024-12-31 23:59:59', 1, 0);