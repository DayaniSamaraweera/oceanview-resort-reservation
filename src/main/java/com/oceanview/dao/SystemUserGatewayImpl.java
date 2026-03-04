package com.oceanview.dao;

import com.oceanview.model.SystemUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO Implementation for SystemUser database operations.
 *
 * <p><b>Design Pattern:</b> DAO Pattern - Encapsulates all
 * database access logic for the users table. Uses PreparedStatement
 * exclusively to prevent SQL injection attacks.</p>
 *
 * <p><b>Security:</b> Password comparison uses SHA-256 hashed
 * values. Plain text passwords never reach this layer.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public class SystemUserGatewayImpl implements ISystemUserGateway {

    /** Logger for user gateway operations */
    private static final Logger GATEWAY_LOGGER =
            Logger.getLogger(SystemUserGatewayImpl.class.getName());

    /** Singleton database connection manager instance */
    private final DatabaseConnectionManager dbManager =
            DatabaseConnectionManager.getInstance();

    /**
     * Maps a ResultSet row to a SystemUser object.
     * Reusable helper method to avoid code duplication.
     *
     * @param resultRow the current ResultSet row
     * @return a populated SystemUser object
     * @throws SQLException if a column access error occurs
     */
    private SystemUser mapResultSetToUser(ResultSet resultRow) throws SQLException {
        SystemUser mappedUser = new SystemUser();
        mappedUser.setUserId(resultRow.getInt("user_id"));
        mappedUser.setUsername(resultRow.getString("username"));
        mappedUser.setPasswordHash(resultRow.getString("password_hash"));
        mappedUser.setFullName(resultRow.getString("full_name"));
        mappedUser.setUserRole(resultRow.getString("user_role"));
        mappedUser.setEmailAddress(resultRow.getString("email_address"));
        mappedUser.setIsActive(resultRow.getBoolean("is_active"));

        if (resultRow.getTimestamp("created_at") != null) {
            mappedUser.setCreatedAt(
                    resultRow.getTimestamp("created_at").toLocalDateTime());
        }
        if (resultRow.getTimestamp("updated_at") != null) {
            mappedUser.setUpdatedAt(
                    resultRow.getTimestamp("updated_at").toLocalDateTime());
        }

        return mappedUser;
    }

    /**
     * {@inheritDoc}
     * Authenticates by matching username and SHA-256 password hash.
     * Only active accounts can authenticate.
     */
    @Override
    public SystemUser authenticateUser(String username, String passwordHash) {

        String authQuery = "SELECT * FROM users "
                + "WHERE username = ? AND password_hash = ? AND is_active = 1";

        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement authStatement = dbConnection.prepareStatement(authQuery);
            authStatement.setString(1, username);
            authStatement.setString(2, passwordHash);

            ResultSet authResult = authStatement.executeQuery();

            if (authResult.next()) {
                GATEWAY_LOGGER.info("User authenticated successfully: " + username);
                return mapResultSetToUser(authResult);
            } else {
                GATEWAY_LOGGER.warning("Authentication failed for user: " + username);
                return null;
            }

        } catch (SQLException authException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Authentication error for user: " + username, authException);
            return null;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public SystemUser findUserById(int userId) {

        String findQuery = "SELECT * FROM users WHERE user_id = ?";
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement findStatement = dbConnection.prepareStatement(findQuery);
            findStatement.setInt(1, userId);

            ResultSet findResult = findStatement.executeQuery();

            if (findResult.next()) {
                return mapResultSetToUser(findResult);
            }
            return null;

        } catch (SQLException findException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error finding user by ID: " + userId, findException);
            return null;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<SystemUser> findAllActiveUsers() {

        String listQuery = "SELECT * FROM users WHERE is_active = 1 ORDER BY full_name";
        List<SystemUser> activeUsers = new ArrayList<>();
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement listStatement = dbConnection.prepareStatement(listQuery);
            ResultSet listResult = listStatement.executeQuery();

            while (listResult.next()) {
                activeUsers.add(mapResultSetToUser(listResult));
            }

            GATEWAY_LOGGER.info("Retrieved " + activeUsers.size() + " active users");

        } catch (SQLException listException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error retrieving active users", listException);
        } finally {
            dbManager.closeConnection(dbConnection);
        }

        return activeUsers;
    }

    /** {@inheritDoc} */
    @Override
    public boolean insertUser(SystemUser user) {

        String insertQuery = "INSERT INTO users "
                + "(username, password_hash, full_name, user_role, email_address, is_active) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement insertStatement = dbConnection.prepareStatement(insertQuery);
            insertStatement.setString(1, user.getUsername());
            insertStatement.setString(2, user.getPasswordHash());
            insertStatement.setString(3, user.getFullName());
            insertStatement.setString(4, user.getUserRole());
            insertStatement.setString(5, user.getEmailAddress());
            insertStatement.setBoolean(6, user.getIsActive());

            int rowsInserted = insertStatement.executeUpdate();

            if (rowsInserted > 0) {
                GATEWAY_LOGGER.info("New user created: " + user.getUsername());
                return true;
            }
            return false;

        } catch (SQLException insertException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error inserting user: " + user.getUsername(), insertException);
            return false;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateUser(SystemUser user) {

        String updateQuery = "UPDATE users SET "
                + "full_name = ?, user_role = ?, email_address = ? "
                + "WHERE user_id = ?";

        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement updateStatement = dbConnection.prepareStatement(updateQuery);
            updateStatement.setString(1, user.getFullName());
            updateStatement.setString(2, user.getUserRole());
            updateStatement.setString(3, user.getEmailAddress());
            updateStatement.setInt(4, user.getUserId());

            int rowsUpdated = updateStatement.executeUpdate();

            if (rowsUpdated > 0) {
                GATEWAY_LOGGER.info("User updated: " + user.getUsername());
                return true;
            }
            return false;

        } catch (SQLException updateException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error updating user ID: " + user.getUserId(), updateException);
            return false;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean deactivateUser(int userId) {

        String deactivateQuery = "UPDATE users SET is_active = 0 WHERE user_id = ?";
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement deactivateStatement =
                    dbConnection.prepareStatement(deactivateQuery);
            deactivateStatement.setInt(1, userId);

            int rowsUpdated = deactivateStatement.executeUpdate();

            if (rowsUpdated > 0) {
                GATEWAY_LOGGER.info("User deactivated - ID: " + userId);
                return true;
            }
            return false;

        } catch (SQLException deactivateException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error deactivating user ID: " + userId, deactivateException);
            return false;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }
}