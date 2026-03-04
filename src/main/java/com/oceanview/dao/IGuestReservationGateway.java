package com.oceanview.dao;

import com.oceanview.model.GuestReservation;
import java.util.List;

/**
 * DAO Interface for GuestReservation operations.
 *
 * <p><b>Requirement Traceability:</b> Supports "Add New Reservation",
 * "Display Reservation Details", and reservation management features.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public interface IGuestReservationGateway {

    /**
     * Generates a unique reservation number using the
     * stored procedure GenerateReservationNumber.
     *
     * @return the generated reservation number (format: RES-YYYY-NNNNN)
     */
    String generateReservationNumber();

    /**
     * Inserts a new reservation into the database.
     *
     * @param reservation the GuestReservation to insert
     * @return the auto-generated reservation ID, or -1 on failure
     */
    int insertReservation(GuestReservation reservation);

    /**
     * Finds a reservation by its number using the
     * stored procedure GetReservationDetails.
     *
     * @param reservationNumber the reservation number to search
     * @return the GuestReservation with full details, or null
     */
    GuestReservation findReservationByNumber(String reservationNumber);

    /**
     * Finds a reservation by its database ID.
     *
     * @param reservationId the reservation ID to search
     * @return the GuestReservation, or null if not found
     */
    GuestReservation findReservationById(int reservationId);

    /**
     * Retrieves all reservations with room details.
     *
     * @return list of all GuestReservation objects
     */
    List<GuestReservation> findAllReservations();

    /**
     * Retrieves reservations filtered by status.
     *
     * @param status the status to filter by
     * @return list of matching GuestReservation objects
     */
    List<GuestReservation> findReservationsByStatus(String status);

    /**
     * Updates a reservation's status and optional cancel reason.
     *
     * @param reservationId the reservation ID to update
     * @param newStatus the new status value
     * @param cancelReason the reason (only for Cancelled status)
     * @return true if update was successful
     */
    boolean updateReservationStatus(int reservationId,
                                    String newStatus, String cancelReason);

    /**
     * Retrieves the most recent reservations.
     *
     * @param limit maximum number of records to return
     * @return list of recent GuestReservation objects
     */
    List<GuestReservation> findRecentReservations(int limit);

    /**
     * Gets the count of reservations by status.
     *
     * @param status the status to count
     * @return the number of reservations with that status
     */
    int getReservationCountByStatus(String status);

    /**
     * Finds reservations created by a specific user.
     *
     * @param userId the creator's user ID
     * @return list of GuestReservation objects by that user
     */
    List<GuestReservation> findReservationsByCreator(int userId);
}