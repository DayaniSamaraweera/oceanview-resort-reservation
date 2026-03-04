package com.oceanview.dao;

import com.oceanview.model.SystemUser;
import java.util.List;

/**
 * DAO Interface for SystemUser operations.
 *
 * <p><b>Design Pattern:</b> DAO (Data Access Object) with
 * Interface-driven development. Separates data access logic
 * from business logic, enabling mock testing with Mockito.</p>
 *
 * <p><b>Requirement Traceability:</b> Supports "User Authentication
 * (Login)" feature and staff management functionality.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.1
 */
public interface ISystemUserGateway {

    /**
     * Authenticates a user by username and hashed password.
     *
     * @param username the login username
     * @param passwordHash the SHA-256 hashed password
     * @return the authenticated SystemUser, or null if credentials invalid
     */
    SystemUser authenticateUser(String username, String passwordHash);

    /**
     * Finds a user by their unique database ID.
     *
     * @param userId the user ID to search for
     * @return the SystemUser, or null if not found
     */
    SystemUser findUserById(int userId);

    /**
     * Retrieves all active system users.
     *
     * @return list of active SystemUser objects
     */
    List<SystemUser> findAllActiveUsers();

    /**
     * Inserts a new user into the system.
     *
     * @param user the SystemUser to insert
     * @return true if insertion was successful
     */
    boolean insertUser(SystemUser user);

    /**
     * Updates an existing user's details.
     *
     * @param user the SystemUser with updated fields
     * @return true if update was successful
     */
    boolean updateUser(SystemUser user);

    /**
     * Deactivates a user account (soft delete).
     *
     * @param userId the ID of the user to deactivate
     * @return true if deactivation was successful
     */
    boolean deactivateUser(int userId);

    /**
     * Updates a user's password and optionally their username.
     * Also resets the must_change_password flag to false.
     * Used when staff members change their temporary credentials
     * on first login.
     *
     * @param userId the user ID whose password to update
     * @param newUsername the new username (null to keep existing)
     * @param newPasswordHash the new SHA-256 hashed password
     * @return true if update was successful
     */
    boolean updatePassword(int userId, String newUsername, String newPasswordHash);
}