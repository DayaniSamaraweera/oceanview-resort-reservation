package com.oceanview.dao;

import com.oceanview.model.InvoiceRecord;
import java.util.List;

/**
 * DAO interface for bill operations.
 */
public interface IInvoiceRecordGateway {

    // calls CalculateBill stored procedure and saves the result
    InvoiceRecord generateAndStoreBill(int reservationId, int generatedBy);

    InvoiceRecord findBillByNumber(String billNumber);

    InvoiceRecord findBillByReservationId(int reservationId);

    List<InvoiceRecord> findAllBills();

    // returns sum of all bill amounts
    double getTotalRevenue();

    int getBillCount();
}