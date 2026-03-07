package com.oceanview.model;

import java.time.LocalDateTime;

/**
 * Entity for bills table. Stores a snapshot of billing data so old bills 
 * don't change when reservations are updated later.
 */
public class InvoiceRecord {

    private int billId;
    private String billNumber;  // format: BILL-YYYY-NNNNN
    private int reservationId;
    private String reservationNumber;
    private String guestName;
    private String roomType;
    private double ratePerNight;
    private int numberOfNights;
    private double subtotal;  // rate * nights
    private double taxPercentage;
    private double taxAmount;
    private double totalAmount;  // subtotal + tax
    private int generatedBy;
    private LocalDateTime generatedAt;

    // from JOIN with users table
    private String generatedByName;

    public InvoiceRecord() {
    }

    // private constructor for builder pattern
    private InvoiceRecord(Builder builder) {
        this.billId = builder.billId;
        this.billNumber = builder.billNumber;
        this.reservationId = builder.reservationId;
        this.reservationNumber = builder.reservationNumber;
        this.guestName = builder.guestName;
        this.roomType = builder.roomType;
        this.ratePerNight = builder.ratePerNight;
        this.numberOfNights = builder.numberOfNights;
        this.subtotal = builder.subtotal;
        this.taxPercentage = builder.taxPercentage;
        this.taxAmount = builder.taxAmount;
        this.totalAmount = builder.totalAmount;
        this.generatedBy = builder.generatedBy;
        this.generatedAt = builder.generatedAt;
        this.generatedByName = builder.generatedByName;
    }

    // getters
    public int getBillId() { return billId; }
    public String getBillNumber() { return billNumber; }
    public int getReservationId() { return reservationId; }
    public String getReservationNumber() { return reservationNumber; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public double getRatePerNight() { return ratePerNight; }
    public int getNumberOfNights() { return numberOfNights; }
    public double getSubtotal() { return subtotal; }
    public double getTaxPercentage() { return taxPercentage; }
    public double getTaxAmount() { return taxAmount; }
    public double getTotalAmount() { return totalAmount; }
    public int getGeneratedBy() { return generatedBy; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public String getGeneratedByName() { return generatedByName; }

    // setters
    public void setBillId(int billId) { this.billId = billId; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setRatePerNight(double ratePerNight) { this.ratePerNight = ratePerNight; }
    public void setNumberOfNights(int numberOfNights) { this.numberOfNights = numberOfNights; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public void setTaxPercentage(double taxPercentage) { this.taxPercentage = taxPercentage; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setGeneratedBy(int generatedBy) { this.generatedBy = generatedBy; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public void setGeneratedByName(String generatedByName) { this.generatedByName = generatedByName; }

    @Override
    public String toString() {
        return "InvoiceRecord{billId=" + billId
                + ", billNumber='" + billNumber + "'"
                + ", reservationNumber='" + reservationNumber + "'"
                + ", guestName='" + guestName + "'"
                + ", totalAmount=" + totalAmount + "}";
    }

    
    //Builder class for constructing InvoiceRecord instances.//
    
    public static class Builder {

        private int billId;
        private String billNumber;
        private int reservationId;
        private String reservationNumber;
        private String guestName;
        private String roomType;
        private double ratePerNight;
        private int numberOfNights;
        private double subtotal;
        private double taxPercentage = 0.00;
        private double taxAmount = 0.00;
        private double totalAmount;
        private int generatedBy;
        private LocalDateTime generatedAt;
        private String generatedByName;

        public Builder billId(int billId) { this.billId = billId; return this; }
        public Builder billNumber(String billNumber) { this.billNumber = billNumber; return this; }
        public Builder reservationId(int reservationId) { this.reservationId = reservationId; return this; }
        public Builder reservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; return this; }
        public Builder guestName(String guestName) { this.guestName = guestName; return this; }
        public Builder roomType(String roomType) { this.roomType = roomType; return this; }
        public Builder ratePerNight(double ratePerNight) { this.ratePerNight = ratePerNight; return this; }
        public Builder numberOfNights(int numberOfNights) { this.numberOfNights = numberOfNights; return this; }
        public Builder subtotal(double subtotal) { this.subtotal = subtotal; return this; }
        public Builder taxPercentage(double taxPercentage) { this.taxPercentage = taxPercentage; return this; }
        public Builder taxAmount(double taxAmount) { this.taxAmount = taxAmount; return this; }
        public Builder totalAmount(double totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder generatedBy(int generatedBy) { this.generatedBy = generatedBy; return this; }
        public Builder generatedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; return this; }
        public Builder generatedByName(String generatedByName) { this.generatedByName = generatedByName; return this; }

      
        public InvoiceRecord build() {
            return new InvoiceRecord(this);
        }
    }
}