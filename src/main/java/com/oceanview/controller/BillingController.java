package com.oceanview.controller;

import com.oceanview.model.GuestReservation;
import com.oceanview.model.InvoiceRecord;
import com.oceanview.model.SystemUser;
import com.oceanview.service.IAuditTrailOrchestrator;
import com.oceanview.service.AuditTrailOrchestratorImpl;
import com.oceanview.service.IBillingOrchestrator;
import com.oceanview.service.BillingOrchestratorImpl;
import com.oceanview.service.IBookingFlowOrchestrator;
import com.oceanview.service.BookingFlowOrchestratorImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//Controller Servlet for billing and invoice operations.
 
@WebServlet("/BillingController")
public class BillingController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    /** Logger for billing events */
    private static final Logger BILL_LOGGER =
            Logger.getLogger(BillingController.class.getName());

    /** Billing service */
    private IBillingOrchestrator billingService;

    /** Booking service for reservation lookup */
    private IBookingFlowOrchestrator bookingService;

    /** Audit service */
    private IAuditTrailOrchestrator auditService;

    @Override
    public void init() throws ServletException {
        billingService = new BillingOrchestratorImpl();
        bookingService = new BookingFlowOrchestratorImpl();
        auditService = new AuditTrailOrchestratorImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "generate":
                showGenerateForm(request, response);
                break;
            case "view":
                viewBill(request, response);
                break;
            case "list":
                listBills(request, response);
                break;
            default:
                listBills(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("generate".equals(action)) {
            generateBill(request, response);
        } else {
            response.sendRedirect(request.getContextPath()
                    + "/BillingController?action=list");
        }
    }

    /**
     * Shows the bill generation form with reservation details.
     */
    private void showGenerateForm(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        String reservationIdParam =
                request.getParameter("reservationId");

        if (reservationIdParam != null) {
            try {
                int reservationId =
                        Integer.parseInt(reservationIdParam);

                GuestReservation reservation =
                        bookingService.getReservationById(reservationId);

                if (reservation != null) {
                    // Check if bill already exists
                    boolean billExists =
                            billingService.isBillAlreadyGenerated(
                                    reservationId);
                    request.setAttribute("reservation", reservation);
                    request.setAttribute("billExists", billExists);

                    if (billExists) {
                        InvoiceRecord existingBill =
                                billingService.getBillByReservationId(
                                        reservationId);
                        request.setAttribute("existingBill", existingBill);
                    }
                }
            } catch (NumberFormatException numError) {
                BILL_LOGGER.warning("Invalid reservation ID");
            }
        }

        request.getRequestDispatcher("/viewBill.jsp")
                .forward(request, response);
    }

    /**
     * Generates a new bill using CalculateBill stored procedure.
     */
    private void generateBill(HttpServletRequest request,
                              HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        SystemUser loggedUser =
                (SystemUser) session.getAttribute("loggedInUser");

        try {
            int reservationId = Integer.parseInt(
                    request.getParameter("reservationId"));

            InvoiceRecord generatedBill =
                    billingService.generateBill(
                            reservationId, loggedUser.getUserId());

            if (generatedBill != null) {
                BILL_LOGGER.info("Bill generated: "
                        + generatedBill.getBillNumber());

                // Log to audit trail
                auditService.logActivity(
                        loggedUser.getUserId(),
                        loggedUser.getUsername(),
                        "GENERATE_BILL",
                        "Generated bill " + generatedBill.getBillNumber()
                                + " for reservation ID: " + reservationId
                                + " | Total: LKR "
                                + generatedBill.getTotalAmount(),
                        "bills",
                        generatedBill.getBillId(),
                        request.getRemoteAddr());

                response.sendRedirect(request.getContextPath()
                        + "/BillingController?action=view&billNumber="
                        + generatedBill.getBillNumber()
                        + "&success=generated");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/BillingController?action=generate&reservationId="
                        + reservationId + "&error=already_exists");
            }

        } catch (Exception billError) {
            BILL_LOGGER.log(Level.SEVERE,
                    "Error generating bill", billError);
            response.sendRedirect(request.getContextPath()
                    + "/BillingController?action=list&error=system");
        }
    }

    /**
     * Displays a single bill/invoice.
     */
    private void viewBill(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String billNumber = request.getParameter("billNumber");
        String reservationIdParam =
                request.getParameter("reservationId");

        InvoiceRecord bill = null;

        if (billNumber != null && !billNumber.isEmpty()) {
            bill = billingService.getBillByNumber(billNumber);
        } else if (reservationIdParam != null) {
            try {
                int reservationId =
                        Integer.parseInt(reservationIdParam);
                bill = billingService.getBillByReservationId(
                        reservationId);
            } catch (NumberFormatException numError) {
                BILL_LOGGER.warning("Invalid reservation ID");
            }
        }

        if (bill != null) {
            GuestReservation reservation =
                    bookingService.getReservationById(
                            bill.getReservationId());
            request.setAttribute("bill", bill);
            request.setAttribute("reservation", reservation);
        }

        request.getRequestDispatcher("/viewBill.jsp")
                .forward(request, response);
    }

    /**
     * Lists all generated bills with summary statistics.
     */
    private void listBills(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        List<InvoiceRecord> allBills = billingService.getAllBills();
        double totalRevenue = billingService.getTotalRevenue();
        int totalBillCount = billingService.getTotalBillCount();

        request.setAttribute("bills", allBills);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalBillCount", totalBillCount);

        request.getRequestDispatcher("/listBills.jsp")
                .forward(request, response);
    }
}