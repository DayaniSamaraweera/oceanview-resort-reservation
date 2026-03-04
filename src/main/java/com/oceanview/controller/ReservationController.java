package com.oceanview.controller;

import com.oceanview.model.GuestReservation;
import com.oceanview.model.ResortRoom;
import com.oceanview.model.SystemUser;
import com.oceanview.service.IAuditTrailOrchestrator;
import com.oceanview.service.AuditTrailOrchestratorImpl;
import com.oceanview.service.IBookingFlowOrchestrator;
import com.oceanview.service.BookingFlowOrchestratorImpl;
import com.oceanview.service.IRoomInventoryOrchestrator;
import com.oceanview.service.RoomInventoryOrchestratorImpl;
import com.oceanview.service.ReservationEventNotifier;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller Servlet for all reservation operations.
 *
 * <p><b>Architecture:</b> MVC Controller in the Presentation Layer.
 * Handles multiple reservation actions via the "action" parameter:
 * - showAddForm: Display the 3-step reservation form
 * - add: Process new reservation submission
 * - list: Display all reservations with status filter
 * - view: Display single reservation details
 * - search: Search reservation by number
 * - updateStatus: Change reservation status</p>
 *
 * <p><b>Requirement Traceability:</b>
 * - "Add New Reservation" → showAddForm + add actions
 * - "Display Reservation Details" → view action
 * - Status management → updateStatus action</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
@WebServlet("/ReservationController")
public class ReservationController extends HttpServlet {

    /** Logger for reservation controller events */
    private static final Logger RES_LOGGER =
            Logger.getLogger(ReservationController.class.getName());

    /** Booking service dependency */
    private IBookingFlowOrchestrator bookingService;

    /** Room service dependency */
    private IRoomInventoryOrchestrator roomService;

    /** Audit service dependency */
    private IAuditTrailOrchestrator auditService;

    @Override
    public void init() throws ServletException {
        bookingService = new BookingFlowOrchestratorImpl();
        roomService = new RoomInventoryOrchestratorImpl();
        auditService = new AuditTrailOrchestratorImpl();
    }

