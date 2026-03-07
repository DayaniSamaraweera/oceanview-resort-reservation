package com.oceanview.dao;

import com.oceanview.model.SystemUser;
import java.util.List;

/**
 * DAO interface for user operations.
 */
public interface ISystemUserGateway {

    // validates username and hashed password, returns null if invalid
    SystemUser authenticateUser(String username, String passwordHash);

    SystemUser findUserById(int userId);

    List<SystemUser> findAllActiveUsers();

    boolean insertUser(SystemUser user);

    boolean updateUser(SystemUser user);

    // soft delete, user record stays in DB but account is disabled
    boolean deactivateUser(int userId);

    // updates password and username, also resets must_change_password flag
    boolean updatePassword(int userId, String newUsername, String newPasswordHash);
}