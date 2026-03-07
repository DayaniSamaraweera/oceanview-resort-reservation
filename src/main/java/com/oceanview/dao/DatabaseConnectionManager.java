package com.oceanview.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton database connection manager using Bill Pugh pattern
 * for thread-safe lazy initialization. All DAOs get connections through here.
 */
public final class DatabaseConnectionManager {

   
    private static final Logger DB_LOGGER =
            Logger.getLogger(DatabaseConnectionManager.class.getName());

    private final String jdbcDriver;

    private final String connectionUrl;

    private final String dbUsername;

    private final String dbPassword;

 // loads DB config from properties file, private to enforce singleton

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

 // inner class only loads when getInstance() is called - lazy + thread safe

private static class SingletonHolder {
        private static final DatabaseConnectionManager SOLE_INSTANCE =
                new DatabaseConnectionManager();
    }

//returns the single instance, thread safe without synchronization

    public static DatabaseConnectionManager getInstance() {
        return SingletonHolder.SOLE_INSTANCE;
    }

 // returns a new DB connection, always close it after use
    public Connection openConnection() throws SQLException {
        Connection newConnection = DriverManager.getConnection(
                this.connectionUrl, this.dbUsername, this.dbPassword);

        DB_LOGGER.fine("New database connection opened");
        return newConnection;
    }

 // closes the connection safely, handles null without throwing exceptions

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

 // checks if DB is reachable, used at startup

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