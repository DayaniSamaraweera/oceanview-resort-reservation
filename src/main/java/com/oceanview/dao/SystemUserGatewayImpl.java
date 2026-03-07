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

//DAO Implementation for SystemUser database operations.//

public class SystemUserGatewayImpl implements ISystemUserGateway {

   
    private static final Logger GATEWAY_LOGGER =
            Logger.getLogger(SystemUserGatewayImpl.class.getName());

 
    private final DatabaseConnectionManager dbManager =
            DatabaseConnectionManager.getInstance();

 
    private SystemUser mapResultSetToUser(ResultSet resultRow) throws SQLException {
        SystemUser mappedUser = new SystemUser();
        mappedUser.setUserId(resultRow.getInt("user_id"));
        mappedUser.setUsername(resultRow.getString("username"));
        mappedUser.setPasswordHash(resultRow.getString("password_hash"));
        mappedUser.setFullName(resultRow.getString("full_name"));
        mappedUser.setUserRole(resultRow.getString("user_role"));
        mappedUser.setEmailAddress(resultRow.getString("email_address"));
        mappedUser.setIsActive(resultRow.getBoolean("is_active"));
        mappedUser.setMustChangePassword(
                resultRow.getBoolean("must_change_password"));

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

    /**
     * {@inheritDoc}
     * Inserts with must_change_password flag for first-login flow.
     */
    @Override
    public boolean insertUser(SystemUser user) {

        String insertQuery = "INSERT INTO users "
                + "(username, password_hash, full_name, user_role, "
                + "email_address, is_active, must_change_password) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

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
            insertStatement.setBoolean(7, user.getMustChangePassword());

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

    /**
     * {@inheritDoc}
     *
     * <p>Updates password hash and optionally the username.
     * Resets must_change_password to 0 (false) so the user
     * is not forced to change again on next login.</p>
     */
    @Override
    public boolean updatePassword(int userId, String newUsername,
                                  String newPasswordHash) {

        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();

            String updateQuery;
            PreparedStatement updateStatement;

            if (newUsername != null && !newUsername.trim().isEmpty()) {
                // Update both username and password
                updateQuery = "UPDATE users SET "
                        + "username = ?, password_hash = ?, "
                        + "must_change_password = 0 "
                        + "WHERE user_id = ?";
                updateStatement = dbConnection.prepareStatement(updateQuery);
                updateStatement.setString(1, newUsername.trim());
                updateStatement.setString(2, newPasswordHash);
                updateStatement.setInt(3, userId);
            } else {
                // Update password only
                updateQuery = "UPDATE users SET "
                        + "password_hash = ?, must_change_password = 0 "
                        + "WHERE user_id = ?";
                updateStatement = dbConnection.prepareStatement(updateQuery);
                updateStatement.setString(1, newPasswordHash);
                updateStatement.setInt(2, userId);
            }

            int rowsUpdated = updateStatement.executeUpdate();

            if (rowsUpdated > 0) {
                GATEWAY_LOGGER.info(
                        "Password updated for user ID: " + userId);
                return true;
            }
            return false;

        } catch (SQLException updateException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error updating password for user ID: " + userId,
                    updateException);
            return false;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }
}