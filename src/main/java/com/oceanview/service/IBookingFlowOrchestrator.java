package com.oceanview.service;

import com.oceanview.model.GuestReservation;
import java.time.LocalDate;
import java.util.List;

/**
 * Service Interface for reservation booking operations.
 *
 * <p><b>Requirement Traceability:</b>
 * - createNewReservation() → "Add New Reservation"
 * - getReservationDetails() → "Display Reservation Details"
 * - All methods support the reservation management workflow</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public interface IBookingFlowOrchestrator {

    /**
     * Creates a new reservation with full validation.
     *
     * @param reservation the GuestReservation to create
     * @return the reservation ID if successful, -1 on failure
     * @throws IllegalArgumentException if validation fails
     */
    int createNewReservation(GuestReservation reservation)
            throws IllegalArgumentException;

    /**
     * Retrieves complete reservation details by number.
     *
     * @param reservationNumber the reservation number to find
     * @return the GuestReservation with full details, or null
     */
    GuestReservation getReservationDetails(String reservationNumber);

    /**
     * Retrieves a reservation by its database ID.
     *
     * @param reservationId the ID to search
     * @return the GuestReservation, or null if not found
     */
    GuestReservation getReservationById(int reservationId);

    /**
     * Retrieves all reservations in the system.
     *
     * @return list of all GuestReservation objects
     */
    List<GuestReservation> getAllReservations();

    /**
     * Retrieves reservations filtered by status.
     *
     * @param status the status filter value
     * @return list of matching GuestReservation objects
     */
    List<GuestReservation> getReservationsByStatus(String status);

    /**
     * Gets the most recent reservations for dashboard display.
     *
     * @param limit maximum number of records
     * @return list of recent GuestReservation objects
     */
    List<GuestReservation> getRecentReservations(int limit);

    /**
     * Updates a reservation's status.
     *
     * @param reservationId the reservation to update
     * @param newStatus the new status value
     * @param cancelReason reason if cancelling (null otherwise)
     * @return true if update was successful
     */
    boolean updateReservationStatus(int reservationId,
                                    String newStatus, String cancelReason);

    /**
     * Gets reservations created by a specific user.
     *
     * @param userId the creator's user ID
     * @return list of GuestReservation objects
     */
    List<GuestReservation> getReservationsByCreator(int userId);

    /**
     * Gets the count of reservations by status.
     *
     * @param status the status to count
     * @return the count
     */
    int getReservationCount(String status);

    /**
     * Validates guest name format.
     *
     * @param guestName the name to validate
     * @return true if valid
     */
    boolean isValidGuestName(String guestName);

    /**
     * Validates contact number format.
     *
     * @param contactNumber the number to validate
     * @return true if valid (Sri Lankan format)
     */
    boolean isValidContactNumber(String contactNumber);

    /**
     * Validates check-in and check-out date logic.
     *
     * @param checkIn the check-in date
     * @param checkOut the check-out date
     * @return true if dates are valid
     */
    boolean areValidDates(LocalDate checkIn, LocalDate checkOut);
}