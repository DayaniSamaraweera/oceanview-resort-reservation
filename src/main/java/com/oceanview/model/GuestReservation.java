package com.oceanview.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing a guest room reservation at Ocean View Resort.
 * 
 * <p>Maps to the 'reservations' table in oceanview_resort_db.</p>
 * 
 * <p><b>Requirement Traceability:</b> This class maps to the core feature
 * "Add New Reservation" which collects reservation number, guest name,
 * address, contact number, room type, check-in date, and check-out date.</p>
 * 
 * <p><b>Status Flow:</b>
 * Confirmed → Checked-In → Checked-Out (normal flow)
 * Confirmed → Cancelled (cancellation with reason)</p>
 * 
 * <p><b>Design Pattern:</b> Builder Pattern for flexible object creation.</p>
 * 
 * @author Dayani Samaraweera
 * @version 1.0
 */
public class GuestReservation {

    /** Unique database identifier (auto-generated) */
    private int reservationId;

    /** System-generated reservation number (format: RES-YYYY-NNNNN) */
    private String reservationNumber;

    /** Full name of the guest making the reservation */
    private String guestName;

    /** Residential address of the guest */
    private String address;

    /** Contact phone number of the guest */
    private String contactNumber;

    /** Email address of the guest for notifications */
    private String guestEmail;

    /** Foreign key reference to the assigned room */
    private int roomId;

    /** Room type: Standard, Superior, Premium, or Executive */
    private String roomType;

    /** Date when the guest checks into the resort */
    private LocalDate checkInDate;

    /** Date when the guest checks out of the resort */
    private LocalDate checkOutDate;

    /** Calculated number of nights (check-out minus check-in) */
    private int numberOfNights;

    /** Current status: Confirmed, Checked-In, Checked-Out, or Cancelled */
    private String reservationStatus;

    /** Reason for cancellation (populated only when status is Cancelled) */
    private String cancelReason;

    /** Foreign key reference to the user who created this reservation */
    private int createdBy;

    /** Timestamp when the reservation was created */
    private LocalDateTime createdAt;

    /** Timestamp of the last reservation modification */
    private LocalDateTime updatedAt;

    // ========== Display fields populated from JOIN queries ==========

    /** Room number from rooms table (populated via GetReservationDetails SP) */
    private String roomNumber;

    /** Nightly rate from rooms table (populated via JOIN) */
    private double ratePerNight;

    /** Creator's full name from users table (populated via JOIN) */
    private String createdByName;

    /**
     * Default no-argument constructor.
     */
    public GuestReservation() {
    }

    /**
     * Private constructor used exclusively by the Builder.
     *
     * @param builder the Builder instance containing field values
     */
    private GuestReservation(Builder builder) {
        this.reservationId = builder.reservationId;
        this.reservationNumber = builder.reservationNumber;
        this.guestName = builder.guestName;
        this.address = builder.address;
        this.contactNumber = builder.contactNumber;
        this.guestEmail = builder.guestEmail;
        this.roomId = builder.roomId;
        this.roomType = builder.roomType;
        this.checkInDate = builder.checkInDate;
        this.checkOutDate = builder.checkOutDate;
        this.numberOfNights = builder.numberOfNights;
        this.reservationStatus = builder.reservationStatus;
        this.cancelReason = builder.cancelReason;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.roomNumber = builder.roomNumber;
        this.ratePerNight = builder.ratePerNight;
        this.createdByName = builder.createdByName;
    }

    // ======================== GETTERS ========================

    /** @return the unique reservation identifier */
    public int getReservationId() { return reservationId; }

    /** @return the system-generated reservation number */
    public String getReservationNumber() { return reservationNumber; }

    /** @return the guest's full name */
    public String getGuestName() { return guestName; }

    /** @return the guest's address */
    public String getAddress() { return address; }

    /** @return the guest's contact number */
    public String getContactNumber() { return contactNumber; }

    /** @return the guest's email address */
    public String getGuestEmail() { return guestEmail; }

    /** @return the assigned room's database ID */
    public int getRoomId() { return roomId; }

    /** @return the room type */
    public String getRoomType() { return roomType; }

    /** @return the check-in date */
    public LocalDate getCheckInDate() { return checkInDate; }

    /** @return the check-out date */
    public LocalDate getCheckOutDate() { return checkOutDate; }

    /** @return the number of nights */
    public int getNumberOfNights() { return numberOfNights; }

    /** @return the reservation status */
    public String getReservationStatus() { return reservationStatus; }

    /** @return the cancellation reason */
    public String getCancelReason() { return cancelReason; }

    /** @return the creator's user ID */
    public int getCreatedBy() { return createdBy; }

    /** @return the creation timestamp */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /** @return the last modification timestamp */
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    /** @return the room number (from JOIN) */
    public String getRoomNumber() { return roomNumber; }

