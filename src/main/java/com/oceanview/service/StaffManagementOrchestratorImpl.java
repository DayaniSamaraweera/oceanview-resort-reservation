package com.oceanview.service;

import com.oceanview.dao.ISystemUserGateway;
import com.oceanview.dao.SystemUserGatewayImpl;
import com.oceanview.model.SystemUser;
import com.oceanview.util.PasswordHashGenerator;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service Implementation for staff user management.
 *
 * <p><b>RBAC:</b> This service is accessible only by ADMIN users.
 * The AuthenticationFilter and servlet-level checks enforce
 * this restriction before calling any method here.</p>
 *
 * <p><b>Architecture:</b> Business Logic Layer - validates staff
 * data, hashes passwords, and delegates database operations
 * to the DAO layer. Uses Builder pattern for user construction.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public class StaffManagementOrchestratorImpl
        implements IStaffManagementOrchestrator {

    /** Logger for staff management events */
    private static final Logger STAFF_LOGGER =
            Logger.getLogger(StaffManagementOrchestratorImpl.class.getName());

    /** Minimum acceptable username length for new staff */
    private static final int MIN_USERNAME_LENGTH = 3;

    /** Minimum acceptable password length for new staff */
    private static final int MIN_PASSWORD_LENGTH = 5;

    /** DAO dependency for user database operations */
    private final ISystemUserGateway userGateway;

    /**
     * Default constructor using concrete DAO implementation.
     */
    public StaffManagementOrchestratorImpl() {
        this.userGateway = new SystemUserGatewayImpl();
    }

    /**
     * Constructor with injected DAO for Mockito testing.
     *
     * @param userGateway the DAO implementation (or mock)
     */
    public StaffManagementOrchestratorImpl(ISystemUserGateway userGateway) {
        this.userGateway = userGateway;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Workflow:
     * 1. Validate all input fields
     * 2. Hash the password using SHA-256
     * 3. Build SystemUser using Builder pattern
     * 4. Delegate to DAO for database insertion</p>
     */
    @Override
    public boolean createStaffAccount(String username, String plainPassword,
                                      String fullName, String userRole,
                                      String emailAddress)
            throws IllegalArgumentException {

        // Validate username
        if (username == null || username.trim().length() < MIN_USERNAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Username must be at least " + MIN_USERNAME_LENGTH
                            + " characters long.");
        }

        // Validate password
        if (plainPassword == null
                || plainPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must be at least " + MIN_PASSWORD_LENGTH
                            + " characters long.");
        }

        // Validate full name
        if (fullName == null || fullName.trim().length() < 2) {
            throw new IllegalArgumentException(
                    "Full name must be at least 2 characters.");
        }

        // Validate role
        if (userRole == null
                || (!"ADMIN".equals(userRole)
                && !"RECEPTIONIST".equals(userRole))) {
            throw new IllegalArgumentException(
                    "User role must be ADMIN or RECEPTIONIST.");
        }

        try {
            // Hash the password for secure storage
            String hashedPassword =
                    PasswordHashGenerator.generateHash(plainPassword);

            if (hashedPassword == null) {
                STAFF_LOGGER.severe("Password hashing failed for new user");
                return false;
            }

            // Build user using Builder pattern
            SystemUser newStaffUser = new SystemUser.Builder()
                    .username(username.trim())
                    .passwordHash(hashedPassword)
                    .fullName(fullName.trim())
                    .userRole(userRole)
                    .emailAddress(
                            emailAddress != null ? emailAddress.trim() : "")
                    .isActive(true)
                    .build();

            boolean insertSuccess = userGateway.insertUser(newStaffUser);

            if (insertSuccess) {
                STAFF_LOGGER.info("New staff account created: "
                        + username + " (Role: " + userRole + ")");
            } else {
                STAFF_LOGGER.warning(
                        "Failed to create staff account: " + username);
            }

            return insertSuccess;

        } catch (Exception createException) {
            STAFF_LOGGER.log(Level.SEVERE,
                    "Error creating staff account: " + username,
                    createException);
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<SystemUser> getAllActiveStaff() {
        return userGateway.findAllActiveUsers();
    }

    /** {@inheritDoc} */
    @Override
    public SystemUser getStaffById(int userId) {
        if (userId <= 0) {
            return null;
        }
        return userGateway.findUserById(userId);
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateStaffDetails(SystemUser user) {
        if (user == null || user.getUserId() <= 0) {
            STAFF_LOGGER.warning("Invalid user for update");
            return false;
        }
        return userGateway.updateUser(user);
    }

    /** {@inheritDoc} */
    @Override
    public boolean deactivateStaffAccount(int userId) {
        if (userId <= 0) {
            STAFF_LOGGER.warning(
                    "Invalid user ID for deactivation: " + userId);
            return false;
        }
        return userGateway.deactivateUser(userId);
    }
}