package com.oceanview.service;

import com.oceanview.dao.ISystemUserGateway;
import com.oceanview.dao.SystemUserGatewayImpl;
import com.oceanview.model.SystemUser;
import com.oceanview.util.PasswordHashGenerator;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//ervice Implementation for staff user management.

public class StaffManagementOrchestratorImpl
        implements IStaffManagementOrchestrator {

    private static final Logger STAFF_LOGGER =
            Logger.getLogger(StaffManagementOrchestratorImpl.class.getName());

    private static final int MIN_USERNAME_LENGTH = 3;

    private static final int MIN_PASSWORD_LENGTH = 5;

    private final ISystemUserGateway userGateway;

   
    public StaffManagementOrchestratorImpl() {
        this.userGateway = new SystemUserGatewayImpl();
    }

 
    public StaffManagementOrchestratorImpl(ISystemUserGateway userGateway) {
        this.userGateway = userGateway;
    }


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
            // Hash the temporary password for secure storage
            String hashedPassword =
                    PasswordHashGenerator.generateHash(plainPassword);

            if (hashedPassword == null) {
                STAFF_LOGGER.severe("Password hashing failed for new user");
                return false;
            }

            // Build user with mustChangePassword = TRUE
            // Staff must change temporary credentials on first login
            SystemUser newStaffUser = new SystemUser.Builder()
                    .username(username.trim())
                    .passwordHash(hashedPassword)
                    .fullName(fullName.trim())
                    .userRole(userRole)
                    .emailAddress(
                            emailAddress != null ? emailAddress.trim() : "")
                    .isActive(true)
                    .mustChangePassword(true)  // FORCE password change on first login
                    .build();

            boolean insertSuccess = userGateway.insertUser(newStaffUser);

            if (insertSuccess) {
                STAFF_LOGGER.info("New staff account created: "
                        + username + " (Role: " + userRole
                        + ") - Must change password on first login");
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