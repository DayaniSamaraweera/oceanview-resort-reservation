package com.oceanview.dao;

import com.oceanview.model.ResortRoom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// DAO Implementation for ResortRoom database operations.//

public class ResortRoomGatewayImpl implements IResortRoomGateway {

   
    private static final Logger GATEWAY_LOGGER =
            Logger.getLogger(ResortRoomGatewayImpl.class.getName());

    
    private final DatabaseConnectionManager dbManager =
            DatabaseConnectionManager.getInstance();


    private ResortRoom mapResultSetToRoom(ResultSet resultRow) throws SQLException {
        ResortRoom mappedRoom = new ResortRoom();
        mappedRoom.setRoomId(resultRow.getInt("room_id"));
        mappedRoom.setRoomNumber(resultRow.getString("room_number"));
        mappedRoom.setRoomType(resultRow.getString("room_type"));
        mappedRoom.setRatePerNight(resultRow.getDouble("rate_per_night"));
        mappedRoom.setIsAvailable(resultRow.getBoolean("is_available"));
        mappedRoom.setFloorNumber(resultRow.getInt("floor_number"));
        mappedRoom.setMaxGuests(resultRow.getInt("max_guests"));
        mappedRoom.setRoomDescription(resultRow.getString("room_description"));

        if (resultRow.getTimestamp("created_at") != null) {
            mappedRoom.setCreatedAt(
                    resultRow.getTimestamp("created_at").toLocalDateTime());
        }

        return mappedRoom;
    }

    /** {@inheritDoc} */
    @Override
    public List<ResortRoom> findAllRooms() {

        String allRoomsQuery = "SELECT * FROM rooms ORDER BY room_number";
        List<ResortRoom> allRooms = new ArrayList<>();
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement roomStatement = dbConnection.prepareStatement(allRoomsQuery);
            ResultSet roomResult = roomStatement.executeQuery();

            while (roomResult.next()) {
                allRooms.add(mapResultSetToRoom(roomResult));
            }

            GATEWAY_LOGGER.info("Retrieved " + allRooms.size() + " total rooms");

        } catch (SQLException queryException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error retrieving all rooms", queryException);
        } finally {
            dbManager.closeConnection(dbConnection);
        }

        return allRooms;
    }

    /** {@inheritDoc} */
    @Override
    public ResortRoom findRoomById(int roomId) {

        String findQuery = "SELECT * FROM rooms WHERE room_id = ?";
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement findStatement = dbConnection.prepareStatement(findQuery);
            findStatement.setInt(1, roomId);

            ResultSet findResult = findStatement.executeQuery();

            if (findResult.next()) {
                return mapResultSetToRoom(findResult);
            }
            return null;

        } catch (SQLException findException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error finding room ID: " + roomId, findException);
            return null;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<ResortRoom> findAvailableRoomsByType(String roomType) {

        String availableQuery = "SELECT * FROM rooms "
                + "WHERE room_type = ? AND is_available = 1 ORDER BY room_number";

        List<ResortRoom> availableRooms = new ArrayList<>();
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement availStatement = dbConnection.prepareStatement(availableQuery);
            availStatement.setString(1, roomType);

            ResultSet availResult = availStatement.executeQuery();

            while (availResult.next()) {
                availableRooms.add(mapResultSetToRoom(availResult));
            }

            GATEWAY_LOGGER.info("Found " + availableRooms.size()
                    + " available " + roomType + " rooms");

        } catch (SQLException availException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error finding available rooms for type: " + roomType, availException);
        } finally {
            dbManager.closeConnection(dbConnection);
        }

        return availableRooms;
    }

    /** {@inheritDoc} */
    @Override
    public List<ResortRoom> findAllAvailableRooms() {

        String allAvailableQuery = "SELECT * FROM rooms "
                + "WHERE is_available = 1 ORDER BY room_number";

        List<ResortRoom> availableRooms = new ArrayList<>();
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement statement = dbConnection.prepareStatement(allAvailableQuery);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                availableRooms.add(mapResultSetToRoom(result));
            }

        } catch (SQLException queryException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error retrieving all available rooms", queryException);
        } finally {
            dbManager.closeConnection(dbConnection);
        }

        return availableRooms;
    }

    /**
     * {@inheritDoc}
     * Calls the MySQL function GetAvailableRoomCount directly.
     */
    @Override
    public int getAvailableRoomCount(String roomType) {

        String countQuery = "SELECT GetAvailableRoomCount(?) AS available_count";
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement countStatement = dbConnection.prepareStatement(countQuery);
            countStatement.setString(1, roomType);

            ResultSet countResult = countStatement.executeQuery();

            if (countResult.next()) {
                return countResult.getInt("available_count");
            }
            return 0;

        } catch (SQLException countException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error getting available room count for: " + roomType, countException);
            return 0;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateRoomAvailability(int roomId, boolean isAvailable) {

        String updateQuery = "UPDATE rooms SET is_available = ? WHERE room_id = ?";
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement updateStatement = dbConnection.prepareStatement(updateQuery);
            updateStatement.setBoolean(1, isAvailable);
            updateStatement.setInt(2, roomId);

            int rowsUpdated = updateStatement.executeUpdate();

            if (rowsUpdated > 0) {
                GATEWAY_LOGGER.info("Room " + roomId
                        + " availability updated to: " + isAvailable);
                return true;
            }
            return false;

        } catch (SQLException updateException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error updating room availability - ID: " + roomId, updateException);
            return false;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }
}