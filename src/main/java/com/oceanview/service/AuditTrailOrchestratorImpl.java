package com.oceanview.service;

import com.oceanview.dao.IActivityAuditGateway;
import com.oceanview.dao.ActivityAuditGatewayImpl;
import com.oceanview.model.ActivityAuditEntry;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//Service Implementation for audit trail management.//

public class AuditTrailOrchestratorImpl implements IAuditTrailOrchestrator {

    
    private static final Logger AUDIT_LOGGER =
            Logger.getLogger(AuditTrailOrchestratorImpl.class.getName());

    
    private final IActivityAuditGateway auditGateway;

   
    public AuditTrailOrchestratorImpl() {
        this.auditGateway = new ActivityAuditGatewayImpl();
    }


    public AuditTrailOrchestratorImpl(IActivityAuditGateway auditGateway) {
        this.auditGateway = auditGateway;
    }

    @Override
    public void logActivity(int userId, String username,
                            String actionType, String description,
                            String targetTable, int targetRecordId,
                            String ipAddress) {

        try {
            // Build the audit entry using Builder pattern
            ActivityAuditEntry auditEntry = new ActivityAuditEntry.Builder()
                    .userId(userId)
                    .username(username != null ? username : "SYSTEM")
                    .actionType(actionType != null ? actionType : "UNKNOWN")
                    .actionDescription(
                            description != null ? description : "No description")
                    .targetTable(targetTable)
                    .targetRecordId(targetRecordId)
                    .ipAddress(ipAddress)
                    .build();

            boolean insertSuccess = auditGateway.insertAuditEntry(auditEntry);

            if (insertSuccess) {
                AUDIT_LOGGER.fine("Audit entry logged: "
                        + actionType + " by " + username);
            } else {
                AUDIT_LOGGER.warning("Failed to log audit entry: "
                        + actionType + " by " + username);
            }

        } catch (Exception auditException) {
            // Audit logging should never crash the main operation
            AUDIT_LOGGER.log(Level.WARNING,
                    "Error logging audit entry - non-critical",
                    auditException);
        }
    }

 
    @Override
    public List<ActivityAuditEntry> getAllAuditEntries() {
        return auditGateway.findAllAuditEntries();
    }


    @Override
    public List<ActivityAuditEntry> getRecentAuditEntries(int limit) {
        if (limit <= 0) {
            limit = 20;
        }
        return auditGateway.findRecentAuditEntries(limit);
    }
}