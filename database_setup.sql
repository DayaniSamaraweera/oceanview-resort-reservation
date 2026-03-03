-- ================================================
-- Ocean View Resort - Database Setup
-- Online Room Reservation System

-- ================================================

DROP DATABASE IF EXISTS oceanview_resort_db;
CREATE DATABASE oceanview_resort_db;
USE oceanview_resort_db;

-- ================================================
-- TABLE: users (RBAC - Admin & Staff)
-- ================================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role ENUM('admin', 'staff') NOT NULL DEFAULT 'staff',
    is_first_login TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================================
-- TABLE: rooms
-- ================================================
CREATE TABLE rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    room_type ENUM('Standard', 'Deluxe', 'Suite', 'Family') NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    capacity INT NOT NULL DEFAULT 2,
    description TEXT,
    status ENUM('Available', 'Occupied', 'Maintenance') NOT NULL DEFAULT 'Available'
);

-- ================================================
-- TABLE: reservations
-- ================================================
CREATE TABLE reservations (
    reservation_id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_number VARCHAR(20) NOT NULL UNIQUE,
    guest_name VARCHAR(100) NOT NULL,
    guest_address TEXT NOT NULL,
    contact_number VARCHAR(15) NOT NULL,
    guest_email VARCHAR(100),
    room_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_nights INT NOT NULL DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status ENUM('Confirmed', 'Checked-In', 'Checked-Out', 'Cancelled') 
        NOT NULL DEFAULT 'Confirmed',
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
        ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- ================================================
-- TABLE: bills
-- ================================================
CREATE TABLE bills (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL UNIQUE,
    room_charge DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) NOT NULL,
    service_charge DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('Pending', 'Paid') NOT NULL DEFAULT 'Pending',
    generated_by INT,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id),
    FOREIGN KEY (generated_by) REFERENCES users(user_id)
);

-- ================================================
-- TABLE: email_logs (Email Simulation)
-- ================================================
CREATE TABLE email_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT,
    recipient_email VARCHAR(100) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    status ENUM('Sent', 'Failed') NOT NULL DEFAULT 'Sent',
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
);

-- ================================================
-- STORED PROCEDURE: Generate Reservation Number
-- ================================================
DELIMITER //
CREATE PROCEDURE GenerateReservationNumber(
    OUT new_reservation_number VARCHAR(20))
BEGIN
    DECLARE next_id INT;
    DECLARE current_year VARCHAR(4);
    SET current_year = YEAR(CURDATE());
    SELECT IFNULL(MAX(reservation_id), 0) + 1 
        INTO next_id FROM reservations;
    SET new_reservation_number = CONCAT(
        'OVR-', current_year, '-', LPAD(next_id, 4, '0'));
END //
DELIMITER ;

-- ================================================
-- STORED PROCEDURE: Calculate Bill
-- ================================================
DELIMITER //
CREATE PROCEDURE CalculateBill(
    IN p_reservation_id INT,
    IN p_generated_by INT)
BEGIN
    DECLARE v_nights INT;
    DECLARE v_rate DECIMAL(10,2);
    DECLARE v_room_charge DECIMAL(10,2);
    DECLARE v_tax DECIMAL(10,2);
    DECLARE v_service DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);

    SELECT r.total_nights, rm.price_per_night
    INTO v_nights, v_rate
    FROM reservations r
    JOIN rooms rm ON r.room_id = rm.room_id
    WHERE r.reservation_id = p_reservation_id;

    SET v_room_charge = v_nights * v_rate;
    SET v_tax = v_room_charge * 0.10;
    SET v_service = v_room_charge * 0.05;
    SET v_total = v_room_charge + v_tax + v_service;

    INSERT INTO bills (reservation_id, room_charge, tax_amount,
        service_charge, total_amount, generated_by)
    VALUES (p_reservation_id, v_room_charge, v_tax,
        v_service, v_total, p_generated_by)
    ON DUPLICATE KEY UPDATE
        room_charge = v_room_charge,
        tax_amount = v_tax,
        service_charge = v_service,
        total_amount = v_total;

    SELECT v_room_charge AS room_charge,
           v_tax AS tax_amount,
           v_service AS service_charge,
           v_total AS total_amount;
END //
DELIMITER ;

-- ================================================
-- STORED PROCEDURE: Get Reservation Details
-- ================================================
DELIMITER //
CREATE PROCEDURE GetReservationDetails(
    IN p_reservation_number VARCHAR(20))
