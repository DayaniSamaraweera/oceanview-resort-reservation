package com.oceanview.service;

import com.oceanview.dao.ISystemUserGateway;
import com.oceanview.dao.SystemUserGatewayImpl;
import com.oceanview.model.SystemUser;
import com.oceanview.util.PasswordHashGenerator;

import java.util.logging.Logger;

//Service Implementation for user authentication.

public class UserAuthenticationOrchestratorImpl
        implements IUserAuthenticationOrchestrator {

   
    private static final Logger AUTH_LOGGER =
            Logger.getLogger(UserAuthenticationOrchestratorImpl.class.getName());

    private static final int MIN_USERNAME_LENGTH = 3;

    private static final int MIN_PASSWORD_LENGTH = 5;

    private final ISystemUserGateway userGateway;

    public UserAuthenticationOrchestratorImpl() {
        this.userGateway = new SystemUserGatewayImpl();
    }

 
    public UserAuthenticationOrchestratorImpl(ISystemUserGateway userGateway) {
        this.userGateway = userGateway;
    }

  
    @Override
    public SystemUser authenticateCredentials(String username,
                                              String plainPassword) {

        // Validate inputs before processing
        if (!isValidUsername(username)) {
            AUTH_LOGGER.warning("Authentication rejected: invalid username");
            return null;
        }

        if (!isValidPassword(plainPassword)) {
            AUTH_LOGGER.warning("Authentication rejected: invalid password");
            return null;
        }

        // Hash the password using SHA-256 for secure comparison
        String hashedPassword = PasswordHashGenerator.generateHash(plainPassword);

        if (hashedPassword == null) {
            AUTH_LOGGER.severe("Password hashing failed during authentication");
            return null;
        }

        // Delegate to DAO layer for database authentication
        SystemUser authenticatedUser =
                userGateway.authenticateUser(username, hashedPassword);

        if (authenticatedUser != null) {
            AUTH_LOGGER.info("Successful login: " + username
                    + " (Role: " + authenticatedUser.getUserRole() + ")");
        } else {
            AUTH_LOGGER.warning("Failed login attempt for username: " + username);
        }

        return authenticatedUser;
    }

   
    @Override
    public boolean isValidUsername(String username) {
        return username != null
                && !username.trim().isEmpty()
                && username.trim().length() >= MIN_USERNAME_LENGTH;
    }

  
    @Override
    public boolean isValidPassword(String password) {
        return password != null
                && !password.trim().isEmpty()
                && password.length() >= MIN_PASSWORD_LENGTH;
    }
}