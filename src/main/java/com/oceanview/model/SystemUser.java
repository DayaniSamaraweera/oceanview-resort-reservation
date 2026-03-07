package com.oceanview.model;

import java.time.LocalDateTime;

/**
 * Entity for users table. Supports ADMIN and RECEPTIONIST roles.
 * New staff must change password on first login.
 */
public class SystemUser {

    private int userId;
    private String username;  // must be unique
    private String passwordHash;  // SHA-256 hashed, never store plain text
    private String fullName;
    private String userRole;  // ADMIN or RECEPTIONIST
    private String emailAddress;
    private boolean isActive;
    
    // when true, user is forced to change password on next login
    // set to true when admin creates new staff account
    private boolean mustChangePassword;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SystemUser() {
    }

    // private constructor for builder pattern
    private SystemUser(Builder builder) {
        this.userId = builder.userId;
        this.username = builder.username;
        this.passwordHash = builder.passwordHash;
        this.fullName = builder.fullName;
        this.userRole = builder.userRole;
        this.emailAddress = builder.emailAddress;
        this.isActive = builder.isActive;
        this.mustChangePassword = builder.mustChangePassword;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    // getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
    public String getUserRole() { return userRole; }
    public String getEmailAddress() { return emailAddress; }
    public boolean getIsActive() { return isActive; }
    public boolean getMustChangePassword() { return mustChangePassword; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
    public void setMustChangePassword(boolean mustChangePassword) { 
        this.mustChangePassword = mustChangePassword; 
    }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "SystemUser{userId=" + userId
                + ", username='" + username + "'"
                + ", fullName='" + fullName + "'"
                + ", userRole='" + userRole + "'"
                + ", isActive=" + isActive
                + ", mustChangePassword=" + mustChangePassword + "}";
    }

    // builder class to create user objects without messy constructors
    public static class Builder {

        private int userId;
        private String username;
        private String passwordHash;
        private String fullName;
        private String userRole;
        private String emailAddress;
        private boolean isActive = true;  // accounts are active by default
        private boolean mustChangePassword = false;  // default no password change needed
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder userId(int userId) { this.userId = userId; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder userRole(String userRole) { this.userRole = userRole; return this; }
        public Builder emailAddress(String emailAddress) { this.emailAddress = emailAddress; return this; }
        public Builder isActive(boolean isActive) { this.isActive = isActive; return this; }
        public Builder mustChangePassword(boolean mustChangePassword) { 
            this.mustChangePassword = mustChangePassword; return this; 
        }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SystemUser build() { return new SystemUser(this); }
    }
}