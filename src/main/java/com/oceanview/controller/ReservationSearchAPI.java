package com.oceanview.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.oceanview.model.GuestReservation;
import com.oceanview.service.IBookingFlowOrchestrator;
import com.oceanview.service.BookingFlowOrchestratorImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

/**
 REST API that returns reservation data as JSON for AJAX search
 and filtering without page reloads.*/

@WebServlet("/api/reservations")
public class ReservationSearchAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    private static final Logger API_LOGGER =
            Logger.getLogger(ReservationSearchAPI.class.getName());

   
    private Gson gsonSerializer;

    private IBookingFlowOrchestrator bookingService;

    @Override
    public void init() throws ServletException {
        gsonSerializer = new Gson();
        bookingService = new BookingFlowOrchestratorImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter jsonWriter = response.getWriter();

        String numberParam = request.getParameter("number");
        String statusParam = request.getParameter("status");
        String actionParam = request.getParameter("action");
        String limitParam = request.getParameter("limit");

        try {
            if (numberParam != null && !numberParam.isEmpty()) {
                // Search by reservation number
                GuestReservation found =
                        bookingService.getReservationDetails(
                                numberParam.trim());

                JsonObject searchResponse = new JsonObject();

                if (found != null) {
                    searchResponse.addProperty("status", "success");
                    searchResponse.add("reservation",
                            buildReservationJson(found));
                } else {
                    searchResponse.addProperty("status", "not_found");
                    searchResponse.addProperty("message",
                            "No reservation found with number: "
                                    + numberParam);
                }

                jsonWriter.print(
                        gsonSerializer.toJson(searchResponse));

            } else if ("recent".equals(actionParam)) {
                // Recent reservations
                int limit = 5;
                if (limitParam != null) {
                    try {
                        limit = Integer.parseInt(limitParam);
                    } catch (NumberFormatException numErr) {
                        limit = 5;
                    }
                }

                List<GuestReservation> recentList =
                        bookingService.getRecentReservations(limit);

                JsonObject recentResponse = new JsonObject();
                recentResponse.addProperty("status", "success");
                recentResponse.addProperty("count",
                        recentList.size());
                recentResponse.add("reservations",
                        buildReservationListJson(recentList));

                jsonWriter.print(
                        gsonSerializer.toJson(recentResponse));

            } else if (statusParam != null && !statusParam.isEmpty()) {
                // Filter by status
                List<GuestReservation> filteredList =
                        bookingService.getReservationsByStatus(
                                statusParam);

                JsonObject filteredResponse = new JsonObject();
                filteredResponse.addProperty("status", "success");
                filteredResponse.addProperty("filter", statusParam);
                filteredResponse.addProperty("count",
                        filteredList.size());
                filteredResponse.add("reservations",
                        buildReservationListJson(filteredList));

                jsonWriter.print(
                        gsonSerializer.toJson(filteredResponse));

            } else {
                // All reservations
                List<GuestReservation> allList =
                        bookingService.getAllReservations();

                JsonObject allResponse = new JsonObject();
                allResponse.addProperty("status", "success");
                allResponse.addProperty("count", allList.size());
                allResponse.add("reservations",
                        buildReservationListJson(allList));

                jsonWriter.print(
                        gsonSerializer.toJson(allResponse));
            }

            API_LOGGER.fine("Reservation API request processed");

        } catch (Exception apiException) {
            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("status", "error");
            errorResponse.addProperty("message",
                    "Failed to retrieve reservation data");
            jsonWriter.print(gsonSerializer.toJson(errorResponse));

            API_LOGGER.severe("Reservation API error: "
                    + apiException.getMessage());
        }

        jsonWriter.flush();
    }

    /**
     * Builds a JSON object for a single reservation.
     *
     * @param reservation the GuestReservation to serialize
     * @return JsonObject with reservation data
     */
    private JsonObject buildReservationJson(
            GuestReservation reservation) {

        JsonObject resObj = new JsonObject();
        resObj.addProperty("reservationId",
                reservation.getReservationId());
        resObj.addProperty("reservationNumber",
                reservation.getReservationNumber());
        resObj.addProperty("guestName",
                reservation.getGuestName());
        resObj.addProperty("address",
                reservation.getAddress());
        resObj.addProperty("contactNumber",
                reservation.getContactNumber());
        resObj.addProperty("guestEmail",
                reservation.getGuestEmail() != null
                        ? reservation.getGuestEmail() : "");
        resObj.addProperty("roomType",
                reservation.getRoomType());
        resObj.addProperty("roomNumber",
                reservation.getRoomNumber() != null
                        ? reservation.getRoomNumber() : "");
        resObj.addProperty("ratePerNight",
                reservation.getRatePerNight());
        resObj.addProperty("checkInDate",
                reservation.getCheckInDate() != null
                        ? reservation.getCheckInDate().toString() : "");
        resObj.addProperty("checkOutDate",
                reservation.getCheckOutDate() != null
                        ? reservation.getCheckOutDate().toString() : "");
        resObj.addProperty("numberOfNights",
                reservation.getNumberOfNights());
        resObj.addProperty("reservationStatus",
                reservation.getReservationStatus());
        resObj.addProperty("cancelReason",
                reservation.getCancelReason() != null
                        ? reservation.getCancelReason() : "");

        return resObj;
    }

   
 
    private JsonArray buildReservationListJson(
            List<GuestReservation> reservations) {

        JsonArray resArray = new JsonArray();
        for (GuestReservation res : reservations) {
            resArray.add(buildReservationJson(res));
        }
        return resArray;
    }
}