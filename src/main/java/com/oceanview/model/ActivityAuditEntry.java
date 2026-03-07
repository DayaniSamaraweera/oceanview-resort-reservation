package com.oceanview.model;

import java.time.LocalDateTime;

//Entity for audit_log table. Tracks all important system actions for security and accountability.

public class ActivityAuditEntry {

   
    private int logId;

    private int userId;

    private String username;

    private String actionType;

    private String actionDescription;

    private String targetTable;

    private int targetRecordId;

    private String ipAddress;

    private LocalDateTime actionTimestamp;

  
    public ActivityAuditEntry() {
    }

 
    private ActivityAuditEntry(Builder builder) {
        this.logId = builder.logId;
        this.userId = builder.userId;
        this.username = builder.username;
        this.actionType = builder.actionType;
        this.actionDescription = builder.actionDescription;
        this.targetTable = builder.targetTable;
        this.targetRecordId = builder.targetRecordId;
        this.ipAddress = builder.ipAddress;
        this.actionTimestamp = builder.actionTimestamp;
    }

    // GETTERS

    public int getLogId() { return logId; }

    public int getUserId() { return userId; }

    public String getUsername() { return username; }

    public String getActionType() { return actionType; }

    public String getActionDescription() { return actionDescription; }

    public String getTargetTable() { return targetTable; }

    public int getTargetRecordId() { return targetRecordId; }

    public String getIpAddress() { return ipAddress; }

    public LocalDateTime getActionTimestamp() { return actionTimestamp; }

    // ======================== SETTERS ========================

    public void setLogId(int logId) { this.logId = logId; }

    public void setUserId(int userId) { this.userId = userId; }

    public void setUsername(String username) { this.username = username; }

    public void setActionType(String actionType) { this.actionType = actionType; }

    public void setActionDescription(String actionDescription) { this.actionDescription = actionDescription; }

    public void setTargetTable(String targetTable) { this.targetTable = targetTable; }

    public void setTargetRecordId(int targetRecordId) { this.targetRecordId = targetRecordId; }

    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public void setActionTimestamp(LocalDateTime actionTimestamp) { this.actionTimestamp = actionTimestamp; }

    @Override
    public String toString() {
        return "ActivityAuditEntry{logId=" + logId
                + ", username='" + username + "'"
                + ", actionType='" + actionType + "'"
                + ", description='" + actionDescription + "'"
                + ", timestamp=" + actionTimestamp + "}";
    }

    // BUILDER

 // builder pattern for creating audit entries without messy constructors

    public static class Builder {

        private int logId;
        private int userId;
        private String username;
        private String actionType;
        private String actionDescription;
        private String targetTable;
        private int targetRecordId;
        private String ipAddress;
        private LocalDateTime actionTimestamp;

        public Builder logId(int logId) { this.logId = logId; return this; }
        public Builder userId(int userId) { this.userId = userId; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder actionType(String actionType) { this.actionType = actionType; return this; }
        public Builder actionDescription(String actionDescription) { this.actionDescription = actionDescription; return this; }
        public Builder targetTable(String targetTable) { this.targetTable = targetTable; return this; }
        public Builder targetRecordId(int targetRecordId) { this.targetRecordId = targetRecordId; return this; }
        public Builder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public Builder actionTimestamp(LocalDateTime actionTimestamp) { this.actionTimestamp = actionTimestamp; return this; }


        public ActivityAuditEntry build() {
            return new ActivityAuditEntry(this);
        }
    }
}