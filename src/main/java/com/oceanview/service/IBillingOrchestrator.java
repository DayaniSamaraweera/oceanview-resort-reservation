package com.oceanview.service;

import com.oceanview.model.InvoiceRecord;
import java.util.List;

// Service Interface for billing and invoice operations.

public interface IBillingOrchestrator {

    /**
     Generates a bill for a reservation using the
     CalculateBill stored procedure.
     */
    InvoiceRecord generateBill(int reservationId, int generatedBy);
     InvoiceRecord getBillByNumber(String billNumber);
    InvoiceRecord getBillByReservationId(int reservationId);
    List<InvoiceRecord> getAllBills();

    double getTotalRevenue();

    int getTotalBillCount();

    boolean isBillAlreadyGenerated(int reservationId);
}