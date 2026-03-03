-- ============================================
-- Ocean View Resort - Database Setup Script
-- Online Room Reservation System
-- Location: Galle, Sri Lanka
-- Developer: Imasha
-- Final Year Project - Software Engineering
-- ============================================

DROP DATABASE IF EXISTS oceanview_resort;
CREATE DATABASE oceanview_resort;
USE oceanview_resort;

-- ============================================
-- TABLE: users
-- Stores system user accounts (Admin & Staff)
-- ============================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'receptionist',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_first_login TINYINT(1) DEFAULT 1
);

-- ============================================
-- TABLE: rooms
-- Stores hotel room information and rates
-- ============================================
CREATE TABLE rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    room_type VARCHAR(30) NOT NULL,
    rate_per_night DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Available',
    description TEXT
);

-- ============================================
-- TABLE: reservations
-- Stores guest booking information
-- ============================================
CREATE TABLE reservations (
    reservation_id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_number VARCHAR(20) NOT NULL UNIQUE,
    guest_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact_number VARCHAR(15) NOT NULL,
    guest_email VARCHAR(100),
    room_id INT NOT NULL,
    room_type VARCHAR(30) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_nights INT NOT NULL DEFAULT 0,
    total_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'Confirmed',
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- ============================================
-- TABLE: bills
-- Stores generated invoice records
-- ============================================
CREATE TABLE bills (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL,
    reservation_number VARCHAR(20) NOT NULL,
    guest_name VARCHAR(100) NOT NULL,
    room_type VARCHAR(30) NOT NULL,
    room_number VARCHAR(10) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_nights INT NOT NULL,
    rate_per_night DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    generated_by INT,
    FOREIGN KEY (reservation_id)
        REFERENCES reservations(reservation_id),
    FOREIGN KEY (generated_by) REFERENCES users(user_id)
);

-- ============================================
-- TABLE: email_logs
-- Tracks email notification history
-- ============================================
CREATE TABLE email_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    recipient VARCHAR(100),
    subject VARCHAR(200),
    message TEXT,
    status VARCHAR(20),
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- STORED PROCEDURE: GenerateReservationNumber
-- Creates unique booking reference number
-- Format: OVR-YYYY-XXXX
-- ============================================
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

-- ============================================
-- STORED PROCEDURE: CalculateBill
-- Computes total cost and creates invoice
-- ============================================
DELIMITER //
CREATE PROCEDURE CalculateBill(
    IN p_reservation_id INT,
    IN p_generated_by INT)
BEGIN
    DECLARE v_reservation_number VARCHAR(20);
    DECLARE v_guest_name VARCHAR(100);
    DECLARE v_room_type VARCHAR(30);
    DECLARE v_room_number VARCHAR(10);
    DECLARE v_check_in DATE;
    DECLARE v_check_out DATE;
    DECLARE v_nights INT;
    DECLARE v_rate DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);

    SELECT r.reservation_number, r.guest_name, r.room_type,
           rm.room_number, r.check_in_date, r.check_out_date,
           r.number_of_nights, rm.rate_per_night
    INTO v_reservation_number, v_guest_name, v_room_type,
         v_room_number, v_check_in, v_check_out,
         v_nights, v_rate
    FROM reservations r
    JOIN rooms rm ON r.room_id = rm.room_id
    WHERE r.reservation_id = p_reservation_id;

    SET v_total = v_nights * v_rate;

    INSERT INTO bills (reservation_id, reservation_number,
        guest_name, room_type, room_number, check_in_date,
        check_out_date, number_of_nights, rate_per_night,
        total_amount, generated_by)
    VALUES (p_reservation_id, v_reservation_number,
        v_guest_name, v_room_type, v_room_number,
        v_check_in, v_check_out, v_nights, v_rate,
        v_total, p_generated_by);

    SELECT v_total AS total_amount;
END //
DELIMITER ;

-- ============================================
-- STORED PROCEDURE: GetReservationDetails
-- Retrieves complete booking information
-- ============================================
DELIMITER //
CREATE PROCEDURE GetReservationDetails(
    IN p_reservation_number VARCHAR(20))
