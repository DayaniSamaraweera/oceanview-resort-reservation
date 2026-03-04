package com.oceanview.service;

import com.oceanview.dao.IActivityAuditGateway;
import com.oceanview.dao.ActivityAuditGatewayImpl;
import com.oceanview.model.ActivityAuditEntry;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service Implementation for audit trail management.
 *
 * <p><b>Architecture:</b> Business Logic Layer - creates audit
 * entries using the Builder pattern and delegates storage to
 * the DAO layer.</p>
 *
 * <p><b>Assumption:</b> The audit trail captures both:
 * 1. Database trigger-generated entries (reservation create/update)
 * 2. Application-level entries (login, bill generation, etc.)
 * This dual-source approach ensures comprehensive tracking.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public class AuditTrailOrchestratorImpl implements IAuditTrailOrchestrator {

    /** Logger for audit trail events */
    private static final Logger AUDIT_LOGGER =
            Logger.getLogger(AuditTrailOrchestratorImpl.class.getName());

    /** DAO dependency for audit database operations */
    private final IActivityAuditGateway auditGateway;

    /**
     * Default constructor using concrete DAO implementation.
     */
    public AuditTrailOrchestratorImpl() {
        this.auditGateway = new ActivityAuditGatewayImpl();
    }

    /**
     * Constructor with injected DAO for Mockito testing.
     *
     * @param auditGateway the DAO implementation (or mock)
     */
    public AuditTrailOrchestratorImpl(IActivityAuditGateway auditGateway) {
        this.auditGateway = auditGateway;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Uses the Builder pattern to construct the
     * ActivityAuditEntry object before persisting it.</p>
     */
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

    /** {@inheritDoc} */
    @Override
    public List<ActivityAuditEntry> getAllAuditEntries() {
        return auditGateway.findAllAuditEntries();
    }

    /** {@inheritDoc} */
    @Override
    public List<ActivityAuditEntry> getRecentAuditEntries(int limit) {
        if (limit <= 0) {
            limit = 20;
        }
        return auditGateway.findRecentAuditEntries(limit);
    }
}