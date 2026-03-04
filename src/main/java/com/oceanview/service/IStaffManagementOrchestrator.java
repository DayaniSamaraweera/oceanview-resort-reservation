package com.oceanview.service;

import com.oceanview.model.SystemUser;
import java.util.List;

/**
 * Service Interface for staff user management (Admin only).
 *
 * <p><b>RBAC:</b> Only users with ADMIN role can access
 * staff management functionality. The AuthenticationFilter
 * enforces this restriction at the servlet level.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public interface IStaffManagementOrchestrator {

    /**
     * Creates a new staff user account.
     *
     * @param username the login username
     * @param plainPassword the plain text password
     * @param fullName the display name
     * @param userRole the role (ADMIN or RECEPTIONIST)
     * @param emailAddress the email address
     * @return true if creation was successful
     * @throws IllegalArgumentException if validation fails
     */
    boolean createStaffAccount(String username, String plainPassword,
                               String fullName, String userRole,
                               String emailAddress)
            throws IllegalArgumentException;

    /**
     * Retrieves all active staff members.
     *
     * @return list of active SystemUser objects
     */
    List<SystemUser> getAllActiveStaff();

    /**
     * Gets a staff member by their user ID.
     *
     * @param userId the user ID
     * @return the SystemUser, or null if not found
     */
    SystemUser getStaffById(int userId);

    /**
     * Updates a staff member's details.
     *
     * @param user the SystemUser with updated fields
     * @return true if update was successful
     */
    boolean updateStaffDetails(SystemUser user);

    /**
     * Deactivates a staff account (soft delete).
     *
     * @param userId the user ID to deactivate
     * @return true if deactivation was successful
     */
    boolean deactivateStaffAccount(int userId);
}