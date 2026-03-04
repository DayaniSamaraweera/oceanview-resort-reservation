<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.oceanview.model.SystemUser" %>
<%@ page import="com.oceanview.model.InvoiceRecord" %>
<%@ page import="com.oceanview.model.GuestReservation" %>
<%
    /*
     * viewBill.jsp - Calculate and Print Bill Page
     *
     * Requirement Traceability: Implements "Calculate and Print Bill"
     * feature. Displays professional invoice design with:
     * - Purple header with resort branding
     * - Itemized billing table
     * - 0% tax line (Ocean View Resort policy)
     * - Total amount
     * - Signature line for printed copies
     *
     * Uses CalculateBill stored procedure via BillingController
     * to compute: subtotal = rate_per_night x number_of_nights
     *
     * Author: Dayani Samaraweera
     */

    SystemUser loggedUser = (SystemUser) session.getAttribute("loggedInUser");
    if (loggedUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String userRole = loggedUser.getUserRole();
    InvoiceRecord bill = (InvoiceRecord) request.getAttribute("bill");
    GuestReservation reservation =
            (GuestReservation) request.getAttribute("reservation");
    Boolean billExists = (Boolean) request.getAttribute("billExists");
    InvoiceRecord existingBill =
            (InvoiceRecord) request.getAttribute("existingBill");

    String successMsg = request.getParameter("success");
    String errorMsg   = request.getParameter("error");
    String reservationIdParam = request.getParameter("reservationId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Generate Bill - Ocean View Resort</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/oceanview-styles.css">
</head>
<body>

<div class="app-layout">

    <!-- ========== LEFT SIDEBAR ========== -->
    <nav class="sidebar">
        <div class="sidebar-header">
            <img src="https://i.imgur.com/OceanViewLogo.png" alt="Logo"
                 style="width:55px;height:55px;border-radius:50%;margin-bottom:8px;
                        border:2px solid rgba(255,255,255,0.3);">
            <h2>Ocean View</h2>
            <p>Resort &amp; Spa, Galle</p>
        </div>
        <div class="sidebar-nav">
            <a href="<%= request.getContextPath() %>/DashboardController">
                <span class="nav-icon">🏠</span><span>Home</span></a>
            <a href="<%= request.getContextPath() %>/ReservationController?action=showAddForm">
                <span class="nav-icon">➕</span><span>Add New Reservation</span></a>
            <a href="<%= request.getContextPath() %>/ReservationController?action=list">
                <span class="nav-icon">📋</span><span>Reservation List</span></a>
            <a href="<%= request.getContextPath() %>/ReservationController?action=search">
                <span class="nav-icon">🔍</span><span>Find Reservation</span></a>
            <a href="<%= request.getContextPath() %>/BillingController?action=list" class="active">
                <span class="nav-icon">🧾</span><span>Generate Bill</span></a>
            <% if ("ADMIN".equals(userRole)) { %>
            <div class="nav-divider"></div>
            <a href="<%= request.getContextPath() %>/ReportController">
                <span class="nav-icon">📊</span><span>Reports</span></a>
            <a href="<%= request.getContextPath() %>/StaffManagement">
                <span class="nav-icon">👥</span><span>Staff Management</span></a>
            <a href="<%= request.getContextPath() %>/AuditLog">
                <span class="nav-icon">📝</span><span>Activity Log</span></a>
            <% } %>
            <div class="nav-divider"></div>
            <a href="<%= request.getContextPath() %>/HelpController">
                <span class="nav-icon">❓</span><span>Help Section</span></a>
        </div>
        <div class="sidebar-footer">
            <div class="user-info">
                Signed in as <strong><%= loggedUser.getFullName() %></strong>
                <br><small><%= userRole %></small>
            </div>
            <a href="<%= request.getContextPath() %>/LoginController?action=logout"
               class="signout-btn">🚪 Sign Out</a>
        </div>
    </nav>

    <!-- ========== MAIN CONTENT ========== -->
    <main class="main-content">

        <div class="page-header">
            <h1>Generate Bill</h1>
            <p class="breadcrumb">Ocean View Resort &gt; Generate Bill</p>
        </div>

        <!-- Messages -->
        <% if ("generated".equals(successMsg)) { %>
        <div class="alert alert-success">✅ Bill generated successfully!</div>
        <% } else if ("already_exists".equals(errorMsg)) { %>
        <div class="alert alert-warning">
            ⚠️ A bill has already been generated for this reservation.
        </div>
        <% } %>

        <% if (bill != null) { %>
        <!-- ========== INVOICE VIEW ========== -->

        <!-- Print / Action Buttons -->
        <div class="btn-group" style="margin-bottom:20px;" id="noPrint">
            <a href="<%= request.getContextPath() %>/BillingController?action=list"
               class="btn-secondary">← Back to Bills</a>
            <button type="button" class="btn-primary"
                    onclick="window.print()"
                    style="width:auto;">🖨️ Print Invoice</button>
            <a href="<%= request.getContextPath() %>/ReservationController?action=view&id=<%= bill.getReservationId() %>"
               class="btn-secondary">View Reservation</a>
        </div>

        <!-- Professional Invoice Design -->
        <div class="invoice-container">

            <!-- Invoice Header -->
            <div class="invoice-header">
                <img src="https://i.imgur.com/OceanViewLogo.png" alt="Logo"
                     style="width:70px;height:70px;border-radius:50%;
                            border:3px solid rgba(255,255,255,0.4);
                            margin-bottom:12px;">
                <h2>Ocean View Resort</h2>
                <p>Beach Road, Unawatuna, Galle, Sri Lanka</p>
                <p>Tel: +94 91 223 4567 | reservations@oceanviewresort.lk</p>
                <div style="margin-top:15px; background:rgba(255,255,255,0.15);
                            border-radius:12px; padding:8px 20px; display:inline-block;">
                    <span style="font-size:0.85rem; opacity:0.8;">INVOICE NUMBER</span><br>
                    <strong style="font-size:1.2rem;"><%= bill.getBillNumber() %></strong>
                </div>
            </div>

            <!-- Invoice Body -->
            <div class="invoice-body">

                <!-- Invoice Details Grid -->
                <div class="invoice-details-grid">
                    <div class="invoice-detail-block">
                        <h4>Billed To</h4>
                        <p><strong><%= bill.getGuestName() %></strong></p>
                        <% if (reservation != null) { %>
                        <p><%= reservation.getAddress() %></p>
                        <p>Tel: <%= reservation.getContactNumber() %></p>
                        <% if (reservation.getGuestEmail() != null &&
                               !reservation.getGuestEmail().isEmpty()) { %>
                        <p><%= reservation.getGuestEmail() %></p>
                        <% } %>
                        <% } %>
                    </div>
                    <div class="invoice-detail-block">
                        <h4>Invoice Details</h4>
                        <p><strong>Invoice No:</strong> <%= bill.getBillNumber() %></p>
                        <p><strong>Reservation No:</strong> <%= bill.getReservationNumber() %></p>
                        <p><strong>Issue Date:</strong>
                            <%= bill.getGeneratedAt() != null
                                ? bill.getGeneratedAt().toString().substring(0,10)
                                : "N/A" %>
                        </p>
                        <p><strong>Issued By:</strong>
                            <%= bill.getGeneratedByName() != null
                                ? bill.getGeneratedByName() : "N/A" %>
                        </p>
                    </div>
                </div>

                <!-- Stay Details -->
                <div style="background:#faf5ff; border-radius:12px;
                            padding:15px; margin-bottom:20px;">
                    <div style="display:grid; grid-template-columns:repeat(3,1fr); gap:15px;">
                        <div style="text-align:center;">
                            <div style="font-size:0.75rem; color:#888; text-transform:uppercase;
                                        letter-spacing:0.5px;">Room Type</div>
                            <div style="font-weight:600; color:#6c3483; margin-top:4px;">
                                <%= bill.getRoomType() %>
                            </div>
                        </div>
                        <% if (reservation != null) { %>
                        <div style="text-align:center;">
                            <div style="font-size:0.75rem; color:#888; text-transform:uppercase;
                                        letter-spacing:0.5px;">Check-In Date</div>
                            <div style="font-weight:600; color:#6c3483; margin-top:4px;">
                                <%= reservation.getCheckInDate() %>
                            </div>
                        </div>
                        <div style="text-align:center;">
                            <div style="font-size:0.75rem; color:#888; text-transform:uppercase;
                                        letter-spacing:0.5px;">Check-Out Date</div>
                            <div style="font-weight:600; color:#6c3483; margin-top:4px;">
                                <%= reservation.getCheckOutDate() %>
                            </div>
                        </div>
                        <% } else { %>
                        <div style="text-align:center;">
                            <div style="font-size:0.75rem; color:#888;">Check-In Date</div>
                            <div style="font-weight:600; color:#6c3483; margin-top:4px;">N/A</div>
                        </div>
                        <div style="text-align:center;">
                            <div style="font-size:0.75rem; color:#888;">Check-Out Date</div>
                            <div style="font-weight:600; color:#6c3483; margin-top:4px;">N/A</div>
                        </div>
                        <% } %>
                    </div>
                </div>

                <!-- Itemized Table -->
                <table class="invoice-table">
                    <thead>
                        <tr>
                            <th>Description</th>
                            <th>Rate Per Night</th>
                            <th>Nights</th>
                            <th style="text-align:right;">Amount</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>
                                <strong><%= bill.getRoomType() %> Room Accommodation</strong>
                                <br>
                                <small style="color:#888;">
                                    Ocean View Resort, Galle, Sri Lanka
                                </small>
                            </td>
                            <td>LKR <%= String.format("%,.2f", bill.getRatePerNight()) %></td>
                            <td><%= bill.getNumberOfNights() %> night(s)</td>
                            <td style="text-align:right;">
                                LKR <%= String.format("%,.2f", bill.getSubtotal()) %>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="3" style="text-align:right; color:#888;">
                                Subtotal
                            </td>
                            <td style="text-align:right;">
                                LKR <%= String.format("%,.2f", bill.getSubtotal()) %>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="3" style="text-align:right; color:#888;">
                                Tax (0%)
                            </td>
                            <td style="text-align:right; color:#888;">
                                LKR 0.00
                            </td>
                        </tr>
                    </tbody>
                </table>

                <!-- Total Row -->
                <div class="invoice-total-row">
                    <span class="total-label">TOTAL AMOUNT DUE</span>
                    <span class="total-amount">
                        LKR <%= String.format("%,.2f", bill.getTotalAmount()) %>
                    </span>
                </div>

                <!-- Thank You Message -->
                <div style="text-align:center; margin-top:25px; padding:15px;
                            background:#f4f6f9; border-radius:12px;">
                    <p style="color:#6c3483; font-weight:600; margin-bottom:4px;">
                        Thank you for staying with us!
                    </p>
                    <p style="color:#888; font-size:0.85rem;">
                        We hope to welcome you back to Ocean View Resort, Galle.
                    </p>
                </div>

                <!-- Signature Line -->
                <div class="invoice-signature">
                    <div class="sig-line"></div>
                    <p>Authorized Signature</p>
                    <p>Ocean View Resort Management</p>
                </div>

            </div>

            <!-- Invoice Footer -->
            <div class="invoice-footer">
                <p>&copy; 2026 Ocean View Resort | Beach Road, Unawatuna, Galle, Sri Lanka</p>
                <p>Tel: +94 91 223 4567 | reservations@oceanviewresort.lk</p>
            </div>

        </div>

        <% } else if (reservation != null) { %>
        <!-- ========== GENERATE BILL FORM ========== -->

        <div class="data-card" style="max-width:600px; margin:0 auto;">
            <h3 style="color:#6c3483; margin-bottom:20px;">
                🧾 Generate Bill for Reservation
            </h3>

            <% if (billExists != null && billExists) { %>
            <div class="alert alert-warning">
                ⚠️ A bill has already been generated for this reservation.
            </div>
            <% if (existingBill != null) { %>
            <div style="text-align:center; margin-top:15px;">
                <a href="<%= request.getContextPath() %>/BillingController?action=view&billNumber=<%= existingBill.getBillNumber() %>"
                   class="btn-gold">View Existing Bill → <%= existingBill.getBillNumber() %></a>
            </div>
            <% } %>
            <% } else { %>

            <!-- Reservation Summary before generating bill -->
            <div class="detail-row">
                <span class="detail-label">Reservation Number</span>
                <span class="detail-value">
                    <strong><%= reservation.getReservationNumber() %></strong>
                </span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Guest Name</span>
                <span class="detail-value"><%= reservation.getGuestName() %></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Room Type</span>
                <span class="detail-value"><%= reservation.getRoomType() %></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Rate Per Night</span>
                <span class="detail-value">
                    LKR <%= String.format("%,.2f", reservation.getRatePerNight()) %>
                </span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Number of Nights</span>
                <span class="detail-value">
                    <strong><%= reservation.getNumberOfNights() %></strong>
                </span>
            </div>
            <div class="detail-row" style="border-top:2px solid #8e44ad;
                                          padding-top:12px; margin-top:5px;">
                <span class="detail-label" style="font-weight:700; color:#6c3483;">
                    Estimated Total
                </span>
                <span class="detail-value" style="font-weight:700; color:#6c3483; font-size:1.2rem;">
                    LKR <%= String.format("%,.2f",
                        reservation.getRatePerNight() * reservation.getNumberOfNights()) %>
                </span>
            </div>

            <form action="<%= request.getContextPath() %>/BillingController"
                  method="POST" style="margin-top:25px;">
                <input type="hidden" name="action" value="generate">
                <input type="hidden" name="reservationId"
                       value="<%= reservation.getReservationId() %>">
                <div class="btn-group" style="justify-content:flex-end;">
                    <a href="<%= request.getContextPath() %>/BillingController?action=list"
                       class="btn-secondary">Cancel</a>
                    <button type="submit" class="btn-success" style="width:auto;">
                        ✅ Generate Bill Now
                    </button>
                </div>
            </form>

            <% } %>
        </div>

        <% } else { %>
        <!-- ========== NO DATA STATE ========== -->
        <div class="data-card" style="text-align:center; padding:50px; max-width:600px; margin:0 auto;">
            <p style="font-size:3rem;">🧾</p>
            <h3 style="color:#888; margin:15px 0;">No Bill Selected</h3>
            <p style="color:#999; margin-bottom:20px;">
                Select a reservation to generate a bill, or view existing bills.
            </p>
            <div class="btn-group" style="justify-content:center;">
                <a href="<%= request.getContextPath() %>/BillingController?action=list"
                   class="btn-secondary">View All Bills</a>
                <a href="<%= request.getContextPath() %>/ReservationController?action=list"
                   class="btn-primary" style="width:auto;">View Reservations</a>
            </div>
        </div>
        <% } %>

        <footer class="app-footer">
            <p>&copy; 2026 Ocean View Resort. All rights reserved.</p>
            <p class="footer-address">
                Beach Road, Unawatuna, Galle, Sri Lanka | +94 91 223 4567
            </p>
        </footer>

    </main>
</div>

<!-- Print styles override -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Hide action buttons when printing
        window.addEventListener('beforeprint', function() {
            var noPrint = document.getElementById('noPrint');
            if (noPrint) noPrint.style.display = 'none';
        });

        window.addEventListener('afterprint', function() {
            var noPrint = document.getElementById('noPrint');
            if (noPrint) noPrint.style.display = 'flex';
        });
    });
</script>

</body>
</html>