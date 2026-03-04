package com.oceanview.service;

import com.oceanview.model.GuestReservation;

/**
 * Observer Interface for the Observer Design Pattern.
 *
 * <p><b>Design Pattern:</b> Observer (GoF) - Defines a one-to-many
 * dependency between objects so that when one object (the Subject)
 * changes state, all its dependents (Observers) are notified
 * and updated automatically.</p>
 *
 * <p><b>Application:</b> When a new reservation is created at
 * Ocean View Resort, all registered observers are notified.
 * The EmailNotificationObserver sends a confirmation email,
 * and future observers (e.g., SMS) can be added without
 * modifying existing code (Open/Closed Principle).</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public interface IReservationObserver {

    /**
     * Called when a new reservation is created.
     *
     * @param reservation the newly created GuestReservation
     */
    void onReservationCreated(GuestReservation reservation);

    /**
     * Called when a reservation status is updated.
     *
     * @param reservation the updated GuestReservation
     * @param newStatus the new status value
     */
    void onReservationStatusChanged(GuestReservation reservation,
                                    String newStatus);

    /**
     * Returns the name of this observer for logging purposes.
     *
     * @return the observer name
     */
    String getObserverName();
}