package com.oceanview.service;

import com.oceanview.model.ResortRoom;
import java.util.List;

/**
 * Service Interface for room inventory management.
 *
 * <p><b>Requirement Traceability:</b> Supports room selection
 * during "Add New Reservation" and availability tracking
 * for dashboard statistics and decision-making reports.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public interface IRoomInventoryOrchestrator {

    /**
     * Gets all rooms in the resort inventory.
     *
     * @return list of all ResortRoom objects
     */
    List<ResortRoom> getAllRooms();

    /**
     * Gets a specific room by its ID.
     *
     * @param roomId the room ID
     * @return the ResortRoom, or null if not found
     */
    ResortRoom getRoomById(int roomId);

    /**
     * Gets available rooms filtered by type.
     *
     * @param roomType the room type to filter
     * @return list of available ResortRoom objects
     */
    List<ResortRoom> getAvailableRoomsByType(String roomType);

    /**
     * Gets all currently available rooms.
     *
     * @return list of all available ResortRoom objects
     */
    List<ResortRoom> getAllAvailableRooms();

    /**
     * Gets the count of available rooms by type.
     *
     * @param roomType the room type, or "ALL"
     * @return the available room count
     */
    int getAvailableCount(String roomType);

    /**
     * Gets the rate per night for a specific room type.
     *
     * @param roomType the room type
     * @return the nightly rate in LKR
     */
    double getRateForRoomType(String roomType);
}