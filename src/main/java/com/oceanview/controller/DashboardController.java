package com.oceanview.controller;

import com.oceanview.model.GuestReservation;
import com.oceanview.model.SystemUser;
import com.oceanview.service.IDashboardDataOrchestrator;
import com.oceanview.service.DashboardDataOrchestratorImpl;
import com.oceanview.service.IBookingFlowOrchestrator;
import com.oceanview.service.BookingFlowOrchestratorImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Controller Servlet for the dashboard page.
 *
 * <p><b>RBAC:</b> Both ADMIN and RECEPTIONIST access this controller,
 * but different data is loaded based on the role:
 * - ADMIN: Full statistics, all recent bookings, room availability grid
 * - RECEPTIONIST: Basic stats, only own recent bookings</p>
 *
 * <p><b>Requirement Traceability:</b> Dashboard provides an overview
 * of the reservation system status and facilitates quick navigation
 * to core features.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
@WebServlet("/DashboardController")
public class DashboardController extends HttpServlet {

    /** Logger for dashboard events */
    private static final Logger DASH_LOGGER =
            Logger.getLogger(DashboardController.class.getName());

    /** Dashboard data service */
    private IDashboardDataOrchestrator dashboardService;

    /** Booking service for receptionist's own bookings */
    private IBookingFlowOrchestrator bookingService;

    @Override
    public void init() throws ServletException {
        dashboardService = new DashboardDataOrchestratorImpl();
        bookingService = new BookingFlowOrchestratorImpl();
    }

    /**
     * Loads dashboard data and forwards to dashboard.jsp.
     * Data loaded varies based on user role.
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        SystemUser loggedUser =
                (SystemUser) session.getAttribute("loggedInUser");

        String userRole = loggedUser.getUserRole();

        // ---- Common data for both roles ----
        int totalRooms = dashboardService.getTotalRoomCount();
        int activeBookings = dashboardService.getActiveBookingCount();
        int availableRooms = dashboardService.getAvailableRoomCount();
        double totalRevenue = dashboardService.getTotalRevenue();

        request.setAttribute("totalRooms", totalRooms);
        request.setAttribute("activeBookings", activeBookings);
        request.setAttribute("availableRooms", availableRooms);
        request.setAttribute("totalRevenue", totalRevenue);

        // ---- Room availability by type (2x2 grid) ----
        Map<String, Integer> roomAvailability =
                dashboardService.getRoomAvailabilityByType();
        request.setAttribute("roomAvailability", roomAvailability);

        // ---- Role-specific data ----
        if ("ADMIN".equals(userRole)) {
            // Admin sees all recent bookings
            List<GuestReservation> recentBookings =
                    dashboardService.getRecentBookings(10);
            request.setAttribute("recentBookings", recentBookings);

            // Admin gets occupancy rate for reports
            double occupancyRate = dashboardService.getOccupancyRate();
            request.setAttribute("occupancyRate", occupancyRate);

            DASH_LOGGER.info("Admin dashboard loaded for: "
                    + loggedUser.getUsername());

        } else {
            // Receptionist sees only their own recent bookings
            List<GuestReservation> ownBookings =
                    bookingService.getReservationsByCreator(
                            loggedUser.getUserId());
            // Limit to 5 most recent
            if (ownBookings.size() > 5) {
                ownBookings = ownBookings.subList(0, 5);
            }
            request.setAttribute("recentBookings", ownBookings);

            DASH_LOGGER.info("Receptionist dashboard loaded for: "
                    + loggedUser.getUsername());
        }

        request.getRequestDispatcher("/dashboard.jsp")
                .forward(request, response);
    }
}