package com.oceanview.service;

import com.oceanview.dao.IGuestReservationGateway;
import com.oceanview.dao.GuestReservationGatewayImpl;
import com.oceanview.model.GuestReservation;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//Service Implementation for reservation booking workflow.//

public class BookingFlowOrchestratorImpl
        implements IBookingFlowOrchestrator {

   
    private static final Logger BOOKING_LOGGER =
            Logger.getLogger(BookingFlowOrchestratorImpl.class.getName());

   
    private final IGuestReservationGateway reservationGateway;

 
    public BookingFlowOrchestratorImpl() {
        this.reservationGateway = new GuestReservationGatewayImpl();
    }

  
    public BookingFlowOrchestratorImpl(
            IGuestReservationGateway reservationGateway) {
        this.reservationGateway = reservationGateway;
    }


    @Override
    public int createNewReservation(GuestReservation reservation)
            throws IllegalArgumentException {

        // Step 1: Validate all input fields
        validateReservationInput(reservation);

        try {
            // Step 2: Generate unique reservation number
            String reservationNumber =
                    reservationGateway.generateReservationNumber();

            if (reservationNumber == null || reservationNumber.isEmpty()) {
                BOOKING_LOGGER.severe(
                        "Failed to generate reservation number");
                return -1;
            }

            // Step 3: Set the generated number on the reservation
            reservation.setReservationNumber(reservationNumber);

            // Step 4: Insert into database (trigger validates dates)
            int newReservationId =
                    reservationGateway.insertReservation(reservation);

            if (newReservationId > 0) {
                BOOKING_LOGGER.info("Reservation created successfully: "
                        + reservationNumber + " (ID: " + newReservationId + ")");
            } else {
                BOOKING_LOGGER.warning(
                        "Reservation insertion returned invalid ID");
            }

            return newReservationId;

        } catch (Exception createException) {
            BOOKING_LOGGER.log(Level.SEVERE,
                    "Error creating reservation", createException);
            return -1;
        }
    }

    /** {@inheritDoc} */
    @Override
    public GuestReservation getReservationDetails(String reservationNumber) {
        if (reservationNumber == null || reservationNumber.trim().isEmpty()) {
            BOOKING_LOGGER.warning(
                    "Reservation number is null or empty for detail lookup");
            return null;
        }
        return reservationGateway.findReservationByNumber(
                reservationNumber.trim());
    }

    /** {@inheritDoc} */
    @Override
    public GuestReservation getReservationById(int reservationId) {
        if (reservationId <= 0) {
            BOOKING_LOGGER.warning("Invalid reservation ID: " + reservationId);
            return null;
        }
        return reservationGateway.findReservationById(reservationId);
    }

    /** {@inheritDoc} */
    @Override
    public List<GuestReservation> getAllReservations() {
        return reservationGateway.findAllReservations();
    }

    /** {@inheritDoc} */
    @Override
    public List<GuestReservation> getReservationsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return reservationGateway.findAllReservations();
        }
        return reservationGateway.findReservationsByStatus(status);
    }

    /** {@inheritDoc} */
    @Override
    public List<GuestReservation> getRecentReservations(int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        return reservationGateway.findRecentReservations(limit);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Status transition rules:
     * - Confirmed → Checked-In (guest arrives)
     * - Confirmed → Cancelled (with mandatory reason)
     * - Checked-In → Checked-Out (guest departs)
     * The after_reservation_update trigger handles room
     * availability updates automatically.</p>
     */
    @Override
    public boolean updateReservationStatus(int reservationId,
                                           String newStatus,
                                           String cancelReason) {

        if (reservationId <= 0 || newStatus == null) {
            BOOKING_LOGGER.warning("Invalid input for status update");
            return false;
        }

        // Validate cancel reason is provided for cancellations
        if ("Cancelled".equals(newStatus)
                && (cancelReason == null || cancelReason.trim().isEmpty())) {
            BOOKING_LOGGER.warning(
                    "Cancel reason is required for cancellation");
            return false;
        }

        boolean updateSuccess = reservationGateway.updateReservationStatus(
                reservationId, newStatus, cancelReason);

        if (updateSuccess) {
            BOOKING_LOGGER.info("Reservation " + reservationId
                    + " status updated to: " + newStatus);
        }

        return updateSuccess;
    }

    /** {@inheritDoc} */
    @Override
    public List<GuestReservation> getReservationsByCreator(int userId) {
        if (userId <= 0) {
            return List.of();
        }
        return reservationGateway.findReservationsByCreator(userId);
    }

    /** {@inheritDoc} */
    @Override
    public int getReservationCount(String status) {
        if (status == null || status.trim().isEmpty()) {
            return 0;
        }
        return reservationGateway.getReservationCountByStatus(status);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValidGuestName(String guestName) {
        if (guestName == null || guestName.trim().isEmpty()) {
            return false;
        }
        String trimmedName = guestName.trim();
        // Minimum 2 characters, letters and spaces only
        return trimmedName.length() >= 2
                && trimmedName.matches("^[a-zA-Z\\s]+$");
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValidContactNumber(String contactNumber) {
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            return false;
        }
        // Sri Lankan phone number: starts with 0, exactly 10 digits
        return contactNumber.trim().matches("^0\\d{9}$");
    }

    /** {@inheritDoc} */
    @Override
    public boolean areValidDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            return false;
        }
        // Check-in must be today or future, check-out must be after check-in
        return !checkIn.isBefore(LocalDate.now())
                && checkOut.isAfter(checkIn);
    }

    /**
     * Performs comprehensive validation on all reservation fields.
     * Throws IllegalArgumentException with a descriptive message
     * if any validation rule is violated.
     *
     * @param reservation the GuestReservation to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateReservationInput(GuestReservation reservation)
            throws IllegalArgumentException {

        if (reservation == null) {
            throw new IllegalArgumentException(
                    "Reservation data cannot be null");
        }

        if (!isValidGuestName(reservation.getGuestName())) {
            throw new IllegalArgumentException(
                    "Invalid guest name. Must be at least 2 characters "
                            + "and contain only letters and spaces.");
        }

        if (reservation.getAddress() == null
                || reservation.getAddress().trim().length() < 5) {
            throw new IllegalArgumentException(
                    "Address must be at least 5 characters long.");
        }

        if (!isValidContactNumber(reservation.getContactNumber())) {
            throw new IllegalArgumentException(
                    "Invalid contact number. Must be a 10-digit "
                            + "Sri Lankan number starting with 0.");
        }

        if (reservation.getRoomType() == null
                || reservation.getRoomType().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Room type must be selected.");
        }

        if (reservation.getRoomId() <= 0) {
            throw new IllegalArgumentException(
                    "A valid room must be selected.");
        }

        if (!areValidDates(reservation.getCheckInDate(),
                reservation.getCheckOutDate())) {
            throw new IllegalArgumentException(
                    "Invalid dates. Check-in must be today or later, "
                            + "and check-out must be after check-in.");
        }

        BOOKING_LOGGER.fine("Reservation input validation passed");
    }
}