package com.oceanview.service;

import com.oceanview.model.InvoiceRecord;
import java.util.List;

/**
 * Service Interface for billing and invoice operations.
 *
 * <p><b>Requirement Traceability:</b> Maps to "Calculate and Print Bill"
 * feature - computes total stay cost based on number of nights
 * and room rates.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public interface IBillingOrchestrator {

    /**
     * Generates a bill for a reservation using the
     * CalculateBill stored procedure.
     *
     * @param reservationId the reservation to bill
     * @param generatedBy the user generating the bill
     * @return the created InvoiceRecord, or null on failure
     */
    InvoiceRecord generateBill(int reservationId, int generatedBy);

    /**
     * Finds a bill by its bill number.
     *
     * @param billNumber the bill number to find
     * @return the InvoiceRecord, or null
     */
    InvoiceRecord getBillByNumber(String billNumber);

    /**
     * Finds a bill linked to a reservation.
     *
     * @param reservationId the reservation ID
     * @return the InvoiceRecord, or null
     */
    InvoiceRecord getBillByReservationId(int reservationId);

    /**
     * Gets all generated bills.
     *
     * @return list of all InvoiceRecord objects
     */
    List<InvoiceRecord> getAllBills();

    /**
     * Gets total revenue from all bills.
     *
     * @return the total revenue amount
     */
    double getTotalRevenue();

    /**
     * Gets the total number of bills.
     *
     * @return the bill count
     */
    int getTotalBillCount();

    /**
     * Checks if a bill already exists for a reservation.
     *
     * @param reservationId the reservation to check
     * @return true if a bill exists
     */
    boolean isBillAlreadyGenerated(int reservationId);
}