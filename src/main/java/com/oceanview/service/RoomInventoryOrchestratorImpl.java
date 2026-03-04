package com.oceanview.service;

import com.oceanview.dao.IResortRoomGateway;
import com.oceanview.dao.ResortRoomGatewayImpl;
import com.oceanview.model.ResortRoom;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service Implementation for room inventory management.
 *
 * <p><b>Architecture:</b> Business Logic Layer - provides room
 * lookup, availability checking, and rate retrieval for the
 * reservation booking workflow.</p>
 *
 * <p><b>Assumption:</b> Room rates are fixed per type:
 * Standard=5500, Superior=8500, Premium=13000, Executive=22000.
 * If the database is unreachable, the hardcoded fallback rates
 * are returned to maintain system availability.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public class RoomInventoryOrchestratorImpl
        implements IRoomInventoryOrchestrator {

    /** Logger for room inventory events */
    private static final Logger ROOM_LOGGER =
            Logger.getLogger(RoomInventoryOrchestratorImpl.class.getName());

    /** DAO dependency for room database operations */
    private final IResortRoomGateway roomGateway;

    /**
     * Default constructor using concrete DAO implementation.
     */
    public RoomInventoryOrchestratorImpl() {
        this.roomGateway = new ResortRoomGatewayImpl();
    }

    /**
     * Constructor with injected DAO for Mockito testing.
     *
     * @param roomGateway the DAO implementation (or mock)
     */
    public RoomInventoryOrchestratorImpl(IResortRoomGateway roomGateway) {
        this.roomGateway = roomGateway;
    }

    /** {@inheritDoc} */
    @Override
    public List<ResortRoom> getAllRooms() {
        return roomGateway.findAllRooms();
    }

    /** {@inheritDoc} */
    @Override
    public ResortRoom getRoomById(int roomId) {
        if (roomId <= 0) {
            ROOM_LOGGER.warning("Invalid room ID requested: " + roomId);
            return null;
        }
        return roomGateway.findRoomById(roomId);
    }

    /** {@inheritDoc} */
    @Override
    public List<ResortRoom> getAvailableRoomsByType(String roomType) {
        if (roomType == null || roomType.trim().isEmpty()) {
            ROOM_LOGGER.warning("Room type is null or empty");
            return List.of();
        }
        return roomGateway.findAvailableRoomsByType(roomType);
    }

    /** {@inheritDoc} */
    @Override
    public List<ResortRoom> getAllAvailableRooms() {
        return roomGateway.findAllAvailableRooms();
    }

    /** {@inheritDoc} */
    @Override
    public int getAvailableCount(String roomType) {
        return roomGateway.getAvailableRoomCount(
                (roomType == null || roomType.trim().isEmpty()) ? "ALL" : roomType);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Returns hardcoded fallback rates if the room type
     * is not found in the database. This ensures the system
     * remains functional even during database issues.</p>
     */
    @Override
    public double getRateForRoomType(String roomType) {
        if (roomType == null) {
            return 0.0;
        }

        // Attempt to get rate from database first
        List<ResortRoom> roomsOfType = roomGateway.findAvailableRoomsByType(roomType);
        if (!roomsOfType.isEmpty()) {
            return roomsOfType.get(0).getRatePerNight();
        }

        // Fallback to hardcoded rates matching Ocean View Resort pricing
        switch (roomType) {
            case "Standard":  return 5500.00;
            case "Superior":  return 8500.00;
            case "Premium":   return 13000.00;
            case "Executive": return 22000.00;
            default:
                ROOM_LOGGER.warning("Unknown room type: " + roomType);
                return 0.0;
        }
    }
}