BEGIN
    SELECT r.reservation_id, r.reservation_number,
           r.guest_name, r.address, r.contact_number,
           r.room_type, rm.room_number, r.check_in_date,
           r.check_out_date, r.number_of_nights,
           r.total_cost, r.status, rm.rate_per_night,
           r.created_at
    FROM reservations r
    JOIN rooms rm ON r.room_id = rm.room_id
    WHERE r.reservation_number = p_reservation_number;
END //
DELIMITER ;

-- ============================================
-- FUNCTION: GetAvailableRoomCount
-- Returns number of available rooms by type
-- ============================================
DELIMITER //
CREATE FUNCTION GetAvailableRoomCount(p_room_type VARCHAR(30))
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE available_count INT;
    SELECT COUNT(*) INTO available_count
    FROM rooms
    WHERE room_type = p_room_type AND status = 'Available';
    RETURN available_count;
END //
DELIMITER ;

-- ============================================
-- TRIGGER: before_reservation_insert
-- Auto-calculates nights and total cost
-- ============================================
DELIMITER //
CREATE TRIGGER before_reservation_insert
BEFORE INSERT ON reservations
FOR EACH ROW
BEGIN
    DECLARE v_rate DECIMAL(10,2);
    SET NEW.number_of_nights =
        DATEDIFF(NEW.check_out_date, NEW.check_in_date);
    SELECT rate_per_night INTO v_rate
        FROM rooms WHERE room_id = NEW.room_id;
    SET NEW.total_cost = NEW.number_of_nights * v_rate;
END //
DELIMITER ;

-- ============================================
-- TRIGGER: after_reservation_insert
-- Updates room status when booking confirmed
-- ============================================
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

-- ============================================
-- TRIGGER: after_reservation_update
-- Updates room status on booking changes
-- ============================================
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

-- ============================================
-- SAMPLE DATA: System Users
-- ============================================
INSERT INTO users (username, password, full_name,
    role, is_first_login) VALUES
('admin', 'admin123', 'Resort Manager',
    'admin', 0),
('reception1', 'rec123', 'Kasun Fernando',
    'receptionist', 0),
('reception2', 'rec456', 'Nimasha Perera',
    'receptionist', 0);

-- ============================================
-- SAMPLE DATA: Hotel Rooms
-- Categories: Standard, Superior, Premium, Executive
-- ============================================
INSERT INTO rooms (room_number, room_type,
    rate_per_night, status, description) VALUES
('101', 'Standard', 5500.00, 'Available',
    'Cozy standard room overlooking the garden'),
('102', 'Standard', 5500.00, 'Available',
    'Comfortable standard room with modern amenities'),
('103', 'Standard', 5500.00, 'Available',
    'Peaceful standard room with natural lighting'),
('201', 'Superior', 8500.00, 'Available',
    'Spacious superior room with ocean view'),
('202', 'Superior', 8500.00, 'Available',
    'Elegant superior room with private balcony'),
('203', 'Superior', 8500.00, 'Available',
    'Relaxing superior room with sea breeze'),
('301', 'Premium', 13000.00, 'Available',
    'Luxurious premium room with panoramic view'),
('302', 'Premium', 13000.00, 'Available',
    'Stunning premium room with sunset view'),
('401', 'Executive', 22000.00, 'Available',
    'Grand executive suite with living area and ocean view'),
('402', 'Executive', 22000.00, 'Available',
    'Royal executive suite with jacuzzi and terrace');

-- ============================================
-- SAMPLE DATA: Test Reservations
-- ============================================
INSERT INTO reservations (reservation_number, guest_name,
    address, contact_number, guest_email, room_id,
    room_type, check_in_date, check_out_date, created_by)
VALUES
('OVR-2025-0001', 'Saman Kumara',
    '123 Temple Road, Kandy', '0772345678',
    'saman.kumara@email.com', 4, 'Superior',
    '2025-07-20', '2025-07-23', 1),
('OVR-2025-0002', 'Dilini Jayawardena',
    '56 Marine Drive, Galle', '0761234567',
    'dilini.j@email.com', 7, 'Premium',
    '2025-07-22', '2025-07-25', 2),
('OVR-2025-0003', 'Ruwan Bandara',
    '78 Peradeniya Road, Kandy', '0751234567',
    'ruwan.b@email.com', 1, 'Standard',
    '2025-07-18', '2025-07-20', 2);

-- ============================================
-- END OF DATABASE SETUP SCRIPT
-- ============================================