package com.oceanview.dao;

import com.oceanview.model.GuestReservation;
import com.oceanview.model.InvoiceRecord;

import java.sql.CallableStatement;
import java.sql.Connection;
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
 * DAO Implementation for InvoiceRecord (Bill) database operations.
 *
 * <p><b>Design Pattern:</b> DAO Pattern - Encapsulates all database
 * access logic for the bills table. Uses the MySQL stored procedure
 * CalculateBill to compute billing amounts server-side.</p>
 *
 * <p><b>Requirement Traceability:</b> Directly maps to the
 * "Calculate and Print Bill" feature - computes total stay cost
 * based on number of nights and room rates using the CalculateBill
 * stored procedure.</p>
 *
 * <p><b>Assumption:</b> Bill data is denormalized (guest name,
 * room type stored directly) to preserve an immutable snapshot
 * of billing information at the time of invoice generation.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public class InvoiceRecordGatewayImpl implements IInvoiceRecordGateway {

    /** Logger for invoice gateway operations */
    private static final Logger GATEWAY_LOGGER =
            Logger.getLogger(InvoiceRecordGatewayImpl.class.getName());

    /** Singleton database connection manager instance */
    private final DatabaseConnectionManager dbManager =
            DatabaseConnectionManager.getInstance();

    /**
     * Maps a ResultSet row to an InvoiceRecord object.
     *
     * @param resultRow the current ResultSet row
     * @return a populated InvoiceRecord object
     * @throws SQLException if a column access error occurs
     */
    private InvoiceRecord mapResultSetToBill(ResultSet resultRow)
            throws SQLException {

        InvoiceRecord mappedBill = new InvoiceRecord();
        mappedBill.setBillId(resultRow.getInt("bill_id"));
        mappedBill.setBillNumber(resultRow.getString("bill_number"));
        mappedBill.setReservationId(resultRow.getInt("reservation_id"));
        mappedBill.setReservationNumber(
                resultRow.getString("reservation_number"));
        mappedBill.setGuestName(resultRow.getString("guest_name"));
        mappedBill.setRoomType(resultRow.getString("room_type"));
        mappedBill.setRatePerNight(resultRow.getDouble("rate_per_night"));
        mappedBill.setNumberOfNights(resultRow.getInt("number_of_nights"));
        mappedBill.setSubtotal(resultRow.getDouble("subtotal"));
        mappedBill.setTaxPercentage(resultRow.getDouble("tax_percentage"));
        mappedBill.setTaxAmount(resultRow.getDouble("tax_amount"));
        mappedBill.setTotalAmount(resultRow.getDouble("total_amount"));
        mappedBill.setGeneratedBy(resultRow.getInt("generated_by"));

        if (resultRow.getTimestamp("generated_at") != null) {
            mappedBill.setGeneratedAt(
                    resultRow.getTimestamp("generated_at").toLocalDateTime());
        }

        // Handle JOIN display field
        try {
            mappedBill.setGeneratedByName(
                    resultRow.getString("generated_by_name"));
        } catch (SQLException nameNotFound) {
            // JOIN field not present in all queries
        }

        return mappedBill;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This method performs a multi-step operation:
     * 1. Calls CalculateBill stored procedure to compute amounts
     * 2. Retrieves reservation details for denormalized storage
     * 3. Inserts the complete bill record into the bills table
     * 4. Returns the fully populated InvoiceRecord</p>
     */
    @Override
    public InvoiceRecord generateAndStoreBill(int reservationId,
                                              int generatedBy) {

        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();

            // ---- Step 1: Call CalculateBill stored procedure ----
            String callCalculate = "{CALL CalculateBill(?, ?, ?, ?, ?)}";
            CallableStatement calcStatement =
                    dbConnection.prepareCall(callCalculate);

            calcStatement.setInt(1, reservationId);
            calcStatement.registerOutParameter(2, Types.VARCHAR);   // bill_number
            calcStatement.registerOutParameter(3, Types.DECIMAL);   // subtotal
            calcStatement.registerOutParameter(4, Types.DECIMAL);   // tax_amount
            calcStatement.registerOutParameter(5, Types.DECIMAL);   // total_amount

            calcStatement.execute();

            String billNumber = calcStatement.getString(2);
            double subtotal = calcStatement.getDouble(3);
            double taxAmount = calcStatement.getDouble(4);
            double totalAmount = calcStatement.getDouble(5);

            GATEWAY_LOGGER.info("Bill calculated - Number: " + billNumber
                    + ", Total: LKR " + totalAmount);

            // ---- Step 2: Get reservation details for bill record ----
            String reservationQuery = "SELECT res.reservation_number, "
                    + "res.guest_name, res.room_type, res.number_of_nights, "
                    + "rm.rate_per_night "
                    + "FROM reservations res "
                    + "INNER JOIN rooms rm ON res.room_id = rm.room_id "
                    + "WHERE res.reservation_id = ?";

            PreparedStatement resStatement =
                    dbConnection.prepareStatement(reservationQuery);
            resStatement.setInt(1, reservationId);
            ResultSet resResult = resStatement.executeQuery();

            if (!resResult.next()) {
                GATEWAY_LOGGER.severe(
                        "Reservation not found for bill: " + reservationId);
                return null;
            }

            String reservationNumber = resResult.getString("reservation_number");
            String guestName = resResult.getString("guest_name");
            String roomType = resResult.getString("room_type");
            int numberOfNights = resResult.getInt("number_of_nights");
            double ratePerNight = resResult.getDouble("rate_per_night");

            // ---- Step 3: Insert bill record ----
            String insertBillQuery = "INSERT INTO bills "
                    + "(bill_number, reservation_id, reservation_number, "
                    + "guest_name, room_type, rate_per_night, number_of_nights, "
                    + "subtotal, tax_percentage, tax_amount, total_amount, "
                    + "generated_by) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement insertStatement = dbConnection.prepareStatement(
                    insertBillQuery, Statement.RETURN_GENERATED_KEYS);

            insertStatement.setString(1, billNumber);
            insertStatement.setInt(2, reservationId);
            insertStatement.setString(3, reservationNumber);
            insertStatement.setString(4, guestName);
            insertStatement.setString(5, roomType);
            insertStatement.setDouble(6, ratePerNight);
            insertStatement.setInt(7, numberOfNights);
            insertStatement.setDouble(8, subtotal);
            insertStatement.setDouble(9, 0.00);    // tax_percentage
            insertStatement.setDouble(10, taxAmount);
            insertStatement.setDouble(11, totalAmount);
            insertStatement.setInt(12, generatedBy);

            int rowsInserted = insertStatement.executeUpdate();

            if (rowsInserted > 0) {
                // ---- Step 4: Build and return the complete InvoiceRecord ----
                ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                int billId = 0;
                if (generatedKeys.next()) {
                    billId = generatedKeys.getInt(1);
                }

                InvoiceRecord generatedBill = new InvoiceRecord.Builder()
                        .billId(billId)
                        .billNumber(billNumber)
                        .reservationId(reservationId)
                        .reservationNumber(reservationNumber)
                        .guestName(guestName)
                        .roomType(roomType)
                        .ratePerNight(ratePerNight)
                        .numberOfNights(numberOfNights)
                        .subtotal(subtotal)
                        .taxPercentage(0.00)
                        .taxAmount(taxAmount)
                        .totalAmount(totalAmount)
                        .generatedBy(generatedBy)
                        .build();

                GATEWAY_LOGGER.info("Bill stored successfully - ID: "
                        + billId + ", Number: " + billNumber);

                return generatedBill;
            }

            return null;

        } catch (SQLException billException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error generating bill for reservation: "
                            + reservationId, billException);
            return null;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public InvoiceRecord findBillByNumber(String billNumber) {

        String findQuery = "SELECT b.*, u.full_name AS generated_by_name "
                + "FROM bills b "
                + "LEFT JOIN users u ON b.generated_by = u.user_id "
                + "WHERE b.bill_number = ?";

        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement findStatement =
                    dbConnection.prepareStatement(findQuery);
            findStatement.setString(1, billNumber);

            ResultSet findResult = findStatement.executeQuery();

            if (findResult.next()) {
                GATEWAY_LOGGER.info("Bill found: " + billNumber);
                return mapResultSetToBill(findResult);
            }
            return null;

        } catch (SQLException findException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error finding bill: " + billNumber, findException);
            return null;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public InvoiceRecord findBillByReservationId(int reservationId) {

        String findQuery = "SELECT b.*, u.full_name AS generated_by_name "
                + "FROM bills b "
                + "LEFT JOIN users u ON b.generated_by = u.user_id "
                + "WHERE b.reservation_id = ?";

        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement findStatement =
                    dbConnection.prepareStatement(findQuery);
            findStatement.setInt(1, reservationId);

            ResultSet findResult = findStatement.executeQuery();

            if (findResult.next()) {
                return mapResultSetToBill(findResult);
            }
            return null;

        } catch (SQLException findException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error finding bill for reservation: " + reservationId,
                    findException);
            return null;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<InvoiceRecord> findAllBills() {

        String allQuery = "SELECT b.*, u.full_name AS generated_by_name "
                + "FROM bills b "
                + "LEFT JOIN users u ON b.generated_by = u.user_id "
                + "ORDER BY b.generated_at DESC";

        List<InvoiceRecord> allBills = new ArrayList<>();
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement allStatement =
                    dbConnection.prepareStatement(allQuery);
            ResultSet allResult = allStatement.executeQuery();

            while (allResult.next()) {
                allBills.add(mapResultSetToBill(allResult));
            }

            GATEWAY_LOGGER.info("Retrieved " + allBills.size() + " bills");

        } catch (SQLException allException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error retrieving all bills", allException);
        } finally {
            dbManager.closeConnection(dbConnection);
        }

        return allBills;
    }

    /** {@inheritDoc} */
    @Override
    public double getTotalRevenue() {

        String revenueQuery = "SELECT COALESCE(SUM(total_amount), 0) "
                + "AS total_revenue FROM bills";

        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement revenueStatement =
                    dbConnection.prepareStatement(revenueQuery);
            ResultSet revenueResult = revenueStatement.executeQuery();

            if (revenueResult.next()) {
                return revenueResult.getDouble("total_revenue");
            }
            return 0.0;

        } catch (SQLException revenueException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error calculating total revenue", revenueException);
            return 0.0;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getBillCount() {

        String countQuery = "SELECT COUNT(*) AS bill_count FROM bills";
        Connection dbConnection = null;

        try {
            dbConnection = dbManager.openConnection();
            PreparedStatement countStatement =
                    dbConnection.prepareStatement(countQuery);
            ResultSet countResult = countStatement.executeQuery();

            if (countResult.next()) {
                return countResult.getInt("bill_count");
            }
            return 0;

        } catch (SQLException countException) {
            GATEWAY_LOGGER.log(Level.SEVERE,
                    "Error counting bills", countException);
            return 0;
        } finally {
            dbManager.closeConnection(dbConnection);
        }
    }
}