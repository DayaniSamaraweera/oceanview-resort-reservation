package com.oceanview.listener;

import com.oceanview.dao.DatabaseConnectionManager;
import com.oceanview.service.EmailNotificationObserver;
import com.oceanview.service.ReservationEventNotifier;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

/**
 * Application startup and shutdown listener.
 *
 * <p><b>Purpose:</b> Initializes critical system components when
 * the Ocean View Resort application starts on Tomcat:
 * 1. Tests database connectivity
 * 2. Registers Observer pattern observers
 * 3. Logs system startup status</p>
 *
 * <p><b>Design Pattern:</b> Observer Pattern initialization -
 * The EmailNotificationObserver is registered with the
 * ReservationEventNotifier on application startup.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
@WebListener
public class ApplicationStartupListener
        implements ServletContextListener {

    /** Logger for startup events */
    private static final Logger STARTUP_LOGGER =
            Logger.getLogger(ApplicationStartupListener.class.getName());

    /**
     * Called when the application starts.
     * Initializes database connection and Observer pattern.
     */
    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {

        STARTUP_LOGGER.info("========================================");
        STARTUP_LOGGER.info(" Ocean View Resort - System Starting");
        STARTUP_LOGGER.info(" Location: Galle, Sri Lanka");
        STARTUP_LOGGER.info("========================================");

        // ---- Step 1: Test database connection ----
        try {
            DatabaseConnectionManager dbManager =
                    DatabaseConnectionManager.getInstance();
            boolean dbConnected = dbManager.testConnection();

            if (dbConnected) {
                STARTUP_LOGGER.info(
                        "✓ Database connection: SUCCESS");
            } else {
                STARTUP_LOGGER.severe(
                        "✗ Database connection: FAILED");
            }
        } catch (Exception dbException) {
            STARTUP_LOGGER.severe(
                    "✗ Database initialization error: "
                            + dbException.getMessage());
        }

        // ---- Step 2: Register Observer pattern observers ----
        try {
            ReservationEventNotifier notifier =
                    ReservationEventNotifier.getInstance();

            // Register email notification observer
            EmailNotificationObserver emailObserver =
                    new EmailNotificationObserver();
            notifier.registerObserver(emailObserver);

            STARTUP_LOGGER.info("✓ Observer pattern initialized: "
                    + notifier.getObserverCount() + " observer(s) registered");

        } catch (Exception observerException) {
            STARTUP_LOGGER.severe(
                    "✗ Observer initialization error: "
                            + observerException.getMessage());
        }

        STARTUP_LOGGER.info("========================================");
        STARTUP_LOGGER.info(" Ocean View Resort - System Ready");
        STARTUP_LOGGER.info("========================================");
    }

    /**
     * Called when the application shuts down.
     * Performs cleanup operations.
     */
    @Override
    public void contextDestroyed(ServletContextEvent contextEvent) {
        STARTUP_LOGGER.info("Ocean View Resort - System Shutting Down");
    }
}