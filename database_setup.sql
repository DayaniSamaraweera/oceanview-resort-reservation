-- ============================================================
-- Ocean View Resort - Online Room Reservation System
-- Database Setup Script
-- Author: Dayani Samaraweera
-- Database: MySQL 8.0
-- ============================================================

DROP DATABASE IF EXISTS oceanview_resort_db;
CREATE DATABASE oceanview_resort_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE oceanview_resort_db;


-- ============================================================
-- TABLE: users
-- Purpose: Stores system user credentials and role information
-- Assumption: Two roles exist - ADMIN (Resort Manager) has
--   full system access, RECEPTIONIST (Staff) has limited access
-- Security: Passwords stored as SHA-256 hashes
-- ============================================================
CREATE TABLE users (
    user_id          INT AUTO_INCREMENT PRIMARY KEY,
    username         VARCHAR(50)  NOT NULL UNIQUE,
    password_hash    VARCHAR(64)  NOT NULL,
    full_name        VARCHAR(100) NOT NULL,
    user_role        ENUM('ADMIN', 'RECEPTIONIST') NOT NULL,
    email_address    VARCHAR(100),
    is_active        TINYINT(1)   DEFAULT 1,
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;


-- ============================================================
-- TABLE: rooms
-- Purpose: Stores room inventory with types and nightly rates
-- Assumption: Ocean View Resort has 14 rooms across 4 floors
--   Floor 1: Standard rooms (LKR 5,500)
--   Floor 2: Superior rooms (LKR 8,500)
--   Floor 3: Premium rooms  (LKR 13,000)
--   Floor 4: Executive suites (LKR 22,000)
-- ============================================================
CREATE TABLE rooms (
    room_id          INT AUTO_INCREMENT PRIMARY KEY,
    room_number      VARCHAR(10)    NOT NULL UNIQUE,
    room_type        ENUM('Standard', 'Superior', 'Premium', 'Executive') NOT NULL,
    rate_per_night   DECIMAL(10,2)  NOT NULL,
    is_available     TINYINT(1)     DEFAULT 1,
    floor_number     INT            NOT NULL,
    max_guests       INT            DEFAULT 2,
    room_description TEXT,
    created_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


-- ============================================================
-- TABLE: reservations
-- Purpose: Stores guest booking information
-- Status Flow: Confirmed -> Checked-In -> Checked-Out
--              Confirmed -> Cancelled (with reason)
-- Requirement Traceability: Maps to "Add New Reservation"
--   feature collecting reservation number, guest name, address,
--   contact number, room type, check-in and check-out dates
-- ============================================================
CREATE TABLE reservations (
    reservation_id     INT AUTO_INCREMENT PRIMARY KEY,
    reservation_number VARCHAR(20)  NOT NULL UNIQUE,
    guest_name         VARCHAR(100) NOT NULL,
    address            VARCHAR(255) NOT NULL,
    contact_number     VARCHAR(15)  NOT NULL,
    guest_email        VARCHAR(100),
    room_id            INT          NOT NULL,
    room_type          ENUM('Standard', 'Superior', 'Premium', 'Executive') NOT NULL,
    check_in_date      DATE         NOT NULL,
    check_out_date     DATE         NOT NULL,
    number_of_nights   INT          DEFAULT 0,
    reservation_status ENUM('Confirmed', 'Checked-In', 'Checked-Out', 'Cancelled')
                       DEFAULT 'Confirmed',
    cancel_reason      VARCHAR(255),
    created_by         INT,
    created_at         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_res_room
        FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    CONSTRAINT fk_res_creator
        FOREIGN KEY (created_by) REFERENCES users(user_id)
) ENGINE=InnoDB;


-- ============================================================
-- TABLE: bills
-- Purpose: Stores generated invoice and billing information
-- Requirement Traceability: Maps to "Calculate and Print Bill"
--   computing total stay cost based on nights and room rates
-- ============================================================
CREATE TABLE bills (
    bill_id            INT AUTO_INCREMENT PRIMARY KEY,
    bill_number        VARCHAR(20)   NOT NULL UNIQUE,
    reservation_id     INT           NOT NULL,
    reservation_number VARCHAR(20)   NOT NULL,
    guest_name         VARCHAR(100)  NOT NULL,
    room_type          ENUM('Standard', 'Superior', 'Premium', 'Executive') NOT NULL,
    rate_per_night     DECIMAL(10,2) NOT NULL,
    number_of_nights   INT           NOT NULL,
    subtotal           DECIMAL(10,2) NOT NULL,
    tax_percentage     DECIMAL(5,2)  DEFAULT 0.00,
    tax_amount         DECIMAL(10,2) DEFAULT 0.00,
    total_amount       DECIMAL(10,2) NOT NULL,
    generated_by       INT,
    generated_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bill_reservation
        FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id),
    CONSTRAINT fk_bill_generator
        FOREIGN KEY (generated_by) REFERENCES users(user_id)
) ENGINE=InnoDB;


-- ============================================================
-- TABLE: audit_log
-- Purpose: Tracks all system activities for security auditing
-- Assumption: Every create, update, delete action is logged
--   to support management oversight and accountability
-- ============================================================
CREATE TABLE audit_log (
    log_id             INT AUTO_INCREMENT PRIMARY KEY,
    user_id            INT,
    username           VARCHAR(50),
    action_type        VARCHAR(50)  NOT NULL,
    action_description VARCHAR(500),
    target_table       VARCHAR(50),
    target_record_id   INT,
    ip_address         VARCHAR(45),
    action_timestamp   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


-- ============================================================
-- STORED PROCEDURE: GenerateReservationNumber
-- Purpose: Generates unique reservation numbers
-- Format: RES-YYYY-NNNNN (e.g., RES-2026-00001)
-- ============================================================
DELIMITER //
CREATE PROCEDURE GenerateReservationNumber(
    OUT generated_number VARCHAR(20)
)
BEGIN
    DECLARE sequence_value INT;
    DECLARE year_prefix VARCHAR(4);

    SET year_prefix = YEAR(CURDATE());

    SELECT COALESCE(
        MAX(CAST(SUBSTRING(reservation_number, 10) AS UNSIGNED)), 0
    ) + 1
    INTO sequence_value
    FROM reservations
    WHERE reservation_number LIKE CONCAT('RES-', year_prefix, '-%');

    SET generated_number = CONCAT(
        'RES-', year_prefix, '-', LPAD(sequence_value, 5, '0')
    );
END //
DELIMITER ;


-- ============================================================
-- STORED PROCEDURE: CalculateBill
-- Purpose: Computes total stay cost based on nights and room rate
-- Requirement: "Calculate and Print Bill" feature
-- ============================================================
DELIMITER //
CREATE PROCEDURE CalculateBill(
    IN  p_reservation_id  INT,
    OUT p_bill_number     VARCHAR(20),
    OUT p_subtotal        DECIMAL(10,2),
    OUT p_tax_amount      DECIMAL(10,2),
    OUT p_total_amount    DECIMAL(10,2)
)
BEGIN
    DECLARE v_rate_per_night  DECIMAL(10,2);
    DECLARE v_nights          INT;
    DECLARE v_tax_percentage  DECIMAL(5,2) DEFAULT 0.00;
    DECLARE v_bill_sequence   INT;
    DECLARE v_year_prefix     VARCHAR(4);

    -- Retrieve room rate and number of nights from reservation
    SELECT rm.rate_per_night, res.number_of_nights
    INTO v_rate_per_night, v_nights
    FROM reservations res
    INNER JOIN rooms rm ON res.room_id = rm.room_id
    WHERE res.reservation_id = p_reservation_id;

    -- Calculate billing amounts
    SET p_subtotal    = v_rate_per_night * v_nights;
    SET p_tax_amount  = p_subtotal * (v_tax_percentage / 100);
    SET p_total_amount = p_subtotal + p_tax_amount;

    -- Generate unique bill number (BILL-YYYY-NNNNN)
    SET v_year_prefix = YEAR(CURDATE());

    SELECT COALESCE(
        MAX(CAST(SUBSTRING(bill_number, 11) AS UNSIGNED)), 0
    ) + 1
    INTO v_bill_sequence
    FROM bills
    WHERE bill_number LIKE CONCAT('BILL-', v_year_prefix, '-%');

    SET p_bill_number = CONCAT(
        'BILL-', v_year_prefix, '-', LPAD(v_bill_sequence, 5, '0')
    );
END //
DELIMITER ;


-- ============================================================
-- STORED PROCEDURE: GetReservationDetails
-- Purpose: Retrieves complete booking information for display
-- Requirement: "Display Reservation Details" feature
-- ============================================================
DELIMITER //
CREATE PROCEDURE GetReservationDetails(
    IN p_reservation_number VARCHAR(20)
)
BEGIN
    SELECT
        res.reservation_id,
        res.reservation_number,
        res.guest_name,
        res.address,
        res.contact_number,
        res.guest_email,
        rm.room_number,
        res.room_type,
        rm.rate_per_night,
        res.check_in_date,
        res.check_out_date,
        res.number_of_nights,
        res.reservation_status,
        res.cancel_reason,
        u.full_name AS created_by_name,
        res.created_at
    FROM reservations res
    INNER JOIN rooms rm ON res.room_id = rm.room_id
    LEFT JOIN users u ON res.created_by = u.user_id
    WHERE res.reservation_number = p_reservation_number;
END //
DELIMITER ;


-- ============================================================
-- FUNCTION: GetAvailableRoomCount
-- Purpose: Returns count of available rooms by type
-- Usage: SELECT GetAvailableRoomCount('Standard');
--        SELECT GetAvailableRoomCount('ALL');
-- ============================================================
DELIMITER //
CREATE FUNCTION GetAvailableRoomCount(
    p_room_type VARCHAR(20)
)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE available_count INT;

    IF p_room_type IS NULL OR p_room_type = '' OR p_room_type = 'ALL' THEN
        SELECT COUNT(*) INTO available_count
        FROM rooms
        WHERE is_available = 1;
    ELSE
        SELECT COUNT(*) INTO available_count
        FROM rooms
        WHERE is_available = 1 AND room_type = p_room_type;
    END IF;

    RETURN available_count;
END //
DELIMITER ;


-- ============================================================
-- TRIGGER: before_reservation_insert
-- Purpose: Validates booking data before saving
--   1. Check-out date must be after check-in date
--   2. Check-in date cannot be in the past
--   3. Automatically calculates number of nights
--   4. Detects booking conflicts (same room, overlapping dates)
-- ============================================================
DELIMITER //
CREATE TRIGGER before_reservation_insert
BEFORE INSERT ON reservations
FOR EACH ROW
BEGIN
    DECLARE overlap_count INT;

    -- Validation: check-out must be after check-in
    IF NEW.check_out_date <= NEW.check_in_date THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Check-out date must be after check-in date';
    END IF;

    -- Validation: check-in cannot be in the past
    IF NEW.check_in_date < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Check-in date cannot be in the past';
    END IF;

    -- Auto-calculate number of nights
    SET NEW.number_of_nights = DATEDIFF(NEW.check_out_date, NEW.check_in_date);

    -- Booking conflict detection: same room, overlapping dates
    SELECT COUNT(*) INTO overlap_count
    FROM reservations
    WHERE room_id = NEW.room_id
      AND reservation_status IN ('Confirmed', 'Checked-In')
      AND NEW.check_in_date < check_out_date
      AND NEW.check_out_date > check_in_date;

    IF overlap_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Room is already booked for the selected dates';
    END IF;
END //
DELIMITER ;


-- ============================================================
-- TRIGGER: after_reservation_insert
-- Purpose: Updates room availability and creates audit log
-- ============================================================
DELIMITER //
CREATE TRIGGER after_reservation_insert
AFTER INSERT ON reservations
FOR EACH ROW
BEGIN
    -- If check-in is today, mark room as unavailable
    IF NEW.check_in_date = CURDATE() THEN
        UPDATE rooms SET is_available = 0
        WHERE room_id = NEW.room_id;
    END IF;

    -- Record action in audit trail
    INSERT INTO audit_log (
        user_id, username, action_type,
        action_description, target_table, target_record_id
    )
    VALUES (
        NEW.created_by,
        (SELECT username FROM users WHERE user_id = NEW.created_by),
        'CREATE_RESERVATION',
        CONCAT('New reservation ', NEW.reservation_number,
               ' created for guest: ', NEW.guest_name),
        'reservations',
        NEW.reservation_id
    );
END //
DELIMITER ;


-- ============================================================
-- TRIGGER: after_reservation_update
-- Purpose: Handles room availability on status changes
--   and logs all modifications to the audit trail
-- ============================================================
DELIMITER //
CREATE TRIGGER after_reservation_update
AFTER UPDATE ON reservations
FOR EACH ROW
BEGIN
    -- Release room when checked-out or cancelled
    IF NEW.reservation_status IN ('Checked-Out', 'Cancelled')
       AND OLD.reservation_status NOT IN ('Checked-Out', 'Cancelled') THEN
        UPDATE rooms SET is_available = 1
        WHERE room_id = NEW.room_id;
    END IF;

    -- Lock room when checked-in
    IF NEW.reservation_status = 'Checked-In'
       AND OLD.reservation_status != 'Checked-In' THEN
        UPDATE rooms SET is_available = 0
        WHERE room_id = NEW.room_id;
    END IF;

    -- Log status changes to audit trail
    IF NEW.reservation_status != OLD.reservation_status THEN
        INSERT INTO audit_log (
            user_id, username, action_type,
            action_description, target_table, target_record_id
        )
        VALUES (
            NEW.created_by,
            (SELECT username FROM users WHERE user_id = NEW.created_by),
            'UPDATE_STATUS',
            CONCAT('Reservation ', NEW.reservation_number,
                   ' changed from ', OLD.reservation_status,
                   ' to ', NEW.reservation_status),
            'reservations',
            NEW.reservation_id
        );
    END IF;
END //
DELIMITER ;


-- ============================================================
-- SAMPLE DATA: System Users
-- Passwords are SHA-256 hashed for security
-- admin / admin123  |  reception1 / recep123
-- ============================================================
INSERT INTO users (username, password_hash, full_name, user_role, email_address)
VALUES
    ('admin',      SHA2('admin123', 256), 'Resort Manager',  'ADMIN',        'admin@oceanviewresort.lk'),
    ('reception1', SHA2('recep123', 256), 'Kasun Fernando',  'RECEPTIONIST', 'kasun@oceanviewresort.lk');


-- ============================================================
-- SAMPLE DATA: Room Inventory (14 rooms, 4 floors)
-- ============================================================

-- Floor 1: Standard Rooms (5 rooms)
INSERT INTO rooms (room_number, room_type, rate_per_night, is_available, floor_number, max_guests, room_description)
VALUES
    ('101', 'Standard',  5500.00,  1, 1, 2, 'Standard room with garden view'),
    ('102', 'Standard',  5500.00,  1, 1, 2, 'Standard room with garden view'),
    ('103', 'Standard',  5500.00,  1, 1, 2, 'Standard room with garden view'),
    ('104', 'Standard',  5500.00,  1, 1, 2, 'Standard room with pool view'),
    ('105', 'Standard',  5500.00,  1, 1, 2, 'Standard room with pool view');

-- Floor 2: Superior Rooms (4 rooms)
INSERT INTO rooms (room_number, room_type, rate_per_night, is_available, floor_number, max_guests, room_description)
VALUES
    ('201', 'Superior',  8500.00,  1, 2, 2, 'Superior room with partial ocean view'),
    ('202', 'Superior',  8500.00,  1, 2, 2, 'Superior room with partial ocean view'),
    ('203', 'Superior',  8500.00,  1, 2, 3, 'Superior room with partial ocean view and extra bed'),
    ('204', 'Superior',  8500.00,  1, 2, 3, 'Superior room with partial ocean view and extra bed');

-- Floor 3: Premium Rooms (3 rooms)
INSERT INTO rooms (room_number, room_type, rate_per_night, is_available, floor_number, max_guests, room_description)
VALUES
    ('301', 'Premium', 13000.00,  1, 3, 3, 'Premium room with full ocean view and balcony'),
    ('302', 'Premium', 13000.00,  1, 3, 3, 'Premium room with full ocean view and balcony'),
    ('303', 'Premium', 13000.00,  1, 3, 3, 'Premium room with full ocean view and private balcony');

-- Floor 4: Executive Suites (2 rooms)
INSERT INTO rooms (room_number, room_type, rate_per_night, is_available, floor_number, max_guests, room_description)
VALUES
    ('401', 'Executive', 22000.00, 1, 4, 4, 'Executive suite with panoramic ocean view and jacuzzi'),
    ('402', 'Executive', 22000.00, 1, 4, 4, 'Executive suite with panoramic ocean view and private terrace');


-- ============================================================
-- VERIFICATION: Confirm setup is successful
-- ============================================================
SELECT '✓ Database setup complete!' AS status;
SELECT COUNT(*) AS total_users FROM users;
SELECT COUNT(*) AS total_rooms FROM rooms;
SELECT GetAvailableRoomCount('ALL') AS all_available;
SELECT GetAvailableRoomCount('Standard') AS standard_available;
SELECT GetAvailableRoomCount('Superior') AS superior_available;
SELECT GetAvailableRoomCount('Premium') AS premium_available;
SELECT GetAvailableRoomCount('Executive') AS executive_available;