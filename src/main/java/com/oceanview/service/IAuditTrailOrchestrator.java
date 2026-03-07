package com.oceanview.service;

import com.oceanview.model.ActivityAuditEntry;
import java.util.List;

//Service Interface for audit trail management.

public interface IAuditTrailOrchestrator {

    //Logs a system action to the audit trail.
    
    void logActivity(int userId, String username, String actionType,
                     String description, String targetTable,
                     int targetRecordId, String ipAddress);

    
     
    List<ActivityAuditEntry> getAllAuditEntries();

    
    List<ActivityAuditEntry> getRecentAuditEntries(int limit);
}