package com.oceanview.dao;

import com.oceanview.model.ResortRoom;
import java.util.List;

/**
 * DAO interface for room operations.
 */
public interface IResortRoomGateway {

    List<ResortRoom> findAllRooms();

    ResortRoom findRoomById(int roomId);

    List<ResortRoom> findAvailableRoomsByType(String roomType);

    List<ResortRoom> findAllAvailableRooms();

    // calls GetAvailableRoomCount MySQL function, pass "ALL" for total count
    int getAvailableRoomCount(String roomType);

    boolean updateRoomAvailability(int roomId, boolean isAvailable);
}