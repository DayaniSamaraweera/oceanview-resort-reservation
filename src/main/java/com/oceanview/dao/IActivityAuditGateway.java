package com.oceanview.dao;

import com.oceanview.model.ActivityAuditEntry;
import java.util.List;

//DAO interface for audit log operations.//

public interface IActivityAuditGateway {

	// inserts a new audit entry into the DB
    boolean insertAuditEntry(ActivityAuditEntry entry);

 // returns all audit entries, newest first
    List<ActivityAuditEntry> findAllAuditEntries();

 // returns most recent audit entries up to the given limit

    List<ActivityAuditEntry> findRecentAuditEntries(int limit);
}