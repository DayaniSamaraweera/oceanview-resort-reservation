package com.oceanview.service;

import com.oceanview.model.ResortRoom;
import java.util.List;

//Service Interface for room inventory management.

public interface IRoomInventoryOrchestrator {


    List<ResortRoom> getAllRooms();

    ResortRoom getRoomById(int roomId);

    List<ResortRoom> getAvailableRoomsByType(String roomType);

    List<ResortRoom> getAllAvailableRooms();

    int getAvailableCount(String roomType);

    double getRateForRoomType(String roomType);
}