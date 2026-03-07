package com.oceanview.service;

import com.oceanview.model.GuestReservation;

//Observer Interface for the Observer Design Pattern.

public interface IReservationObserver {


    void onReservationCreated(GuestReservation reservation);

    void onReservationStatusChanged(GuestReservation reservation,
                                    String newStatus);

    String getObserverName();
}