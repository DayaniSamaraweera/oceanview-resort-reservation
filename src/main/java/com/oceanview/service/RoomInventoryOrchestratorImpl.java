package com.oceanview.service;

import com.oceanview.dao.IResortRoomGateway;
import com.oceanview.dao.ResortRoomGatewayImpl;
import com.oceanview.model.ResortRoom;

import java.util.List;
import java.util.logging.Logger;

//Service Implementation for room inventory management.

public class RoomInventoryOrchestratorImpl
        implements IRoomInventoryOrchestrator {

   
    private static final Logger ROOM_LOGGER =
            Logger.getLogger(RoomInventoryOrchestratorImpl.class.getName());

    private final IResortRoomGateway roomGateway;

    public RoomInventoryOrchestratorImpl() {
        this.roomGateway = new ResortRoomGatewayImpl();
    }

    public RoomInventoryOrchestratorImpl(IResortRoomGateway roomGateway) {
        this.roomGateway = roomGateway;
    }

    @Override
    public List<ResortRoom> getAllRooms() {
        return roomGateway.findAllRooms();
    }

    @Override
    public ResortRoom getRoomById(int roomId) {
        if (roomId <= 0) {
            ROOM_LOGGER.warning("Invalid room ID requested: " + roomId);
            return null;
        }
        return roomGateway.findRoomById(roomId);
    }

    @Override
    public List<ResortRoom> getAvailableRoomsByType(String roomType) {
        if (roomType == null || roomType.trim().isEmpty()) {
            ROOM_LOGGER.warning("Room type is null or empty");
            return List.of();
        }
        return roomGateway.findAvailableRoomsByType(roomType);
    }

   
    @Override
    public List<ResortRoom> getAllAvailableRooms() {
        return roomGateway.findAllAvailableRooms();
    }

   
    @Override
    public int getAvailableCount(String roomType) {
        return roomGateway.getAvailableRoomCount(
                (roomType == null || roomType.trim().isEmpty()) ? "ALL" : roomType);
    }

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