package com.oceanview.dao;

import com.oceanview.model.GuestReservation;
import java.util.List;

//DAO interface for reservation operations.

public interface IGuestReservationGateway {
	
	
    String generateReservationNumber();


    int insertReservation(GuestReservation reservation);


    GuestReservation findReservationByNumber(String reservationNumber);
    
 
    GuestReservation findReservationById(int reservationId);

 
    List<GuestReservation> findAllReservations();

  
    List<GuestReservation> findReservationsByStatus(String status);

 // cancel reason only required when status is Cancelled
    boolean updateReservationStatus(int reservationId,
                                    String newStatus, String cancelReason);

  
    List<GuestReservation> findRecentReservations(int limit);

   
    int getReservationCountByStatus(String status);

 
    List<GuestReservation> findReservationsByCreator(int userId);
}