package com.oceanview.model;

import java.time.LocalDateTime;

/**
 * Entity for rooms table. Has 14 rooms across 4 floors with different rates.
 */
public class ResortRoom {

    private int roomId;
    private String roomNumber;  // physical number like 101, 202
    private String roomType;  // Standard, Superior, Premium, Executive
    private double ratePerNight;
    private boolean isAvailable;
    private int floorNumber;  // 1-4
    private int maxGuests;
    private String roomDescription;
    private LocalDateTime createdAt;

    public ResortRoom() {
    }

    // private constructor for builder pattern
    private ResortRoom(Builder builder) {
        this.roomId = builder.roomId;
        this.roomNumber = builder.roomNumber;
        this.roomType = builder.roomType;
        this.ratePerNight = builder.ratePerNight;
        this.isAvailable = builder.isAvailable;
        this.floorNumber = builder.floorNumber;
        this.maxGuests = builder.maxGuests;
        this.roomDescription = builder.roomDescription;
        this.createdAt = builder.createdAt;
    }

    // getters
    public int getRoomId() { return roomId; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public double getRatePerNight() { return ratePerNight; }
    public boolean getIsAvailable() { return isAvailable; }
    public int getFloorNumber() { return floorNumber; }
    public int getMaxGuests() { return maxGuests; }
    public String getRoomDescription() { return roomDescription; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // setters
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setRatePerNight(double ratePerNight) { this.ratePerNight = ratePerNight; }
    public void setIsAvailable(boolean isAvailable) { this.isAvailable = isAvailable; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }
    public void setMaxGuests(int maxGuests) { this.maxGuests = maxGuests; }
    public void setRoomDescription(String roomDescription) { this.roomDescription = roomDescription; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "ResortRoom{roomId=" + roomId
                + ", roomNumber='" + roomNumber + "'"
                + ", roomType='" + roomType + "'"
                + ", ratePerNight=" + ratePerNight
                + ", isAvailable=" + isAvailable + "}";
    }

    // builder class for creating room objects without messy constructors
    public static class Builder {

        private int roomId;
        private String roomNumber;
        private String roomType;
        private double ratePerNight;
        private boolean isAvailable = true;  // rooms are available by default
        private int floorNumber;
        private int maxGuests = 2;  // default 2 guests
        private String roomDescription;
        private LocalDateTime createdAt;

        public Builder roomId(int roomId) {
            this.roomId = roomId;
            return this;
        }

        public Builder roomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
            return this;
        }

        public Builder roomType(String roomType) {
            this.roomType = roomType;
            return this;
        }

        public Builder ratePerNight(double ratePerNight) {
            this.ratePerNight = ratePerNight;
            return this;
        }

        public Builder isAvailable(boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        public Builder floorNumber(int floorNumber) {
            this.floorNumber = floorNumber;
            return this;
        }

        public Builder maxGuests(int maxGuests) {
            this.maxGuests = maxGuests;
            return this;
        }

        public Builder roomDescription(String roomDescription) {
            this.roomDescription = roomDescription;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ResortRoom build() {
            return new ResortRoom(this);
        }
    }
}