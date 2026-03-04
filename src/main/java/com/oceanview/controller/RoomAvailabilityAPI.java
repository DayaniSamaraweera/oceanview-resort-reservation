package com.oceanview.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.oceanview.model.ResortRoom;
import com.oceanview.service.IRoomInventoryOrchestrator;
import com.oceanview.service.RoomInventoryOrchestratorImpl;

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
 * REST API Endpoint for room availability data.
 *
 * <p><b>Architecture:</b> Distributed Application - This servlet
 * returns JSON responses instead of forwarding to JSP pages.
 * Strictly separated from page-navigation servlets.</p>
 *
 * <p><b>Endpoints:</b>
 * GET /api/rooms - All rooms with availability
 * GET /api/rooms?type=Standard - Rooms filtered by type
 * GET /api/rooms?action=counts - Availability counts per type</p>
 *
 * <p><b>Usage:</b> Called by JavaScript fetch() on the frontend
 * for dynamic room availability updates without page reload
 * (Event-Driven Architecture).</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
@WebServlet("/api/rooms")
public class RoomAvailabilityAPI extends HttpServlet {

    /** Logger for API events */
    private static final Logger API_LOGGER =
            Logger.getLogger(RoomAvailabilityAPI.class.getName());

    /** JSON serialization library */
    private Gson gsonSerializer;

    /** Room service dependency */
    private IRoomInventoryOrchestrator roomService;

    @Override
    public void init() throws ServletException {
        gsonSerializer = new Gson();
        roomService = new RoomInventoryOrchestratorImpl();
    }

    /**
     * Handles GET requests and returns JSON response.
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter jsonWriter = response.getWriter();

        String action = request.getParameter("action");
        String roomType = request.getParameter("type");

        try {
            if ("counts".equals(action)) {
                // Return availability counts per room type
                JsonObject countsResponse = new JsonObject();
                countsResponse.addProperty("status", "success");

                JsonObject countsData = new JsonObject();
                countsData.addProperty("Standard",
                        roomService.getAvailableCount("Standard"));
                countsData.addProperty("Superior",
                        roomService.getAvailableCount("Superior"));
                countsData.addProperty("Premium",
                        roomService.getAvailableCount("Premium"));
                countsData.addProperty("Executive",
                        roomService.getAvailableCount("Executive"));
                countsData.addProperty("Total",
                        roomService.getAvailableCount("ALL"));

                countsResponse.add("data", countsData);
                jsonWriter.print(gsonSerializer.toJson(countsResponse));

            } else if (roomType != null && !roomType.isEmpty()) {
                // Return available rooms of specific type
                List<ResortRoom> availableRooms =
                        roomService.getAvailableRoomsByType(roomType);

                JsonObject typeResponse = new JsonObject();
                typeResponse.addProperty("status", "success");
                typeResponse.addProperty("roomType", roomType);
                typeResponse.addProperty("count",
                        availableRooms.size());

                JsonArray roomsArray = new JsonArray();
                for (ResortRoom room : availableRooms) {
                    JsonObject roomObj = new JsonObject();
                    roomObj.addProperty("roomId", room.getRoomId());
                    roomObj.addProperty("roomNumber",
                            room.getRoomNumber());
                    roomObj.addProperty("roomType",
                            room.getRoomType());
                    roomObj.addProperty("ratePerNight",
                            room.getRatePerNight());
                    roomObj.addProperty("floorNumber",
                            room.getFloorNumber());
                    roomObj.addProperty("maxGuests",
                            room.getMaxGuests());
                    roomObj.addProperty("description",
                            room.getRoomDescription());
                    roomsArray.add(roomObj);
                }

                typeResponse.add("rooms", roomsArray);
                jsonWriter.print(gsonSerializer.toJson(typeResponse));

            } else {
                // Return all rooms
                List<ResortRoom> allRooms = roomService.getAllRooms();

                JsonObject allResponse = new JsonObject();
                allResponse.addProperty("status", "success");
                allResponse.addProperty("totalCount",
                        allRooms.size());

                JsonArray allArray = new JsonArray();
                for (ResortRoom room : allRooms) {
                    JsonObject roomObj = new JsonObject();
                    roomObj.addProperty("roomId", room.getRoomId());
                    roomObj.addProperty("roomNumber",
                            room.getRoomNumber());
                    roomObj.addProperty("roomType",
                            room.getRoomType());
                    roomObj.addProperty("ratePerNight",
                            room.getRatePerNight());
                    roomObj.addProperty("isAvailable",
                            room.getIsAvailable());
                    roomObj.addProperty("floorNumber",
                            room.getFloorNumber());
                    allArray.add(roomObj);
                }

                allResponse.add("rooms", allArray);
                jsonWriter.print(gsonSerializer.toJson(allResponse));
            }

            API_LOGGER.fine("Room API request processed successfully");

        } catch (Exception apiException) {
            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("status", "error");
            errorResponse.addProperty("message",
                    "Failed to retrieve room data");
            jsonWriter.print(gsonSerializer.toJson(errorResponse));

            API_LOGGER.severe("Room API error: "
                    + apiException.getMessage());
        }

        jsonWriter.flush();
    }
}