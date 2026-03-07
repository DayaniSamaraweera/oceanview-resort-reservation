package com.oceanview.service;

import com.oceanview.dao.IInvoiceRecordGateway;
import com.oceanview.dao.InvoiceRecordGatewayImpl;
import com.oceanview.model.InvoiceRecord;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Service Implementation for billing and invoice operations.//
public class BillingOrchestratorImpl implements IBillingOrchestrator {

   
    private static final Logger BILLING_LOGGER =
            Logger.getLogger(BillingOrchestratorImpl.class.getName());

    private final IInvoiceRecordGateway invoiceGateway;

    public BillingOrchestratorImpl() {
        this.invoiceGateway = new InvoiceRecordGatewayImpl();
    }

    public BillingOrchestratorImpl(IInvoiceRecordGateway invoiceGateway) {
        this.invoiceGateway = invoiceGateway;
    }

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

    @Override
    public InvoiceRecord getBillByNumber(String billNumber) {
        if (billNumber == null || billNumber.trim().isEmpty()) {
            BILLING_LOGGER.warning("Bill number is null or empty");
            return null;
        }
        return invoiceGateway.findBillByNumber(billNumber.trim());
    }

  
    @Override
    public InvoiceRecord getBillByReservationId(int reservationId) {
        if (reservationId <= 0) {
            return null;
        }
        return invoiceGateway.findBillByReservationId(reservationId);
    }

   
    @Override
    public List<InvoiceRecord> getAllBills() {
        return invoiceGateway.findAllBills();
    }

   
    @Override
    public double getTotalRevenue() {
        return invoiceGateway.getTotalRevenue();
    }

    @Override
    public int getTotalBillCount() {
        return invoiceGateway.getBillCount();
    }

    
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