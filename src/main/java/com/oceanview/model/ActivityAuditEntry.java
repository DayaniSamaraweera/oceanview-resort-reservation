package com.oceanview.model;

import java.time.LocalDateTime;

/**
 * Entity class representing an audit trail entry at Ocean View Resort.
 * 
 * <p>Maps to the 'audit_log' table in oceanview_resort_db.</p>
 * 
 * <p><b>Assumption:</b> Every significant system action (create, update,
 * delete operations) is logged for security auditing, accountability,
 * and management oversight purposes.</p>
 * 
 * <p><b>Design Pattern:</b> Builder Pattern for flexible object creation.</p>
 * 
 * @author Dayani Samaraweera
 * @version 1.0
 */
public class ActivityAuditEntry {

    /** Unique log entry identifier (auto-generated) */
    private int logId;

    /** ID of the user who performed the action */
    private int userId;

    /** Username of the user who performed the action */
    private String username;

    /** Type of action: CREATE_RESERVATION, UPDATE_STATUS, GENERATE_BILL, etc. */
    private String actionType;

    /** Human-readable description of what was done */
    private String actionDescription;

    /** Database table that was affected */
    private String targetTable;

    /** ID of the specific record that was affected */
    private int targetRecordId;

    /** IP address from which the action was performed */
    private String ipAddress;

    /** Timestamp when the action occurred */
    private LocalDateTime actionTimestamp;

    /**
     * Default no-argument constructor.
     */
    public ActivityAuditEntry() {
    }

    /**
     * Private constructor used exclusively by the Builder.
     *
     * @param builder the Builder instance containing field values
     */
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

    // ======================== GETTERS ========================

    /** @return the unique log entry identifier */
    public int getLogId() { return logId; }

    /** @return the actor's user ID */
    public int getUserId() { return userId; }

    /** @return the actor's username */
    public String getUsername() { return username; }

    /** @return the action type */
    public String getActionType() { return actionType; }

    /** @return the action description */
    public String getActionDescription() { return actionDescription; }

    /** @return the affected table name */
    public String getTargetTable() { return targetTable; }

    /** @return the affected record ID */
    public int getTargetRecordId() { return targetRecordId; }

    /** @return the source IP address */
    public String getIpAddress() { return ipAddress; }

    /** @return the action timestamp */
    public LocalDateTime getActionTimestamp() { return actionTimestamp; }

    // ======================== SETTERS ========================

    /** @param logId the log entry identifier to set */
    public void setLogId(int logId) { this.logId = logId; }

    /** @param userId the actor's user ID to set */
    public void setUserId(int userId) { this.userId = userId; }

    /** @param username the actor's username to set */
    public void setUsername(String username) { this.username = username; }

    /** @param actionType the action type to set */
    public void setActionType(String actionType) { this.actionType = actionType; }

    /** @param actionDescription the description to set */
    public void setActionDescription(String actionDescription) { this.actionDescription = actionDescription; }

    /** @param targetTable the affected table name to set */
    public void setTargetTable(String targetTable) { this.targetTable = targetTable; }

    /** @param targetRecordId the affected record ID to set */
    public void setTargetRecordId(int targetRecordId) { this.targetRecordId = targetRecordId; }

    /** @param ipAddress the source IP address to set */
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    /** @param actionTimestamp the action timestamp to set */
    public void setActionTimestamp(LocalDateTime actionTimestamp) { this.actionTimestamp = actionTimestamp; }

    @Override
    public String toString() {
        return "ActivityAuditEntry{logId=" + logId
                + ", username='" + username + "'"
                + ", actionType='" + actionType + "'"
                + ", description='" + actionDescription + "'"
                + ", timestamp=" + actionTimestamp + "}";
    }

    // ======================== BUILDER ========================

    /**
     * Builder class for constructing ActivityAuditEntry instances.
     *
     * <p>Usage example:</p>
     * <pre>
     * ActivityAuditEntry entry = new ActivityAuditEntry.Builder()
     *     .userId(1)
     *     .username("admin")
     *     .actionType("CREATE_RESERVATION")
     *     .actionDescription("New reservation RES-2026-00001 created")
     *     .targetTable("reservations")
     *     .targetRecordId(1)
     *     .build();
     * </pre>
     */
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

        /**
         * Constructs the ActivityAuditEntry instance.
         *
         * @return a new ActivityAuditEntry object
         */
        public ActivityAuditEntry build() {
            return new ActivityAuditEntry(this);
        }
    }
}