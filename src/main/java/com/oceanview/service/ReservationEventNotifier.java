package com.oceanview.service;

import com.oceanview.model.GuestReservation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//Subject class for the Observer Design Pattern.

public class ReservationEventNotifier {

    private static final Logger NOTIFIER_LOGGER =
            Logger.getLogger(ReservationEventNotifier.class.getName());

  
    private static ReservationEventNotifier soleInstance;


    private final List<IReservationObserver> registeredObservers;

    //Private constructor enforces Singleton pattern.
     
    private ReservationEventNotifier() {
        this.registeredObservers = new ArrayList<>();
        NOTIFIER_LOGGER.info(
                "ReservationEventNotifier initialized");
    }


    public static synchronized ReservationEventNotifier getInstance() {
        if (soleInstance == null) {
            soleInstance = new ReservationEventNotifier();
        }
        return soleInstance;
    }

  
    public void registerObserver(IReservationObserver observer) {
        if (observer != null && !registeredObservers.contains(observer)) {
            registeredObservers.add(observer);
            NOTIFIER_LOGGER.info("Observer registered: "
                    + observer.getObserverName());
        }
    }

  
    public void removeObserver(IReservationObserver observer) {
        if (observer != null) {
            registeredObservers.remove(observer);
            NOTIFIER_LOGGER.info("Observer removed: "
                    + observer.getObserverName());
        }
    }


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


    public int getObserverCount() {
        return registeredObservers.size();
    }
}