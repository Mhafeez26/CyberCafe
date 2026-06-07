CREATE DATABASE IF NOT EXISTS cybercafe;
USE cybercafe;

CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INT,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS computer_categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    hourly_rate DOUBLE NOT NULL
);

CREATE TABLE IF NOT EXISTS computers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    category_id INT,
    status VARCHAR(20) DEFAULT 'Available',
    FOREIGN KEY (category_id) REFERENCES computer_categories(id)
);

CREATE TABLE IF NOT EXISTS games (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    computer_id INT,
    FOREIGN KEY (computer_id) REFERENCES computers(id)
);

CREATE TABLE IF NOT EXISTS memberships (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    discount_percent DOUBLE NOT NULL
);

CREATE TABLE IF NOT EXISTS customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    membership_id INT,
    FOREIGN KEY (membership_id) REFERENCES memberships(id)
);

CREATE TABLE IF NOT EXISTS sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    computer_id INT,
    customer_id INT,
    start_time DATETIME,
    end_time DATETIME,
    total_amount DOUBLE DEFAULT 0,
    status VARCHAR(20) DEFAULT 'Active',
    FOREIGN KEY (computer_id) REFERENCES computers(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    computer_id INT,
    customer_id INT,
    reservation_date DATETIME,
    status VARCHAR(20) DEFAULT 'Pending',
    FOREIGN KEY (computer_id) REFERENCES computers(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    position VARCHAR(50),
    salary DOUBLE,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tax_percent DOUBLE DEFAULT 0,
    currency VARCHAR(10) DEFAULT 'PKR',
    receipt_footer VARCHAR(255) DEFAULT 'Thank you for visiting!',
    system_name VARCHAR(100) DEFAULT 'Cyber Cafe'
);

INSERT IGNORE INTO roles (name) VALUES ('Admin'), ('Employee');

INSERT IGNORE INTO users (username, password, role_id) VALUES ('admin', 'admin123', 1);

INSERT IGNORE INTO computer_categories (name, hourly_rate) VALUES
('Normal', 50), ('Gaming', 100), ('VIP', 150);

INSERT IGNORE INTO memberships (name, discount_percent) VALUES
('None', 0), ('Silver', 10), ('Gold', 20), ('Platinum', 30);

INSERT IGNORE INTO settings (tax_percent, currency, receipt_footer, system_name)
VALUES (5, 'PKR', 'Thank you for visiting!', 'Cyber Cafe');
