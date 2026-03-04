package com.oceanview.service;

import com.oceanview.model.GuestReservation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Subject class for the Observer Design Pattern.
 *
 * <p><b>Design Pattern:</b> Observer (GoF) - This class acts as the
 * Subject (or Publisher) that maintains a list of observers and
 * notifies them when reservation events occur.</p>
 *
 * <p><b>Design Pattern:</b> Singleton - Only one notifier instance
 * exists to manage all reservation event observers centrally.</p>
 *
 * <p><b>How it works:</b>
 * 1. Observers register themselves via registerObserver()
 * 2. When a reservation event occurs, the corresponding notify
 *    method is called
 * 3. All registered observers receive the notification
 * 4. Each observer handles the event independently</p>
 *
 * <p><b>Extensibility:</b> New observers (e.g., SMS notifications,
 * analytics tracking) can be added by implementing the
 * IReservationObserver interface and registering with this notifier.
 * No changes to existing code are required (Open/Closed Principle).</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public class ReservationEventNotifier {

    /** Logger for notification events */
    private static final Logger NOTIFIER_LOGGER =
            Logger.getLogger(ReservationEventNotifier.class.getName());

    /** Singleton instance */
    private static ReservationEventNotifier soleInstance;

    /** List of registered observers */
    private final List<IReservationObserver> registeredObservers;

    /**
     * Private constructor enforces Singleton pattern.
     */
    private ReservationEventNotifier() {
        this.registeredObservers = new ArrayList<>();
        NOTIFIER_LOGGER.info(
                "ReservationEventNotifier initialized");
    }

    /**
     * Returns the single instance of ReservationEventNotifier.
     * Thread-safe using synchronized keyword.
     *
     * @return the sole ReservationEventNotifier instance
     */
    public static synchronized ReservationEventNotifier getInstance() {
        if (soleInstance == null) {
            soleInstance = new ReservationEventNotifier();
        }
        return soleInstance;
    }

    /**
     * Registers a new observer to receive reservation event
     * notifications.
     *
     * @param observer the observer to register
     */
    public void registerObserver(IReservationObserver observer) {
        if (observer != null && !registeredObservers.contains(observer)) {
            registeredObservers.add(observer);
            NOTIFIER_LOGGER.info("Observer registered: "
                    + observer.getObserverName());
        }
    }

    /**
     * Removes an observer from the notification list.
     *
     * @param observer the observer to remove
     */
    public void removeObserver(IReservationObserver observer) {
        if (observer != null) {
            registeredObservers.remove(observer);
            NOTIFIER_LOGGER.info("Observer removed: "
                    + observer.getObserverName());
        }
    }

    /**
     * Notifies all registered observers that a new reservation
     * has been created.
     *
     * @param reservation the newly created GuestReservation
     */
    public void notifyReservationCreated(GuestReservation reservation) {
        NOTIFIER_LOGGER.info("Notifying " + registeredObservers.size()
                + " observers of new reservation: "
                + reservation.getReservationNumber());

        for (IReservationObserver observer : registeredObservers) {
            try {
                observer.onReservationCreated(reservation);
            } catch (Exception observerException) {
                // One observer failing should not affect others
                NOTIFIER_LOGGER.log(Level.WARNING,
                        "Observer " + observer.getObserverName()
                                + " failed on reservation created",
                        observerException);
            }
        }
    }

    /**
     * Notifies all registered observers that a reservation
     * status has changed.
     *
     * @param reservation the updated GuestReservation
     * @param newStatus the new status value
     */
    public void notifyStatusChanged(GuestReservation reservation,
                                    String newStatus) {
        NOTIFIER_LOGGER.info("Notifying " + registeredObservers.size()
                + " observers of status change: "
                + reservation.getReservationNumber()
                + " → " + newStatus);

        for (IReservationObserver observer : registeredObservers) {
            try {
                observer.onReservationStatusChanged(reservation, newStatus);
            } catch (Exception observerException) {
                NOTIFIER_LOGGER.log(Level.WARNING,
                        "Observer " + observer.getObserverName()
                                + " failed on status change",
                        observerException);
            }
        }
    }

    /**
     * Returns the number of registered observers.
     * Useful for testing and monitoring.
     *
     * @return the count of registered observers
     */
    public int getObserverCount() {
        return registeredObservers.size();
    }
}