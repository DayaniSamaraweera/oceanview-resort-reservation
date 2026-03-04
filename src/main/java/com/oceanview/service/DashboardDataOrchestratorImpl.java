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

/**
 * Service Implementation for dashboard statistics and report data.
 *
 * <p><b>Architecture:</b> Business Logic Layer - aggregates data
 * from multiple DAOs to provide comprehensive dashboard statistics
 * and decision-making report data.</p>
 *
 * <p><b>Requirement Traceability:</b>
 * - Dashboard stats cards: total rooms, active bookings, available rooms, revenue
 * - Room availability grid: breakdown by type
 * - Reports: occupancy rate, revenue by room type, status breakdown</p>
 *
 * <p><b>Decision-Making Reports:</b> The revenue by room type and
 * occupancy rate data specifically facilitate management decisions
 * regarding pricing strategy, room allocation, and staffing needs.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public class DashboardDataOrchestratorImpl
        implements IDashboardDataOrchestrator {

    /** Logger for dashboard data events */
    private static final Logger DASHBOARD_LOGGER =
            Logger.getLogger(DashboardDataOrchestratorImpl.class.getName());

    /** DAO dependency for room operations */
    private final IResortRoomGateway roomGateway;

    /** DAO dependency for reservation operations */
    private final IGuestReservationGateway reservationGateway;

    /** DAO dependency for billing operations */
    private final IInvoiceRecordGateway invoiceGateway;

    /** Database manager for custom report queries */
    private final DatabaseConnectionManager dbManager;

    /**
     * Default constructor using concrete DAO implementations.
     */
    public DashboardDataOrchestratorImpl() {
        this.roomGateway = new ResortRoomGatewayImpl();
        this.reservationGateway = new GuestReservationGatewayImpl();
        this.invoiceGateway = new InvoiceRecordGatewayImpl();
        this.dbManager = DatabaseConnectionManager.getInstance();
    }

    /**
     * Constructor with injected DAOs for Mockito testing.
     *
     * @param roomGateway the room DAO (or mock)
     * @param reservationGateway the reservation DAO (or mock)
     * @param invoiceGateway the invoice DAO (or mock)
     */
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

    /**
     * {@inheritDoc}
     * Active bookings include both Confirmed and Checked-In statuses.
     */
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

    /**
     * {@inheritDoc}
     * Provides a complete breakdown of reservations across all statuses.
     */
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

    /**
     * {@inheritDoc}
     *
     * <p>Runs a custom aggregate query to calculate total revenue
     * per room type. This data helps management identify which
     * room categories generate the most income, supporting
     * decisions on pricing, marketing, and room allocation.</p>
     */
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

    /**
     * {@inheritDoc}
     *
     * <p>Occupancy rate formula:
     * (total rooms - available rooms) / total rooms × 100
     *
     * This metric helps management understand current hotel
     * utilization and plan staffing accordingly.</p>
     */
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