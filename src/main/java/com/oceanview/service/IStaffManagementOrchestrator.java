package com.oceanview.service;

import com.oceanview.model.SystemUser;
import java.util.List;

// Service Interface for staff user management (Admin only).

public interface IStaffManagementOrchestrator {

    boolean createStaffAccount(String username, String plainPassword,
                               String fullName, String userRole,
                               String emailAddress)
            throws IllegalArgumentException;

    List<SystemUser> getAllActiveStaff();

    SystemUser getStaffById(int userId);

    boolean updateStaffDetails(SystemUser user);

 
    boolean deactivateStaffAccount(int userId);
}