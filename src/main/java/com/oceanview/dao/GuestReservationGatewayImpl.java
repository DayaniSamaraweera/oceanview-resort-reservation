package com.oceanview.dao;

import com.oceanview.model.GuestReservation;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO Implementation for GuestReservation database operations.
 *
 * <p><b>Design Pattern:</b> DAO Pattern - Encapsulates all database
 * access logic for the reservations table. Uses MySQL stored
 * procedures (GenerateReservationNumber, GetReservationDetails)
 * for complex operations.</p>
 *
 * <p><b>Requirement Traceability:</b>
 * - generateReservationNumber() → "Add New Reservation" (auto-generate unique ID)
 * - insertReservation() → "Add New Reservation" (store booking details)
 * - findReservationByNumber() → "Display Reservation Details"
 * - findAllReservations() → Reservation listing and management
 * - updateReservationStatus() → Status management and cancellation</p>
 *
 * <p><b>Security:</b> All queries use PreparedStatement to prevent
 * SQL injection attacks.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public class GuestReservationGatewayImpl implements IGuestReservationGateway {

    /** Logger for reservation gateway operations */
    private static final Logger GATEWAY_LOGGER =
            Logger.getLogger(GuestReservationGatewayImpl.class.getName());

    /** Singleton database connection manager instance */
    private final DatabaseConnectionManager dbManager =
            DatabaseConnectionManager.getInstance();

    /**
     * Maps a ResultSet row to a GuestReservation object.
     * Handles both basic reservation data and JOIN-populated
     * display fields (room_number, rate_per_night, created_by_name).
     *
     * @param resultRow the current ResultSet row
     * @return a populated GuestReservation object
     * @throws SQLException if a column access error occurs
     */
    private GuestReservation mapResultSetToReservation(ResultSet resultRow)
            throws SQLException {

        GuestReservation mappedReservation = new GuestReservation();

        mappedReservation.setReservationId(
                resultRow.getInt("reservation_id"));
        mappedReservation.setReservationNumber(
                resultRow.getString("reservation_number"));
        mappedReservation.setGuestName(
                resultRow.getString("guest_name"));
        mappedReservation.setAddress(
                resultRow.getString("address"));
        mappedReservation.setContactNumber(
                resultRow.getString("contact_number"));
        mappedReservation.setRoomType(
                resultRow.getString("room_type"));
        mappedReservation.setReservationStatus(
                resultRow.getString("reservation_status"));
        mappedReservation.setNumberOfNights(
                resultRow.getInt("number_of_nights"));

        // Handle nullable guest_email column
        try {
            mappedReservation.setGuestEmail(
                    resultRow.getString("guest_email"));
        } catch (SQLException emailNotFound) {
            // Column may not exist in all queries
        }

        // Handle room_id - may be in base table or JOIN result
        try {
            mappedReservation.setRoomId(
                    resultRow.getInt("room_id"));
        } catch (SQLException roomIdNotFound) {
            // Column may not exist in stored procedure results
        }

        // Handle cancel_reason for cancelled reservations
        try {
            mappedReservation.setCancelReason(
                    resultRow.getString("cancel_reason"));
        } catch (SQLException cancelNotFound) {
            // Column may not exist in all queries
        }

        // Handle created_by foreign key
        try {
            mappedReservation.setCreatedBy(
                    resultRow.getInt("created_by"));
        } catch (SQLException creatorNotFound) {
            // Column may not exist in stored procedure results
        }

        // Parse date columns safely
        Date checkInSql = resultRow.getDate("check_in_date");
        if (checkInSql != null) {
            mappedReservation.setCheckInDate(checkInSql.toLocalDate());
        }

        Date checkOutSql = resultRow.getDate("check_out_date");
        if (checkOutSql != null) {
            mappedReservation.setCheckOutDate(checkOutSql.toLocalDate());
        }

        // Parse timestamp columns safely
        if (resultRow.getTimestamp("created_at") != null) {
            mappedReservation.setCreatedAt(
                    resultRow.getTimestamp("created_at").toLocalDateTime());
        }

        // Handle JOIN display fields (may not exist in all queries)
        try {
            mappedReservation.setRoomNumber(
                    resultRow.getString("room_number"));
        } catch (SQLException roomNumNotFound) {
            // JOIN field not present
        }

        try {
            mappedReservation.setRatePerNight(
                    resultRow.getDouble("rate_per_night"));
        } catch (SQLException rateNotFound) {
            // JOIN field not present
        }

        try {
            mappedReservation.setCreatedByName(
                    resultRow.getString("created_by_name"));
        } catch (SQLException nameNotFound) {
            // JOIN field not present
        }

        return mappedReservation;
    }

    /**
     * {@inheritDoc}
     * Calls the MySQL stored procedure GenerateReservationNumber
     * to produce a unique reservation number in format RES-YYYY-NNNNN.
     */
    @Override
    public String generateReservationNumber() {

        String callProcedure = "{CALL GenerateReservationNumber(?)}";
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            CallableStatement callStatement =
                    dbConnection.prepareCall(callProcedure);

            // Register the OUT parameter for the generated number
            callStatement.registerOutParameter(1, Types.VARCHAR);
            callStatement.execute();

            String generatedNumber = callStatement.getString(1);
            GATEWAY_LOGGER.info(
                    "Generated reservation number: " + generatedNumber);
            return generatedNumber;

        } catch (SQLException generateException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error generating reservation number", generateException);
            return null;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /**
     * {@inheritDoc}
     * Inserts a new reservation record. The database trigger
     * before_reservation_insert automatically validates dates,
     * calculates number_of_nights, and checks for booking conflicts.
     */
    @Override
    public int insertReservation(GuestReservation reservation) {

        String insertQuery = "INSERT INTO reservations "
                + "(reservation_number, guest_name, address, contact_number, "
                + "guest_email, room_id, room_type, check_in_date, "
                + "check_out_date, created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement insertStatement = dbConnection.prepareStatement(
                    insertQuery, Statement.RETURN_GENERATED_KEYS);

            insertStatement.setString(1, reservation.getReservationNumber());
            insertStatement.setString(2, reservation.getGuestName());
            insertStatement.setString(3, reservation.getAddress());
            insertStatement.setString(4, reservation.getContactNumber());
            insertStatement.setString(5, reservation.getGuestEmail());
            insertStatement.setInt(6, reservation.getRoomId());
            insertStatement.setString(7, reservation.getRoomType());
            insertStatement.setDate(8,
                    Date.valueOf(reservation.getCheckInDate()));
            insertStatement.setDate(9,
                    Date.valueOf(reservation.getCheckOutDate()));
            insertStatement.setInt(10, reservation.getCreatedBy());

            int rowsInserted = insertStatement.executeUpdate();

            if (rowsInserted > 0) {
                // Retrieve the auto-generated reservation_id
                ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newReservationId = generatedKeys.getInt(1);
                    GATEWAY_LOGGER.info("Reservation inserted - ID: "
                            + newReservationId + ", Number: "
                            + reservation.getReservationNumber());
                    return newReservationId;
                }
            }
            return -1;

        } catch (SQLException insertException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error inserting reservation: "
                            + reservation.getReservationNumber(),
                    insertException);
            return -1;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /**
     * {@inheritDoc}
     * Calls the MySQL stored procedure GetReservationDetails
     * which performs a JOIN between reservations, rooms, and users
     * tables to return complete booking information.
     */
    @Override
    public GuestReservation findReservationByNumber(String reservationNumber) {

        String callProcedure = "{CALL GetReservationDetails(?)}";
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            CallableStatement callStatement =
                    dbConnection.prepareCall(callProcedure);
            callStatement.setString(1, reservationNumber);

            ResultSet detailResult = callStatement.executeQuery();

            if (detailResult.next()) {
                GATEWAY_LOGGER.info(
                        "Reservation found: " + reservationNumber);
                return mapResultSetToReservation(detailResult);
            } else {
                GATEWAY_LOGGER.warning(
                        "Reservation not found: " + reservationNumber);
                return null;
            }

        } catch (SQLException findException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error finding reservation: " + reservationNumber,
                    findException);
            return null;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public GuestReservation findReservationById(int reservationId) {

        String findQuery = "SELECT res.*, rm.room_number, rm.rate_per_night, "
                + "u.full_name AS created_by_name "
                + "FROM reservations res "
                + "INNER JOIN rooms rm ON res.room_id = rm.room_id "
                + "LEFT JOIN users u ON res.created_by = u.user_id "
                + "WHERE res.reservation_id = ?";

        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement findStatement =
                    dbConnection.prepareStatement(findQuery);
            findStatement.setInt(1, reservationId);

            ResultSet findResult = findStatement.executeQuery();

            if (findResult.next()) {
                return mapResultSetToReservation(findResult);
            }
            return null;

        } catch (SQLException findException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error finding reservation ID: " + reservationId,
                    findException);
            return null;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<GuestReservation> findAllReservations() {

        String allQuery = "SELECT res.*, rm.room_number, rm.rate_per_night, "
                + "u.full_name AS created_by_name "
                + "FROM reservations res "
                + "INNER JOIN rooms rm ON res.room_id = rm.room_id "
                + "LEFT JOIN users u ON res.created_by = u.user_id "
                + "ORDER BY res.created_at DESC";

        List<GuestReservation> allReservations = new ArrayList<>();
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement allStatement =
                    dbConnection.prepareStatement(allQuery);
            ResultSet allResult = allStatement.executeQuery();

            while (allResult.next()) {
                allReservations.add(mapResultSetToReservation(allResult));
            }

            GATEWAY_LOGGER.info("Retrieved "
                    + allReservations.size() + " reservations");

        } catch (SQLException allException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error retrieving all reservations", allException);
        } finally {
            dbManager.closeConnection(dbConnection);
        }

        return allReservations;
    }

    /** {@inheritDoc} */
    @Override
    public List<GuestReservation> findReservationsByStatus(String status) {

        String statusQuery = "SELECT res.*, rm.room_number, rm.rate_per_night, "
                + "u.full_name AS created_by_name "
                + "FROM reservations res "
                + "INNER JOIN rooms rm ON res.room_id = rm.room_id "
                + "LEFT JOIN users u ON res.created_by = u.user_id "
                + "WHERE res.reservation_status = ? "
                + "ORDER BY res.created_at DESC";

        List<GuestReservation> filteredReservations = new ArrayList<>();
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement statusStatement =
                    dbConnection.prepareStatement(statusQuery);
            statusStatement.setString(1, status);

            ResultSet statusResult = statusStatement.executeQuery();

            while (statusResult.next()) {
                filteredReservations.add(
                        mapResultSetToReservation(statusResult));
            }

            GATEWAY_LOGGER.info("Found " + filteredReservations.size()
                    + " reservations with status: " + status);

        } catch (SQLException statusException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error filtering reservations by status: " + status,
                    statusException);
        } finally {
            dbManager.closeConnection(dbConnection);
        }

        return filteredReservations;
    }

    /**
     * {@inheritDoc}
     * Updates reservation status. The database trigger
     * after_reservation_update automatically handles room
     * availability changes and audit trail logging.
     */
    @Override
    public boolean updateReservationStatus(int reservationId,
                                           String newStatus,
                                           String cancelReason) {

        String updateQuery = "UPDATE reservations SET "
                + "reservation_status = ?, cancel_reason = ? "
                + "WHERE reservation_id = ?";

        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement updateStatement =
                    dbConnection.prepareStatement(updateQuery);
            updateStatement.setString(1, newStatus);
            updateStatement.setString(2, cancelReason);
            updateStatement.setInt(3, reservationId);

            int rowsUpdated = updateStatement.executeUpdate();

            if (rowsUpdated > 0) {
                GATEWAY_LOGGER.info("Reservation " + reservationId
                        + " status changed to: " + newStatus);
                return true;
            }
            return false;

        } catch (SQLException updateException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error updating reservation status - ID: "
                            + reservationId, updateException);
            return false;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<GuestReservation> findRecentReservations(int limit) {

        String recentQuery = "SELECT res.*, rm.room_number, rm.rate_per_night, "
                + "u.full_name AS created_by_name "
                + "FROM reservations res "
                + "INNER JOIN rooms rm ON res.room_id = rm.room_id "
                + "LEFT JOIN users u ON res.created_by = u.user_id "
                + "ORDER BY res.created_at DESC LIMIT ?";

        List<GuestReservation> recentReservations = new ArrayList<>();
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement recentStatement =
                    dbConnection.prepareStatement(recentQuery);
            recentStatement.setInt(1, limit);

            ResultSet recentResult = recentStatement.executeQuery();

            while (recentResult.next()) {
                recentReservations.add(
                        mapResultSetToReservation(recentResult));
            }

        } catch (SQLException recentException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error retrieving recent reservations", recentException);
        } finally {
            dbManager.closeConnection(dbConnection);
        }

        return recentReservations;
    }

    /** {@inheritDoc} */
    @Override
    public int getReservationCountByStatus(String status) {

        String countQuery = "SELECT COUNT(*) AS status_count "
                + "FROM reservations WHERE reservation_status = ?";

        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement countStatement =
                    dbConnection.prepareStatement(countQuery);
            countStatement.setString(1, status);

            ResultSet countResult = countStatement.executeQuery();

            if (countResult.next()) {
                return countResult.getInt("status_count");
            }
            return 0;

        } catch (SQLException countException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error counting reservations by status: " + status,
                    countException);
            return 0;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<GuestReservation> findReservationsByCreator(int userId) {

        String creatorQuery = "SELECT res.*, rm.room_number, rm.rate_per_night, "
                + "u.full_name AS created_by_name "
                + "FROM reservations res "
                + "INNER JOIN rooms rm ON res.room_id = rm.room_id "
                + "LEFT JOIN users u ON res.created_by = u.user_id "
                + "WHERE res.created_by = ? "
                + "ORDER BY res.created_at DESC";

        List<GuestReservation> creatorReservations = new ArrayList<>();
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement creatorStatement =
                    dbConnection.prepareStatement(creatorQuery);
            creatorStatement.setInt(1, userId);

            ResultSet creatorResult = creatorStatement.executeQuery();

            while (creatorResult.next()) {
                creatorReservations.add(
                        mapResultSetToReservation(creatorResult));
            }

        } catch (SQLException creatorException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error retrieving reservations for user: " + userId,
                    creatorException);
        } finally {
            dbManager.closeConnection(dbConnection);
        }

        return creatorReservations;
    }
}