package com.oceanview.service;

import com.oceanview.dao.ISystemUserGateway;
import com.oceanview.dao.SystemUserGatewayImpl;
import com.oceanview.model.SystemUser;
import com.oceanview.util.PasswordHashGenerator;

import java.util.logging.Logger;

/**
 * Service Implementation for user authentication.
 *
 * <p><b>Architecture:</b> Business Logic Layer - validates user
 * input, hashes passwords using SHA-256, and delegates database
 * operations to the DAO layer.</p>
 *
 * <p><b>Security:</b>
 * - Plain text passwords are hashed before any database query
 * - Input validation prevents empty/null credentials
 * - Minimum length requirements enforce password strength</p>
 *
 * <p><b>Requirement Traceability:</b> Implements "User Authentication
 * (Login)" - requires username and password for secure system access.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public class UserAuthenticationOrchestratorImpl
        implements IUserAuthenticationOrchestrator {

    /** Logger for authentication events */
    private static final Logger AUTH_LOGGER =
            Logger.getLogger(UserAuthenticationOrchestratorImpl.class.getName());

    /** Minimum acceptable username length */
    private static final int MIN_USERNAME_LENGTH = 3;

    /** Minimum acceptable password length */
    private static final int MIN_PASSWORD_LENGTH = 5;

    /** DAO dependency for user database operations */
    private final ISystemUserGateway userGateway;

    /**
     * Default constructor using concrete DAO implementation.
     * Used in production environment.
     */
    public UserAuthenticationOrchestratorImpl() {
        this.userGateway = new SystemUserGatewayImpl();
    }

    /**
     * Constructor with injected DAO dependency.
     * Used for unit testing with Mockito mock objects.
     *
     * @param userGateway the DAO implementation (or mock)
     */
    public UserAuthenticationOrchestratorImpl(ISystemUserGateway userGateway) {
        this.userGateway = userGateway;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Workflow:
     * 1. Validate username and password are not empty
     * 2. Hash the plain text password using SHA-256
     * 3. Delegate to DAO for database authentication
     * 4. Return authenticated user or null</p>
     */
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

    /** {@inheritDoc} */
    @Override
    public boolean isValidUsername(String username) {
        return username != null
                && !username.trim().isEmpty()
                && username.trim().length() >= MIN_USERNAME_LENGTH;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValidPassword(String password) {
        return password != null
                && !password.trim().isEmpty()
                && password.length() >= MIN_PASSWORD_LENGTH;
    }
}