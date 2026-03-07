package com.oceanview.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity for reservations table. Holds all booking details.
 * Status flow: Confirmed → Checked-In → Checked-Out, or Confirmed → Cancelled.
 */
public class GuestReservation {

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

    private String reservationStatus;

    private String cancelReason;

    private int createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    //  Display fields populated from JOIN queries 

    private String roomNumber;

    private double ratePerNight;

    private String createdByName;

    public GuestReservation() {
    }

 
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

    //GETTERS

    public int getReservationId() { return reservationId; }

    public String getReservationNumber() { return reservationNumber; }

    public String getGuestName() { return guestName; }

    public String getAddress() { return address; }

    public String getContactNumber() { return contactNumber; }

    public String getGuestEmail() { return guestEmail; }

    public int getRoomId() { return roomId; }

    public String getRoomType() { return roomType; }

    public LocalDate getCheckInDate() { return checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }

    public int getNumberOfNights() { return numberOfNights; }

    public String getReservationStatus() { return reservationStatus; }

    public String getCancelReason() { return cancelReason; }

    public int getCreatedBy() { return createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public String getRoomNumber() { return roomNumber; }

    public double getRatePerNight() { return ratePerNight; }

    public String getCreatedByName() { return createdByName; }

    // SETTERS

    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }

    public void setGuestName(String guestName) { this.guestName = guestName; }

    public void setAddress(String address) { this.address = address; }

    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    public void setRoomId(int roomId) { this.roomId = roomId; }

    public void setRoomType(String roomType) { this.roomType = roomType; }

    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public void setNumberOfNights(int numberOfNights) { this.numberOfNights = numberOfNights; }

    public void setReservationStatus(String reservationStatus) { this.reservationStatus = reservationStatus; }

    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public void setRatePerNight(double ratePerNight) { this.ratePerNight = ratePerNight; }

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

    // BUILDER 

    //Builder class for constructing GuestReservation instances.//
    
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