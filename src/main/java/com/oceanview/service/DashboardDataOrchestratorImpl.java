package com.oceanview.service;

import com.oceanview.dao.DatabaseConnectionManager;
import com.oceanview.dao.IResortRoomGateway;
import com.oceanview.dao.IGuestReservationGateway;
import com.oceanview.dao.IInvoiceRecordGateway;
import com.oceanview.dao.ResortRoomGatewayImpl;
import com.oceanview.dao.GuestReservationGatewayImpl;
import com.oceanview.dao.InvoiceRecordGatewayImpl;
import com.oceanview.model.GuestReservation;
import com.oceanview.model.ResortRoom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//Service Implementation for dashboard statistics and report data.

public class DashboardDataOrchestratorImpl
        implements IDashboardDataOrchestrator {

    private static final Logger DASHBOARD_LOGGER =
            Logger.getLogger(DashboardDataOrchestratorImpl.class.getName());

   
    private final IResortRoomGateway roomGateway;

    
    private final IGuestReservationGateway reservationGateway;


    private final IInvoiceRecordGateway invoiceGateway;

  
    private final DatabaseConnectionManager dbManager;

  
    public DashboardDataOrchestratorImpl() {
        this.roomGateway = new ResortRoomGatewayImpl();
        this.reservationGateway = new GuestReservationGatewayImpl();
        this.invoiceGateway = new InvoiceRecordGatewayImpl();
        this.dbManager = DatabaseConnectionManager.getInstance();
    }

    // Constructor with injected DAOs for Mockito testing.//
    
    public DashboardDataOrchestratorImpl(
            IResortRoomGateway roomGateway,
            IGuestReservationGateway reservationGateway,
            IInvoiceRecordGateway invoiceGateway) {
        this.roomGateway = roomGateway;
        this.reservationGateway = reservationGateway;
        this.invoiceGateway = invoiceGateway;
        this.dbManager = DatabaseConnectionManager.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public int getTotalRoomCount() {
        List<ResortRoom> allRooms = roomGateway.findAllRooms();
        return allRooms.size();
    }

    //Active bookings include both Confirmed and Checked-In statuses.
     
    @Override
    public int getActiveBookingCount() {
        int confirmedCount =
                reservationGateway.getReservationCountByStatus("Confirmed");
        int checkedInCount =
                reservationGateway.getReservationCountByStatus("Checked-In");
        return confirmedCount + checkedInCount;
    }

    /** {@inheritDoc} */
    @Override
    public int getAvailableRoomCount() {
        return roomGateway.getAvailableRoomCount("ALL");
    }

    /** {@inheritDoc} */
    @Override
    public double getTotalRevenue() {
        return invoiceGateway.getTotalRevenue();
    }

    /** {@inheritDoc} */
    @Override
    public List<GuestReservation> getRecentBookings(int limit) {
        if (limit <= 0) {
            limit = 5;
        }
        return reservationGateway.findRecentReservations(limit);
    }

    /**
     * {@inheritDoc}
     * Returns a LinkedHashMap to preserve room type ordering:
     * Standard → Superior → Premium → Executive
     */
    @Override
    public Map<String, Integer> getRoomAvailabilityByType() {
        Map<String, Integer> availabilityMap = new LinkedHashMap<>();

        availabilityMap.put("Standard",
                roomGateway.getAvailableRoomCount("Standard"));
        availabilityMap.put("Superior",
                roomGateway.getAvailableRoomCount("Superior"));
        availabilityMap.put("Premium",
                roomGateway.getAvailableRoomCount("Premium"));
        availabilityMap.put("Executive",
                roomGateway.getAvailableRoomCount("Executive"));

        DASHBOARD_LOGGER.fine("Room availability loaded: " + availabilityMap);
        return availabilityMap;
    }

    //Provides a complete breakdown of reservations across all statuses.
     
    @Override
    public Map<String, Integer> getReservationStatusBreakdown() {
        Map<String, Integer> statusMap = new LinkedHashMap<>();

        statusMap.put("Confirmed",
                reservationGateway.getReservationCountByStatus("Confirmed"));
        statusMap.put("Checked-In",
                reservationGateway.getReservationCountByStatus("Checked-In"));
        statusMap.put("Checked-Out",
                reservationGateway.getReservationCountByStatus("Checked-Out"));
        statusMap.put("Cancelled",
                reservationGateway.getReservationCountByStatus("Cancelled"));

        return statusMap;
    }

    @Override
    public Map<String, Double> getRevenueByRoomType() {
        Map<String, Double> revenueMap = new LinkedHashMap<>();

        // Initialize with zero values to ensure all types appear
        revenueMap.put("Standard", 0.0);
        revenueMap.put("Superior", 0.0);
        revenueMap.put("Premium", 0.0);
        revenueMap.put("Executive", 0.0);

        String revenueQuery = "SELECT room_type, "
                + "COALESCE(SUM(total_amount), 0) AS type_revenue "
                + "FROM bills "
                + "GROUP BY room_type ORDER BY room_type";

        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement revenueStatement =
                    dbConnection.prepareStatement(revenueQuery);
            ResultSet revenueResult = revenueStatement.executeQuery();

            while (revenueResult.next()) {
                String roomType = revenueResult.getString("room_type");
                double typeRevenue =
                        revenueResult.getDouble("type_revenue");
                revenueMap.put(roomType, typeRevenue);
            }

            DASHBOARD_LOGGER.info(
                    "Revenue by room type calculated: " + revenueMap);

        } catch (SQLException revenueException) {
            DASHBOARD_LOGGER.log(Level.SEVERE,
                    "Error calculating revenue by room type",
                    revenueException);
        } finally {
            dbManager.closeConnection(dbConnection);
        }

        return revenueMap;
    }

    @Override
    public double getOccupancyRate() {
        int totalRooms = getTotalRoomCount();

        if (totalRooms == 0) {
            return 0.0;
        }

        int availableRooms = getAvailableRoomCount();
        int occupiedRooms = totalRooms - availableRooms;

        double occupancyRate =
                ((double) occupiedRooms / totalRooms) * 100.0;

        // Round to 1 decimal place
        occupancyRate = Math.round(occupancyRate * 10.0) / 10.0;

        DASHBOARD_LOGGER.fine("Occupancy rate: " + occupancyRate + "%");
        return occupancyRate;
    }
}