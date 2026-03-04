package com.oceanview.service;

import com.oceanview.model.SystemUser;

/**
 * Service Interface for user authentication and session management.
 *
 * <p><b>Architecture:</b> Business Logic Layer in the 3-Tier Architecture.
 * This interface separates authentication logic from data access (DAO)
 * and presentation (Servlets/JSP) layers.</p>
 *
 * <p><b>Design Pattern:</b> Interface-driven development enables
 * Mockito-based unit testing by allowing mock implementations
 * to be injected during TDD.</p>
 *
 * <p><b>Requirement Traceability:</b> Maps to "User Authentication (Login)"
 * feature - requires username and password for secure system access.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public interface IUserAuthenticationOrchestrator {

    /**
     * Authenticates a user with plain text credentials.
     * Internally hashes the password using SHA-256 before
     * comparing with the stored hash in the database.
     *
     * @param username the login username
     * @param plainPassword the plain text password (will be hashed)
     * @return the authenticated SystemUser, or null if invalid
     */
    SystemUser authenticateCredentials(String username, String plainPassword);

    /**
     * Validates whether the provided username is not empty
     * and meets minimum length requirements.
     *
     * @param username the username to validate
     * @return true if the username is valid
     */
    boolean isValidUsername(String username);

    /**
     * Validates whether the provided password meets
     * minimum security requirements.
     *
     * @param password the password to validate
     * @return true if the password meets requirements
     */
    boolean isValidPassword(String password);
}