package com.oceanview.service;

import com.oceanview.model.GuestReservation;
import java.time.LocalDate;
import java.util.List;

//Service Interface for reservation booking operations.

public interface IBookingFlowOrchestrator {


    int createNewReservation(GuestReservation reservation)
            throws IllegalArgumentException;

    GuestReservation getReservationDetails(String reservationNumber);

    GuestReservation getReservationById(int reservationId);

    List<GuestReservation> getAllReservations();

    List<GuestReservation> getReservationsByStatus(String status);

    List<GuestReservation> getRecentReservations(int limit);

    boolean updateReservationStatus(int reservationId,
                                    String newStatus, String cancelReason);

    List<GuestReservation> getReservationsByCreator(int userId);


    int getReservationCount(String status);

 
    boolean isValidGuestName(String guestName);


    boolean isValidContactNumber(String contactNumber);


    boolean areValidDates(LocalDate checkIn, LocalDate checkOut);
}