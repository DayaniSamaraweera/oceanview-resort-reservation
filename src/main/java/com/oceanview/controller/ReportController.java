package com.oceanview.controller;

import com.oceanview.service.IDashboardDataOrchestrator;
import com.oceanview.service.DashboardDataOrchestratorImpl;
import com.oceanview.service.IBillingOrchestrator;
import com.oceanview.service.BillingOrchestratorImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 Loads report data for admin decision-making.
 Includes occupancy rate, revenue by room type,
 booking patterns and room availability.*/

@WebServlet("/ReportController")
public class ReportController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    private static final Logger REPORT_LOGGER =
            Logger.getLogger(ReportController.class.getName());

    
    private IDashboardDataOrchestrator dashboardService;

   
    private IBillingOrchestrator billingService;

    @Override
    public void init() throws ServletException {
        dashboardService = new DashboardDataOrchestratorImpl();
        billingService = new BillingOrchestratorImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Occupancy rate
        double occupancyRate = dashboardService.getOccupancyRate();
        request.setAttribute("occupancyRate", occupancyRate);

        // Room availability by type
        Map<String, Integer> roomAvailability =
                dashboardService.getRoomAvailabilityByType();
        request.setAttribute("roomAvailability", roomAvailability);

        // Reservation status breakdown
        Map<String, Integer> statusBreakdown =
                dashboardService.getReservationStatusBreakdown();
        request.setAttribute("statusBreakdown", statusBreakdown);

        // Revenue by room type
        Map<String, Double> revenueByType =
                dashboardService.getRevenueByRoomType();
        request.setAttribute("revenueByType", revenueByType);

        // Overall stats
        int totalRooms = dashboardService.getTotalRoomCount();
        int availableRooms = dashboardService.getAvailableRoomCount();
        double totalRevenue = billingService.getTotalRevenue();
        int totalBills = billingService.getTotalBillCount();

        request.setAttribute("totalRooms", totalRooms);
        request.setAttribute("availableRooms", availableRooms);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalBills", totalBills);

        REPORT_LOGGER.info("Reports page loaded with current data");

        request.getRequestDispatcher("/reports.jsp")
                .forward(request, response);
    }
}