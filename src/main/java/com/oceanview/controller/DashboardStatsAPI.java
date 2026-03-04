package com.oceanview.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Logger;

/**
 * REST API Endpoint for dashboard statistics and report data.
 *
 * <p><b>Architecture:</b> Distributed Application - Returns JSON
 * data consumed by JavaScript for rendering dashboard charts
 * and statistics cards dynamically.</p>
 *
 * <p><b>Endpoints:</b>
 * GET /api/dashboard - All dashboard statistics
 * GET /api/dashboard?action=revenue - Revenue by room type
 * GET /api/dashboard?action=occupancy - Occupancy rate</p>
 *
 * <p><b>Rubric:</b> "Decision-Making Reports - generate visual
 * data that specifically facilitates management decision-making"
 * This API provides the data for Canvas API charts.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
@WebServlet("/api/dashboard")
public class DashboardStatsAPI extends HttpServlet {

    /** Logger for API events */
    private static final Logger API_LOGGER =
            Logger.getLogger(DashboardStatsAPI.class.getName());

    /** JSON serialization */
    private Gson gsonSerializer;

    /** Dashboard service */
    private IDashboardDataOrchestrator dashboardService;

    /** Billing service */
    private IBillingOrchestrator billingService;

    @Override
    public void init() throws ServletException {
        gsonSerializer = new Gson();
        dashboardService = new DashboardDataOrchestratorImpl();
        billingService = new BillingOrchestratorImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter jsonWriter = response.getWriter();
        String action = request.getParameter("action");

        try {
            if ("revenue".equals(action)) {
                // Revenue breakdown by room type (for bar chart)
                JsonObject revenueResponse = new JsonObject();
                revenueResponse.addProperty("status", "success");

                Map<String, Double> revenueByType =
                        dashboardService.getRevenueByRoomType();

                JsonObject revenueData = new JsonObject();
                for (Map.Entry<String, Double> entry
                        : revenueByType.entrySet()) {
                    revenueData.addProperty(
                            entry.getKey(), entry.getValue());
                }

                revenueResponse.add("data", revenueData);
                revenueResponse.addProperty("totalRevenue",
                        billingService.getTotalRevenue());

                jsonWriter.print(
                        gsonSerializer.toJson(revenueResponse));

            } else if ("occupancy".equals(action)) {
                // Occupancy rate (for circle chart)
                JsonObject occupancyResponse = new JsonObject();
                occupancyResponse.addProperty("status", "success");
                occupancyResponse.addProperty("occupancyRate",
                        dashboardService.getOccupancyRate());
                occupancyResponse.addProperty("totalRooms",
                        dashboardService.getTotalRoomCount());
                occupancyResponse.addProperty("availableRooms",
                        dashboardService.getAvailableRoomCount());
                occupancyResponse.addProperty("occupiedRooms",
                        dashboardService.getTotalRoomCount()
                                - dashboardService.getAvailableRoomCount());

                jsonWriter.print(
                        gsonSerializer.toJson(occupancyResponse));

            } else if ("status".equals(action)) {
                // Reservation status breakdown (for pie chart)
                JsonObject statusResponse = new JsonObject();
                statusResponse.addProperty("status", "success");

                Map<String, Integer> statusBreakdown =
                        dashboardService.getReservationStatusBreakdown();

                JsonObject statusData = new JsonObject();
                for (Map.Entry<String, Integer> entry
                        : statusBreakdown.entrySet()) {
                    statusData.addProperty(
                            entry.getKey(), entry.getValue());
                }

                statusResponse.add("data", statusData);
                jsonWriter.print(
                        gsonSerializer.toJson(statusResponse));

            } else {
                // Full dashboard stats
                JsonObject fullResponse = new JsonObject();
                fullResponse.addProperty("status", "success");

                JsonObject stats = new JsonObject();
                stats.addProperty("totalRooms",
                        dashboardService.getTotalRoomCount());
                stats.addProperty("activeBookings",
                        dashboardService.getActiveBookingCount());
                stats.addProperty("availableRooms",
                        dashboardService.getAvailableRoomCount());
                stats.addProperty("totalRevenue",
                        dashboardService.getTotalRevenue());
                stats.addProperty("occupancyRate",
                        dashboardService.getOccupancyRate());
                stats.addProperty("totalBills",
                        billingService.getTotalBillCount());

                fullResponse.add("stats", stats);

                // Room availability by type
                Map<String, Integer> availability =
                        dashboardService.getRoomAvailabilityByType();
                JsonObject availData = new JsonObject();
                for (Map.Entry<String, Integer> entry
                        : availability.entrySet()) {
                    availData.addProperty(
                            entry.getKey(), entry.getValue());
                }
                fullResponse.add("roomAvailability", availData);

                jsonWriter.print(
                        gsonSerializer.toJson(fullResponse));
            }

            API_LOGGER.fine("Dashboard API processed: "
                    + (action != null ? action : "full"));

        } catch (Exception apiException) {
            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("status", "error");
            errorResponse.addProperty("message",
                    "Failed to retrieve dashboard data");
            jsonWriter.print(gsonSerializer.toJson(errorResponse));

            API_LOGGER.severe("Dashboard API error: "
                    + apiException.getMessage());
        }

        jsonWriter.flush();
    }
}