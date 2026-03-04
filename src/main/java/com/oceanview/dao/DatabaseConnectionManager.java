package com.oceanview.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton Database Connection Manager for Ocean View Resort.
 *
 * <p><b>Design Pattern:</b> Singleton (Bill Pugh Implementation) -
 * Uses a static inner holder class to achieve thread-safe lazy
 * initialization without synchronization overhead. Only one
 * instance of this manager exists throughout the application
 * lifecycle, providing centralized database connection management.</p>
 *
 * <p><b>Architecture:</b> Part of the Data Access Layer in the
 * 3-Tier Architecture. All DAO classes obtain database connections
 * exclusively through this manager.</p>
 *
 * <p><b>Assumption:</b> Database credentials are stored in
 * database.properties file under src/main/resources/. Each
 * DAO method obtains a new connection and closes it after use
 * to prevent resource leaks.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public final class DatabaseConnectionManager {

    /** Logger for database connection events */
    private static final Logger DB_LOGGER =
            Logger.getLogger(DatabaseConnectionManager.class.getName());

    /** JDBC driver class name loaded from properties */
    private final String jdbcDriver;

    /** Database connection URL loaded from properties */
    private final String connectionUrl;

    /** Database username loaded from properties */
    private final String dbUsername;

    /** Database password loaded from properties */
    private final String dbPassword;

    /**
     * Private constructor prevents external instantiation.
     * Loads database configuration from database.properties file.
     * This ensures the Singleton pattern is enforced.
     */
    private DatabaseConnectionManager() {
        Properties dbProperties = new Properties();

        try (InputStream propertyStream = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (propertyStream == null) {
                DB_LOGGER.severe("database.properties file not found in resources");
                throw new RuntimeException(
                        "Database configuration file not found");
            }

            dbProperties.load(propertyStream);

            this.jdbcDriver = dbProperties.getProperty("db.driver");
            this.connectionUrl = dbProperties.getProperty("db.url");
            this.dbUsername = dbProperties.getProperty("db.username");
            this.dbPassword = dbProperties.getProperty("db.password");

            // Load the JDBC driver class into memory
            Class.forName(this.jdbcDriver);

            DB_LOGGER.info("DatabaseConnectionManager initialized successfully");

        } catch (Exception initException) {
            DB_LOGGER.log(Level.SEVERE,
                    "Failed to initialize database configuration", initException);
            throw new RuntimeException(
                    "Database initialization failed", initException);
        }
    }

    /**
     * Static inner holder class for Bill Pugh Singleton implementation.
     * The JVM guarantees that the inner class is not loaded until
     * getInstance() is called, providing lazy initialization.
     * The class loading mechanism ensures thread safety.
     */
    private static class SingletonHolder {
        private static final DatabaseConnectionManager SOLE_INSTANCE =
                new DatabaseConnectionManager();
    }

    /**
     * Returns the single instance of DatabaseConnectionManager.
     * Thread-safe without explicit synchronization due to the
     * Bill Pugh implementation using a static inner holder class.
     *
     * @return the sole DatabaseConnectionManager instance
     */
    public static DatabaseConnectionManager getInstance() {
        return SingletonHolder.SOLE_INSTANCE;
    }

    /**
     * Creates and returns a new database connection.
     * Each DAO method should call this to obtain a connection,
     * use it for the required operation, and close it in a
     * finally block or try-with-resources statement.
     *
     * @return a new Connection to oceanview_resort_db
     * @throws SQLException if a database access error occurs
     */
    public Connection openConnection() throws SQLException {
        Connection newConnection = DriverManager.getConnection(
                this.connectionUrl, this.dbUsername, this.dbPassword);

        DB_LOGGER.fine("New database connection opened");
        return newConnection;
    }

    /**
     * Safely closes a database connection.
     * Handles null checks and logs any closure errors
     * without throwing exceptions to the caller.
     *
     * @param connection the connection to close (may be null)
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    DB_LOGGER.fine("Database connection closed");
                }
            } catch (SQLException closeException) {
                DB_LOGGER.log(Level.WARNING,
                        "Error closing database connection", closeException);
            }
        }
    }

    /**
     * Tests whether the database is reachable.
     * Used during application startup to verify configuration.
     *
     * @return true if a connection can be established
     */
    public boolean testConnection() {
        try (Connection testConn = openConnection()) {
            boolean isValid = testConn != null && !testConn.isClosed();
            DB_LOGGER.info("Database connection test: " +
                    (isValid ? "SUCCESS" : "FAILED"));
            return isValid;
        } catch (SQLException testException) {
            DB_LOGGER.log(Level.SEVERE,
                    "Database connection test FAILED", testException);
            return false;
        }
    }
}