package com.oceanview.dao;

import com.oceanview.model.ResortRoom;
import java.util.List;

/**
 * DAO Interface for ResortRoom operations.
 *
 * <p><b>Requirement Traceability:</b> Supports room selection
 * during "Add New Reservation" and room availability tracking
 * for management decision-making reports.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public interface IResortRoomGateway {

    /**
     * Retrieves all rooms in the resort.
     *
     * @return list of all ResortRoom objects
     */
    List<ResortRoom> findAllRooms();

    /**
     * Finds a specific room by its database ID.
     *
     * @param roomId the room ID to search for
     * @return the ResortRoom, or null if not found
     */
    ResortRoom findRoomById(int roomId);

    /**
     * Finds all available rooms of a specific type.
     *
     * @param roomType the room type to filter by
     * @return list of available rooms of that type
     */
    List<ResortRoom> findAvailableRoomsByType(String roomType);

    /**
     * Retrieves all currently available rooms.
     *
     * @return list of all available ResortRoom objects
     */
    List<ResortRoom> findAllAvailableRooms();

    /**
     * Gets the count of available rooms by type using the
     * MySQL function GetAvailableRoomCount.
     *
     * @param roomType the room type, or "ALL" for total count
     * @return the number of available rooms
     */
    int getAvailableRoomCount(String roomType);

    /**
     * Updates a room's availability status.
     *
     * @param roomId the room ID to update
     * @param isAvailable the new availability status
     * @return true if update was successful
     */
    boolean updateRoomAvailability(int roomId, boolean isAvailable);
}