    /** @return the nightly rate (from JOIN) */
    public double getRatePerNight() { return ratePerNight; }

    /** @return the creator's full name (from JOIN) */
    public String getCreatedByName() { return createdByName; }

    // ======================== SETTERS ========================

    /** @param reservationId the reservation identifier to set */
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    /** @param reservationNumber the reservation number to set */
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }

    /** @param guestName the guest name to set */
    public void setGuestName(String guestName) { this.guestName = guestName; }

    /** @param address the address to set */
    public void setAddress(String address) { this.address = address; }

    /** @param contactNumber the contact number to set */
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    /** @param guestEmail the email to set */
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    /** @param roomId the room ID to set */
    public void setRoomId(int roomId) { this.roomId = roomId; }

    /** @param roomType the room type to set */
    public void setRoomType(String roomType) { this.roomType = roomType; }

    /** @param checkInDate the check-in date to set */
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    /** @param checkOutDate the check-out date to set */
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    /** @param numberOfNights the number of nights to set */
    public void setNumberOfNights(int numberOfNights) { this.numberOfNights = numberOfNights; }

    /** @param reservationStatus the status to set */
    public void setReservationStatus(String reservationStatus) { this.reservationStatus = reservationStatus; }

    /** @param cancelReason the cancellation reason to set */
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    /** @param createdBy the creator's user ID to set */
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    /** @param createdAt the creation timestamp to set */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /** @param updatedAt the modification timestamp to set */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /** @param roomNumber the room number to set (display field) */
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    /** @param ratePerNight the nightly rate to set (display field) */
    public void setRatePerNight(double ratePerNight) { this.ratePerNight = ratePerNight; }

    /** @param createdByName the creator name to set (display field) */
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    @Override
    public String toString() {
        return "GuestReservation{reservationId=" + reservationId
                + ", reservationNumber='" + reservationNumber + "'"
                + ", guestName='" + guestName + "'"
                + ", roomType='" + roomType + "'"
                + ", checkIn=" + checkInDate
                + ", checkOut=" + checkOutDate
                + ", status='" + reservationStatus + "'}";
    }

    // ======================== BUILDER ========================

    /**
     * Builder class for constructing GuestReservation instances.
     *
     * <p>Usage example:</p>
     * <pre>
     * GuestReservation booking = new GuestReservation.Builder()
     *     .guestName("John Silva")
     *     .address("45 Galle Road, Colombo")
     *     .contactNumber("0771234567")
     *     .roomId(1)
     *     .roomType("Standard")
     *     .checkInDate(LocalDate.of(2026, 3, 15))
     *     .checkOutDate(LocalDate.of(2026, 3, 18))
     *     .createdBy(1)
     *     .build();
     * </pre>
     */
    public static class Builder {

        private int reservationId;
        private String reservationNumber;
        private String guestName;
        private String address;
        private String contactNumber;
        private String guestEmail;
        private int roomId;
        private String roomType;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private int numberOfNights;
        private String reservationStatus = "Confirmed";
        private String cancelReason;
        private int createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String roomNumber;
        private double ratePerNight;
        private String createdByName;

        public Builder reservationId(int reservationId) {
            this.reservationId = reservationId;
            return this;
        }

        public Builder reservationNumber(String reservationNumber) {
            this.reservationNumber = reservationNumber;
            return this;
        }

        public Builder guestName(String guestName) {
            this.guestName = guestName;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder contactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
            return this;
        }

        public Builder guestEmail(String guestEmail) {
            this.guestEmail = guestEmail;
            return this;
        }

        public Builder roomId(int roomId) {
            this.roomId = roomId;
            return this;
        }

        public Builder roomType(String roomType) {
            this.roomType = roomType;
            return this;
        }

        public Builder checkInDate(LocalDate checkInDate) {
            this.checkInDate = checkInDate;
            return this;
        }

        public Builder checkOutDate(LocalDate checkOutDate) {
            this.checkOutDate = checkOutDate;
            return this;
        }

        public Builder numberOfNights(int numberOfNights) {
            this.numberOfNights = numberOfNights;
            return this;
        }

        public Builder reservationStatus(String reservationStatus) {
            this.reservationStatus = reservationStatus;
            return this;
        }

        public Builder cancelReason(String cancelReason) {
            this.cancelReason = cancelReason;
            return this;
        }

        public Builder createdBy(int createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder roomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
            return this;
        }

        public Builder ratePerNight(double ratePerNight) {
            this.ratePerNight = ratePerNight;
            return this;
        }

        public Builder createdByName(String createdByName) {
            this.createdByName = createdByName;
            return this;
        }

        /**
         * Constructs the GuestReservation instance.
         *
         * @return a new GuestReservation object
         */
        public GuestReservation build() {
            return new GuestReservation(this);
        }
    }
}