    /**
     * Handles GET requests for displaying pages and data.
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            action = "list";
        }

        switch (action) {
            case "showAddForm":
                showAddReservationForm(request, response);
                break;
            case "list":
                listReservations(request, response);
                break;
            case "view":
                viewReservation(request, response);
                break;
            case "search":
                showSearchPage(request, response);
                break;
            case "searchResult":
                searchReservation(request, response);
                break;
            default:
                listReservations(request, response);
                break;
        }
    }

    /**
     * Handles POST requests for form submissions.
     */
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            response.sendRedirect(request.getContextPath()
                    + "/ReservationController?action=list");
            return;
        }

        switch (action) {
            case "add":
                addReservation(request, response);
                break;
            case "updateStatus":
                updateReservationStatus(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath()
                        + "/ReservationController?action=list");
                break;
        }
    }

    /**
     * Displays the 3-step Add New Reservation form.
     * Loads available rooms for the room selection step.
     */
    private void showAddReservationForm(HttpServletRequest request,
                                        HttpServletResponse response)
            throws ServletException, IOException {

        // Load available rooms for each type
        List<ResortRoom> standardRooms =
                roomService.getAvailableRoomsByType("Standard");
        List<ResortRoom> superiorRooms =
                roomService.getAvailableRoomsByType("Superior");
        List<ResortRoom> premiumRooms =
                roomService.getAvailableRoomsByType("Premium");
        List<ResortRoom> executiveRooms =
                roomService.getAvailableRoomsByType("Executive");

        request.setAttribute("standardRooms", standardRooms);
        request.setAttribute("superiorRooms", superiorRooms);
        request.setAttribute("premiumRooms", premiumRooms);
        request.setAttribute("executiveRooms", executiveRooms);

        // Room rates for JavaScript display
        request.setAttribute("standardRate", 5500.00);
        request.setAttribute("superiorRate", 8500.00);
        request.setAttribute("premiumRate", 13000.00);
        request.setAttribute("executiveRate", 22000.00);

        RES_LOGGER.info("Add reservation form loaded");
        request.getRequestDispatcher("/addReservation.jsp")
                .forward(request, response);
    }

    /**
     * Processes the Add New Reservation form submission.
     * Validates all inputs and creates the reservation.
     */
    private void addReservation(HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        SystemUser loggedUser =
                (SystemUser) session.getAttribute("loggedInUser");

        try {
            // Extract form data
            String guestName = request.getParameter("guestName");
            String address = request.getParameter("address");
            String contactNumber = request.getParameter("contactNumber");
            String guestEmail = request.getParameter("guestEmail");
            String roomType = request.getParameter("roomType");
            int roomId = Integer.parseInt(
                    request.getParameter("roomId"));
            LocalDate checkInDate = LocalDate.parse(
                    request.getParameter("checkInDate"));
            LocalDate checkOutDate = LocalDate.parse(
                    request.getParameter("checkOutDate"));

            // Build reservation using Builder pattern
            GuestReservation newReservation =
                    new GuestReservation.Builder()
                            .guestName(guestName)
                            .address(address)
                            .contactNumber(contactNumber)
                            .guestEmail(guestEmail)
                            .roomId(roomId)
                            .roomType(roomType)
                            .checkInDate(checkInDate)
                            .checkOutDate(checkOutDate)
                            .createdBy(loggedUser.getUserId())
                            .build();

            // Create reservation via service layer
            int reservationId =
                    bookingService.createNewReservation(newReservation);

            if (reservationId > 0) {
                RES_LOGGER.info("Reservation created successfully - ID: "
                        + reservationId);

                // Notify observers (email notification)
                try {
                    GuestReservation createdReservation =
                            bookingService.getReservationById(reservationId);
                    if (createdReservation != null) {
                        ReservationEventNotifier.getInstance()
                                .notifyReservationCreated(createdReservation);
                    }
                } catch (Exception notifyException) {
                    RES_LOGGER.log(Level.WARNING,
                            "Observer notification failed", notifyException);
                }

                // Log to audit trail
                auditService.logActivity(
                        loggedUser.getUserId(),
                        loggedUser.getUsername(),
                        "CREATE_RESERVATION",
                        "Created reservation for guest: " + guestName,
                        "reservations",
                        reservationId,
                        request.getRemoteAddr());

                response.sendRedirect(request.getContextPath()
                        + "/ReservationController?action=view&id="
                        + reservationId + "&success=created");

            } else {
                request.setAttribute("errorMessage",
                        "Failed to create reservation. The room may "
                                + "already be booked for the selected dates.");
                showAddReservationForm(request, response);
            }

        } catch (IllegalArgumentException validationError) {
            request.setAttribute("errorMessage",
                    validationError.getMessage());
            showAddReservationForm(request, response);

        } catch (Exception unexpectedError) {
            RES_LOGGER.log(Level.SEVERE,
                    "Unexpected error creating reservation",
                    unexpectedError);
            request.setAttribute("errorMessage",
                    "An unexpected error occurred. Please try again.");
            showAddReservationForm(request, response);
        }
    }

    /**
     * Lists all reservations with optional status filter.
     */
    private void listReservations(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        String statusFilter = request.getParameter("status");
        List<GuestReservation> reservations;

        if (statusFilter != null && !statusFilter.isEmpty()
                && !"All".equals(statusFilter)) {
            reservations = bookingService
                    .getReservationsByStatus(statusFilter);
        } else {
            reservations = bookingService.getAllReservations();
        }

        request.setAttribute("reservations", reservations);
        request.setAttribute("currentFilter",
                statusFilter != null ? statusFilter : "All");

        // Get counts for filter tabs
        request.setAttribute("allCount",
                bookingService.getAllReservations().size());
        request.setAttribute("confirmedCount",
                bookingService.getReservationCount("Confirmed"));
        request.setAttribute("checkedOutCount",
                bookingService.getReservationCount("Checked-Out"));
        request.setAttribute("cancelledCount",
                bookingService.getReservationCount("Cancelled"));

        request.getRequestDispatcher("/listReservations.jsp")
                .forward(request, response);
    }

    /**
     * Displays detailed view of a single reservation.
     */
    private void viewReservation(HttpServletRequest request,
                                 HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        String numberParam = request.getParameter("number");

        GuestReservation reservation = null;

        if (idParam != null && !idParam.isEmpty()) {
            try {
                int reservationId = Integer.parseInt(idParam);
                reservation =
                        bookingService.getReservationById(reservationId);
            } catch (NumberFormatException numError) {
                RES_LOGGER.warning("Invalid reservation ID: " + idParam);
            }
        } else if (numberParam != null && !numberParam.isEmpty()) {
            reservation =
                    bookingService.getReservationDetails(numberParam);
        }

        if (reservation != null) {
            request.setAttribute("reservation", reservation);
            request.getRequestDispatcher("/viewReservation.jsp")
                    .forward(request, response);
        } else {
            request.setAttribute("errorMessage",
                    "Reservation not found.");
            listReservations(request, response);
        }
    }

    /**
     * Shows the search reservation page.
     */
    private void showSearchPage(HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/searchReservation.jsp")
                .forward(request, response);
    }

    /**
     * Searches for a reservation by number and displays result.
     */
    private void searchReservation(HttpServletRequest request,
                                   HttpServletResponse response)
            throws ServletException, IOException {

        String searchNumber =
                request.getParameter("reservationNumber");

        if (searchNumber == null || searchNumber.trim().isEmpty()) {
            request.setAttribute("searchError",
                    "Please enter a reservation number.");
            request.getRequestDispatcher("/searchReservation.jsp")
                    .forward(request, response);
            return;
        }

        GuestReservation foundReservation =
                bookingService.getReservationDetails(searchNumber.trim());

        if (foundReservation != null) {
            request.setAttribute("reservation", foundReservation);
            request.setAttribute("searchNumber", searchNumber.trim());
            request.getRequestDispatcher("/viewReservation.jsp")
                    .forward(request, response);
        } else {
            request.setAttribute("searchError",
                    "No reservation found with number: "
                            + searchNumber.trim());
            request.setAttribute("searchNumber", searchNumber.trim());
            request.getRequestDispatcher("/searchReservation.jsp")
                    .forward(request, response);
        }
    }

    /**
     * Updates a reservation's status (Check-In, Check-Out, Cancel).
     */
    private void updateReservationStatus(HttpServletRequest request,
                                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        SystemUser loggedUser =
                (SystemUser) session.getAttribute("loggedInUser");

        try {
            int reservationId = Integer.parseInt(
                    request.getParameter("reservationId"));
            String newStatus = request.getParameter("newStatus");
            String cancelReason = request.getParameter("cancelReason");

            boolean updateSuccess = bookingService.updateReservationStatus(
                    reservationId, newStatus, cancelReason);

            if (updateSuccess) {
                RES_LOGGER.info("Reservation " + reservationId
                        + " status updated to: " + newStatus);

                // Notify observers of status change
                try {
                    GuestReservation updatedReservation =
                            bookingService.getReservationById(reservationId);
                    if (updatedReservation != null) {
                        ReservationEventNotifier.getInstance()
                                .notifyStatusChanged(
                                        updatedReservation, newStatus);
                    }
                } catch (Exception notifyException) {
                    RES_LOGGER.log(Level.WARNING,
                            "Observer notification failed", notifyException);
                }

                // Log to audit trail
                auditService.logActivity(
                        loggedUser.getUserId(),
                        loggedUser.getUsername(),
                        "UPDATE_STATUS",
                        "Reservation " + reservationId
                                + " changed to " + newStatus,
                        "reservations",
                        reservationId,
                        request.getRemoteAddr());

                response.sendRedirect(request.getContextPath()
                        + "/ReservationController?action=view&id="
                        + reservationId + "&success=updated");

            } else {
                response.sendRedirect(request.getContextPath()
                        + "/ReservationController?action=view&id="
                        + reservationId + "&error=update_failed");
            }

        } catch (Exception statusError) {
            RES_LOGGER.log(Level.SEVERE,
                    "Error updating reservation status", statusError);
            response.sendRedirect(request.getContextPath()
                    + "/ReservationController?action=list&error=system");
        }
    }
}