BEGIN
    SELECT r.reservation_id, r.reservation_number,
           r.guest_name, r.guest_address, r.contact_number,
           r.guest_email, rm.room_number, rm.room_type,
           rm.price_per_night, r.check_in_date,
           r.check_out_date, r.total_nights,
           r.total_amount, r.status, r.created_at
    FROM reservations r
    JOIN rooms rm ON r.room_id = rm.room_id
    WHERE r.reservation_number = p_reservation_number;
END //
DELIMITER ;

-- ================================================
-- FUNCTION: Get Available Room Count
-- ================================================
DELIMITER //
CREATE FUNCTION GetAvailableRoomCount(p_room_type VARCHAR(20))
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_count INT;
    SELECT COUNT(*) INTO v_count
    FROM rooms
    WHERE status = 'Available'
    AND (p_room_type = 'ALL' OR room_type = p_room_type);
    RETURN v_count;
END //
DELIMITER ;

-- ================================================
-- TRIGGER: Auto calculate nights and total
-- ================================================
DELIMITER //
CREATE TRIGGER before_reservation_insert
BEFORE INSERT ON reservations
FOR EACH ROW
BEGIN
    DECLARE v_rate DECIMAL(10,2);
    SET NEW.total_nights = 
        DATEDIFF(NEW.check_out_date, NEW.check_in_date);
    SELECT price_per_night INTO v_rate 
        FROM rooms WHERE room_id = NEW.room_id;
    SET NEW.total_amount = NEW.total_nights * v_rate;
END //
DELIMITER ;

-- ================================================
-- TRIGGER: Update room status after reservation
-- ================================================
DELIMITER //
CREATE TRIGGER after_reservation_insert
AFTER INSERT ON reservations
FOR EACH ROW
BEGIN
    IF NEW.status = 'Confirmed' THEN
        UPDATE rooms SET status = 'Occupied' 
            WHERE room_id = NEW.room_id;
    END IF;
END //
DELIMITER ;

-- ================================================
-- TRIGGER: Update room status on reservation change
-- ================================================
DELIMITER //
CREATE TRIGGER after_reservation_update
AFTER UPDATE ON reservations
FOR EACH ROW
BEGIN
    IF NEW.status = 'Checked-Out' 
        OR NEW.status = 'Cancelled' THEN
        UPDATE rooms SET status = 'Available' 
            WHERE room_id = NEW.room_id;
    ELSEIF NEW.status = 'Confirmed' THEN
        UPDATE rooms SET status = 'Occupied' 
            WHERE room_id = NEW.room_id;
    END IF;
END //
DELIMITER ;

-- ================================================
-- SAMPLE DATA: Users
-- ================================================
INSERT INTO users (username, password, full_name, email, role, is_first_login) 
VALUES
('admin', 'admin123', 'System Administrator', 'admin@oceanviewresort.lk', 'admin', 0),
('staff1', 'staff123', 'Kamal Perera', 'kamal@oceanviewresort.lk', 'staff', 0),
('staff2', 'staff123', 'Nimali Silva', 'nimali@oceanviewresort.lk', 'staff', 1);

-- ================================================
-- SAMPLE DATA: Rooms
-- ================================================
INSERT INTO rooms (room_number, room_type, price_per_night, capacity, description) 
VALUES
('101', 'Standard', 5000.00, 2, 'Comfortable standard room with garden view'),
('102', 'Standard', 5000.00, 2, 'Standard room with garden view'),
('103', 'Standard', 5500.00, 2, 'Standard room with partial ocean view'),
('201', 'Deluxe', 8500.00, 2, 'Spacious deluxe room with ocean view'),
('202', 'Deluxe', 8500.00, 2, 'Deluxe room with balcony and ocean view'),
('203', 'Deluxe', 9000.00, 3, 'Premium deluxe room with panoramic view'),
('301', 'Suite', 15000.00, 4, 'Luxury suite with private balcony and ocean view'),
('302', 'Suite', 18000.00, 4, 'Presidential suite with jacuzzi and terrace'),
('401', 'Family', 12000.00, 6, 'Large family room with two bedrooms'),
('402', 'Family', 12000.00, 6, 'Family room with kids play area');

-- ================================================
-- SAMPLE DATA: Test Reservation
-- ================================================
INSERT INTO reservations 
(reservation_number, guest_name, guest_address, contact_number, 
guest_email, room_id, check_in_date, check_out_date, created_by)
VALUES 
('OVR-2025-0001', 'Nimal Jayawardena', 'No 45, Galle Road, Colombo 03',
'0771234567', 'nimal@gmail.com', 4, '2025-07-10', '2025-07-13', 1);