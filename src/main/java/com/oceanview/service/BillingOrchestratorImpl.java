package com.oceanview.service;

import com.oceanview.dao.IInvoiceRecordGateway;
import com.oceanview.dao.InvoiceRecordGatewayImpl;
import com.oceanview.model.InvoiceRecord;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service Implementation for billing and invoice operations.
 *
 * <p><b>Architecture:</b> Business Logic Layer - validates billing
 * requests, prevents duplicate bill generation, and delegates
 * calculations to the CalculateBill stored procedure through
 * the DAO layer.</p>
 *
 * <p><b>Requirement Traceability:</b> Implements "Calculate and
 * Print Bill" feature. The CalculateBill stored procedure computes:
 * subtotal = rate_per_night × number_of_nights
 * tax_amount = subtotal × (tax_percentage / 100)
 * total_amount = subtotal + tax_amount</p>
 *
 * <p><b>Assumption:</b> A reservation can only have one bill.
 * Once generated, the bill becomes an immutable financial record.
 * The 0% tax rate is applied as Ocean View Resort currently
 * operates without service tax charges.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
public class BillingOrchestratorImpl implements IBillingOrchestrator {

    /** Logger for billing events */
    private static final Logger BILLING_LOGGER =
            Logger.getLogger(BillingOrchestratorImpl.class.getName());

    /** DAO dependency for invoice database operations */
    private final IInvoiceRecordGateway invoiceGateway;

    /**
     * Default constructor using concrete DAO implementation.
     */
    public BillingOrchestratorImpl() {
        this.invoiceGateway = new InvoiceRecordGatewayImpl();
    }

    /**
     * Constructor with injected DAO for Mockito testing.
     *
     * @param invoiceGateway the DAO implementation (or mock)
     */
    public BillingOrchestratorImpl(IInvoiceRecordGateway invoiceGateway) {
        this.invoiceGateway = invoiceGateway;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Workflow:
     * 1. Validate reservation ID and generator user ID
     * 2. Check if a bill already exists for this reservation
     * 3. Call DAO which invokes CalculateBill stored procedure
     * 4. Return the generated InvoiceRecord with all amounts</p>
     */
    @Override
    public InvoiceRecord generateBill(int reservationId, int generatedBy) {

        // Validate input parameters
        if (reservationId <= 0) {
            BILLING_LOGGER.warning(
                    "Invalid reservation ID for bill generation: "
                            + reservationId);
            return null;
        }

        if (generatedBy <= 0) {
            BILLING_LOGGER.warning(
                    "Invalid user ID for bill generation: " + generatedBy);
            return null;
        }

        // Prevent duplicate bill generation
        if (isBillAlreadyGenerated(reservationId)) {
            BILLING_LOGGER.warning(
                    "Bill already exists for reservation ID: "
                            + reservationId);
            return null;
        }

        try {
            // Delegate to DAO which calls CalculateBill stored procedure
            InvoiceRecord generatedBill =
                    invoiceGateway.generateAndStoreBill(
                            reservationId, generatedBy);

            if (generatedBill != null) {
                BILLING_LOGGER.info("Bill generated successfully: "
                        + generatedBill.getBillNumber()
                        + " | Total: LKR "
                        + generatedBill.getTotalAmount());
            } else {
                BILLING_LOGGER.severe(
                        "Bill generation returned null for reservation: "
                                + reservationId);
            }

            return generatedBill;

        } catch (Exception billException) {
            BILLING_LOGGER.log(Level.SEVERE,
                    "Error generating bill for reservation: "
                            + reservationId, billException);
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public InvoiceRecord getBillByNumber(String billNumber) {
        if (billNumber == null || billNumber.trim().isEmpty()) {
            BILLING_LOGGER.warning("Bill number is null or empty");
            return null;
        }
        return invoiceGateway.findBillByNumber(billNumber.trim());
    }

    /** {@inheritDoc} */
    @Override
    public InvoiceRecord getBillByReservationId(int reservationId) {
        if (reservationId <= 0) {
            return null;
        }
        return invoiceGateway.findBillByReservationId(reservationId);
    }

    /** {@inheritDoc} */
    @Override
    public List<InvoiceRecord> getAllBills() {
        return invoiceGateway.findAllBills();
    }

    /** {@inheritDoc} */
    @Override
    public double getTotalRevenue() {
        return invoiceGateway.getTotalRevenue();
    }

    /** {@inheritDoc} */
    @Override
    public int getTotalBillCount() {
        return invoiceGateway.getBillCount();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isBillAlreadyGenerated(int reservationId) {
        if (reservationId <= 0) {
            return false;
        }
        InvoiceRecord existingBill =
                invoiceGateway.findBillByReservationId(reservationId);
        return existingBill != null;
    }
}