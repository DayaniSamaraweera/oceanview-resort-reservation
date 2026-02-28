 
-- ================================================
-- Ocean View Resort - Database Setup Script
-- Developer: D.I.Samaraweera
-- Module: CIS6003 - Advanced Programming
-- ================================================

CREATE DATABASE IF NOT EXISTS oceanview_resort;
USE oceanview_resort;

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role ENUM('admin', 'receptionist') NOT NULL,
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

CREATE TABLE room_types (
    type_id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) NOT NULL,
    rate_per_night DECIMAL(10,2) NOT NULL,
    capacity INT NOT NULL,
    description TEXT
);

CREATE TABLE rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    type_id INT NOT NULL,
    status ENUM('available','occupied','maintenance','cleaning') DEFAULT 'available',
    FOREIGN KEY (type_id) REFERENCES room_types(type_id)
);

CREATE TABLE guests (
    guest_id INT PRIMARY KEY AUTO_INCREMENT,
    guest_name VARCHAR(100) NOT NULL,
    address TEXT,
    contact_number VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reservations (
    reservation_id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_number VARCHAR(20) UNIQUE NOT NULL,
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    status ENUM('pending','confirmed','checked_in','checked_out','cancelled') DEFAULT 'pending',
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

INSERT INTO room_types (type_name, rate_per_night, capacity, description) VALUES
('Single', 5000.00, 1, 'Comfortable single occupancy room with garden view'),
('Double', 8000.00, 2, 'Spacious double occupancy room with balcony'),
('Deluxe', 12000.00, 2, 'Deluxe room with stunning ocean view'),
('Suite', 20000.00, 4, 'Luxury suite with premium amenities and private pool');

INSERT INTO users (username, password, full_name, email, role) VALUES
('admin', 'admin123', 'System Administrator', 'admin@oceanview.lk', 'admin');