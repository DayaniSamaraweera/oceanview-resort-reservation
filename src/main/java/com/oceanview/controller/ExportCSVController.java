package com.oceanview.controller;

import com.oceanview.model.GuestReservation;
import com.oceanview.model.InvoiceRecord;
import com.oceanview.service.IBillingOrchestrator;
import com.oceanview.service.BillingOrchestratorImpl;
import com.oceanview.service.IBookingFlowOrchestrator;
import com.oceanview.service.BookingFlowOrchestratorImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

//Exports reservation and billing data as CSV files for download.//

@WebServlet("/ExportCSV")
public class ExportCSVController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final Logger CSV_LOGGER =
            Logger.getLogger(ExportCSVController.class.getName());

    private IBookingFlowOrchestrator bookingService;
    private IBillingOrchestrator billingService;

    @Override
    public void init() throws ServletException {
        bookingService = new BookingFlowOrchestratorImpl();
        billingService = new BillingOrchestratorImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String exportType = request.getParameter("type");

        if ("reservations".equals(exportType)) {
            exportReservationsCSV(response);
        } else if ("bills".equals(exportType)) {
            exportBillsCSV(response);
        } else {
            response.sendRedirect(request.getContextPath()
                    + "/ReportController");
        }
    }

    /**
     * Exports all reservations as a CSV file.
     */
    private void exportReservationsCSV(HttpServletResponse response)
            throws IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"OceanView_Reservations.csv\"");

        PrintWriter csvWriter = response.getWriter();

        // CSV Header
        csvWriter.println("Reservation Number,Guest Name,Contact Number,"
                + "Room Type,Check-In Date,Check-Out Date,"
                + "Nights,Status,Created At");

        // CSV Data
        List<GuestReservation> reservations =
                bookingService.getAllReservations();

        for (GuestReservation res : reservations) {
            csvWriter.println(
                    escapeCSV(res.getReservationNumber()) + ","
                    + escapeCSV(res.getGuestName()) + ","
                    + escapeCSV(res.getContactNumber()) + ","
                    + escapeCSV(res.getRoomType()) + ","
                    + res.getCheckInDate() + ","
                    + res.getCheckOutDate() + ","
                    + res.getNumberOfNights() + ","
                    + escapeCSV(res.getReservationStatus()) + ","
                    + res.getCreatedAt()
            );
        }

        csvWriter.flush();
        CSV_LOGGER.info("Reservations CSV exported: "
                + reservations.size() + " records");
    }

    /**
     * Exports all bills as a CSV file.
     */
    private void exportBillsCSV(HttpServletResponse response)
            throws IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"OceanView_Bills.csv\"");

        PrintWriter csvWriter = response.getWriter();

        // CSV Header
        csvWriter.println("Bill Number,Reservation Number,Guest Name,"
                + "Room Type,Rate Per Night,Nights,"
                + "Subtotal,Tax,Total Amount,Generated At");

        // CSV Data
        List<InvoiceRecord> bills = billingService.getAllBills();

        for (InvoiceRecord bill : bills) {
            csvWriter.println(
                    escapeCSV(bill.getBillNumber()) + ","
                    + escapeCSV(bill.getReservationNumber()) + ","
                    + escapeCSV(bill.getGuestName()) + ","
                    + escapeCSV(bill.getRoomType()) + ","
                    + bill.getRatePerNight() + ","
                    + bill.getNumberOfNights() + ","
                    + bill.getSubtotal() + ","
                    + bill.getTaxAmount() + ","
                    + bill.getTotalAmount() + ","
                    + bill.getGeneratedAt()
            );
        }

        csvWriter.flush();
        CSV_LOGGER.info("Bills CSV exported: "
                + bills.size() + " records");
    }

 
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")
                || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}