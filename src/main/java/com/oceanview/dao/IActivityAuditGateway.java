package com.oceanview.dao;

import com.oceanview.model.ActivityAuditEntry;
import java.util.List;

/**
 * DAO Interface for ActivityAuditEntry operations.
 *
 * <p><b>Assumption:</b> All significant system actions are logged
 * for security auditing and management accountability.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public interface IActivityAuditGateway {

    /**
     * Inserts a new audit trail entry.
     *
     * @param entry the ActivityAuditEntry to insert
     * @return true if insertion was successful
     */
    boolean insertAuditEntry(ActivityAuditEntry entry);

    /**
     * Retrieves all audit trail entries ordered by most recent.
     *
     * @return list of all ActivityAuditEntry objects
     */
    List<ActivityAuditEntry> findAllAuditEntries();

    /**
     * Retrieves the most recent audit entries.
     *
     * @param limit maximum number of records to return
     * @return list of recent ActivityAuditEntry objects
     */
    List<ActivityAuditEntry> findRecentAuditEntries(int limit);
}