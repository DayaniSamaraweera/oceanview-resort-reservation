package com.oceanview.dao;

import com.oceanview.model.InvoiceRecord;
import java.util.List;

/**
 * DAO Interface for InvoiceRecord (Bill) operations.
 *
 * <p><b>Requirement Traceability:</b> Supports "Calculate and
 * Print Bill" feature - computes total stay cost based on
 * number of nights and room rates.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public interface IInvoiceRecordGateway {

    /**
     * Generates a bill using the CalculateBill stored procedure
     * and stores it in the bills table.
     *
     * @param reservationId the reservation to bill
     * @param generatedBy the user ID generating the bill
     * @return the created InvoiceRecord, or null on failure
     */
    InvoiceRecord generateAndStoreBill(int reservationId, int generatedBy);

    /**
     * Finds a bill by its unique bill number.
     *
     * @param billNumber the bill number to search
     * @return the InvoiceRecord, or null if not found
     */
    InvoiceRecord findBillByNumber(String billNumber);

    /**
     * Finds a bill linked to a specific reservation.
     *
     * @param reservationId the reservation ID to search
     * @return the InvoiceRecord, or null if not found
     */
    InvoiceRecord findBillByReservationId(int reservationId);

    /**
     * Retrieves all generated bills.
     *
     * @return list of all InvoiceRecord objects
     */
    List<InvoiceRecord> findAllBills();

    /**
     * Calculates the total revenue from all bills.
     *
     * @return the sum of all total_amount values
     */
    double getTotalRevenue();

    /**
     * Gets the total number of bills generated.
     *
     * @return the bill count
     */
    int getBillCount();
}