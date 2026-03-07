package com.oceanview.service;

import com.oceanview.model.GuestReservation;
import java.util.List;
import java.util.Map;

//Service Interface for dashboard statistics and report data.

public interface IDashboardDataOrchestrator {


    int getTotalRoomCount();

    int getActiveBookingCount();

    int getAvailableRoomCount();

    double getTotalRevenue();

    List<GuestReservation> getRecentBookings(int limit);

    Map<String, Integer> getRoomAvailabilityByType();

    Map<String, Integer> getReservationStatusBreakdown();

    Map<String, Double> getRevenueByRoomType();

    double getOccupancyRate();
}