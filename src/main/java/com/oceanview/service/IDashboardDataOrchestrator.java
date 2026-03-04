package com.oceanview.service;

import com.oceanview.model.GuestReservation;
import java.util.List;
import java.util.Map;

/**
 * Service Interface for dashboard statistics and report data.
 *
 * <p><b>Requirement Traceability:</b> Supports the dashboard
 * display with statistics cards and facilitates management
 * decision-making through comprehensive data aggregation.</p>
 *
 * <p><b>Rubric:</b> "Decision-Making Reports - generate visual
 * data that specifically facilitates management decision-making"</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public interface IDashboardDataOrchestrator {

    /**
     * Gets total number of rooms in the resort.
     *
     * @return total room count
     */
    int getTotalRoomCount();

    /**
     * Gets count of currently active (Confirmed + Checked-In) bookings.
     *
     * @return active booking count
     */
    int getActiveBookingCount();

    /**
     * Gets count of currently available rooms.
     *
     * @return available room count
     */
    int getAvailableRoomCount();

    /**
     * Gets total revenue from all generated bills.
     *
     * @return total revenue in LKR
     */
    double getTotalRevenue();

    /**
     * Gets recent reservations for the dashboard table.
     *
     * @param limit maximum number of records
     * @return list of recent GuestReservation objects
     */
    List<GuestReservation> getRecentBookings(int limit);

    /**
     * Gets room availability breakdown by type for the
     * dashboard grid and reports.
     *
     * @return map of room type to available count
     */
    Map<String, Integer> getRoomAvailabilityByType();

    /**
     * Gets reservation count breakdown by status for reports.
     *
     * @return map of status to count
     */
    Map<String, Integer> getReservationStatusBreakdown();

    /**
     * Gets revenue breakdown by room type for reports.
     * Facilitates management decision-making on pricing.
     *
     * @return map of room type to total revenue
     */
    Map<String, Double> getRevenueByRoomType();

    /**
     * Calculates the occupancy rate as a percentage.
     * (booked rooms / total rooms) × 100
     *
     * @return occupancy rate percentage
     */
    double getOccupancyRate();
}