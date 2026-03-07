package com.oceanview.service;

import com.oceanview.model.SystemUser;

//Service Interface for user authentication and session management.
 
public interface IUserAuthenticationOrchestrator {


    SystemUser authenticateCredentials(String username, String plainPassword);

    boolean isValidUsername(String username);

    boolean isValidPassword(String password);
}