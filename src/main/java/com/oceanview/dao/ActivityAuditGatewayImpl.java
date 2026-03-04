package com.oceanview.dao;

import com.oceanview.model.ActivityAuditEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO Implementation for ActivityAuditEntry database operations.
 *
 * <p><b>Design Pattern:</b> DAO Pattern - Encapsulates all
 * database access logic for the audit_log table.</p>
 *
 * <p><b>Assumption:</b> Audit entries are created both by
 * database triggers (after_reservation_insert, after_reservation_update)
 * and by application-level code for actions like login, bill
 * generation, and user management. This dual approach ensures
 * comprehensive activity tracking.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public class ActivityAuditGatewayImpl implements IActivityAuditGateway {

    /** Logger for audit gateway operations */
    private static final Logger GATEWAY_LOGGER =
            Logger.getLogger(ActivityAuditGatewayImpl.class.getName());

    /** Singleton database connection manager instance */
    private final DatabaseConnectionManager dbManager =
            DatabaseConnectionManager.getInstance();

    /**
     * Maps a ResultSet row to an ActivityAuditEntry object.
     *
     * @param resultRow the current ResultSet row
     * @return a populated ActivityAuditEntry object
     * @throws SQLException if a column access error occurs
     */
    private ActivityAuditEntry mapResultSetToAudit(ResultSet resultRow)
            throws SQLException {

        ActivityAuditEntry mappedEntry = new ActivityAuditEntry();
        mappedEntry.setLogId(resultRow.getInt("log_id"));
        mappedEntry.setUserId(resultRow.getInt("user_id"));
        mappedEntry.setUsername(resultRow.getString("username"));
        mappedEntry.setActionType(resultRow.getString("action_type"));
        mappedEntry.setActionDescription(
                resultRow.getString("action_description"));
        mappedEntry.setTargetTable(resultRow.getString("target_table"));
        mappedEntry.setTargetRecordId(resultRow.getInt("target_record_id"));
        mappedEntry.setIpAddress(resultRow.getString("ip_address"));

        if (resultRow.getTimestamp("action_timestamp") != null) {
            mappedEntry.setActionTimestamp(
                    resultRow.getTimestamp("action_timestamp")
                            .toLocalDateTime());
        }

        return mappedEntry;
    }

    /**
     * {@inheritDoc}
     * Inserts a manual audit entry from application-level actions.
     * Database triggers handle reservation-related audit entries
     * automatically.
     */
    @Override
    public boolean insertAuditEntry(ActivityAuditEntry entry) {

        String insertQuery = "INSERT INTO audit_log "
                + "(user_id, username, action_type, action_description, "
                + "target_table, target_record_id, ip_address) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement insertStatement =
                    dbConnection.prepareStatement(insertQuery);

            insertStatement.setInt(1, entry.getUserId());
            insertStatement.setString(2, entry.getUsername());
            insertStatement.setString(3, entry.getActionType());
            insertStatement.setString(4, entry.getActionDescription());
            insertStatement.setString(5, entry.getTargetTable());
            insertStatement.setInt(6, entry.getTargetRecordId());
            insertStatement.setString(7, entry.getIpAddress());

            int rowsInserted = insertStatement.executeUpdate();

            if (rowsInserted > 0) {
                GATEWAY_LOGGER.fine("Audit entry logged: "
                        + entry.getActionType()
                        + " by " + entry.getUsername());
                return true;
            }
            return false;

        } catch (SQLException insertException) {
            GATEWAY_LOGGER.log(Level.WARNING,
                    "Error inserting audit entry", insertException);
            return false;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<ActivityAuditEntry> findAllAuditEntries() {

        String allQuery = "SELECT * FROM audit_log "
                + "ORDER BY action_timestamp DESC";

        List<ActivityAuditEntry> allEntries = new ArrayList<>();
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement allStatement =
                    dbConnection.prepareStatement(allQuery);
            ResultSet allResult = allStatement.executeQuery();

            while (allResult.next()) {
                allEntries.add(mapResultSetToAudit(allResult));
            }

            GATEWAY_LOGGER.info("Retrieved "
                    + allEntries.size() + " audit entries");

        } catch (SQLException allException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error retrieving audit entries", allException);
        } finally {
            dbManager.closeConnection(dbConnection);
        }

        return allEntries;
    }

    /** {@inheritDoc} */
    @Override
    public List<ActivityAuditEntry> findRecentAuditEntries(int limit) {

        String recentQuery = "SELECT * FROM audit_log "
                + "ORDER BY action_timestamp DESC LIMIT ?";

        List<ActivityAuditEntry> recentEntries = new ArrayList<>();
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement recentStatement =
                    dbConnection.prepareStatement(recentQuery);
            recentStatement.setInt(1, limit);

            ResultSet recentResult = recentStatement.executeQuery();

            while (recentResult.next()) {
                recentEntries.add(mapResultSetToAudit(recentResult));
            }

        } catch (SQLException recentException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error retrieving recent audit entries", recentException);
        } finally {
            dbManager.closeConnection(dbConnection);
        }

        return recentEntries;
    }
}