package com.oceanview.service;

import com.oceanview.model.ActivityAuditEntry;
import java.util.List;

/**
 * Service Interface for audit trail management.
 *
 * <p><b>Assumption:</b> All significant system actions are logged
 * to support security auditing, accountability, and management
 * oversight at Ocean View Resort.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public interface IAuditTrailOrchestrator {

    /**
     * Logs a system action to the audit trail.
     *
     * @param userId the actor's user ID
     * @param username the actor's username
     * @param actionType the type of action performed
     * @param description human-readable description
     * @param targetTable the affected database table
     * @param targetRecordId the affected record ID
     * @param ipAddress the source IP address
     */
    void logActivity(int userId, String username, String actionType,
                     String description, String targetTable,
                     int targetRecordId, String ipAddress);

    /**
     * Retrieves all audit trail entries.
     *
     * @return list of all ActivityAuditEntry objects
     */
    List<ActivityAuditEntry> getAllAuditEntries();

    /**
     * Gets the most recent audit entries.
     *
     * @param limit maximum number of records
     * @return list of recent ActivityAuditEntry objects
     */
    List<ActivityAuditEntry> getRecentAuditEntries(int limit);
}