<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.oceanview.model.SystemUser" %>
<%@ page import="com.oceanview.model.GuestReservation" %>
<%@ page import="com.oceanview.model.InvoiceRecord" %>
<%
    /*
     * viewReservation.jsp - Display Reservation Details Page
     *
     * Requirement Traceability: Implements "Display Reservation Details"
     * feature - retrieves and displays complete booking information
     * for a specific reservation using GetReservationDetails stored procedure.
     *
     * Layout: Two column design
     * - Left column: Guest information card
     * - Right column: Room & booking information card
     * - Bottom: Action buttons (Check-In, Check-Out, Cancel, Generate Bill)
     *
     * Author: Dayani Samaraweera
     */

    SystemUser loggedUser = (SystemUser) session.getAttribute("loggedInUser");
    if (loggedUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String userRole = loggedUser.getUserRole();

    GuestReservation reservation =
            (GuestReservation) request.getAttribute("reservation");

    String successMsg = request.getParameter("success");
    String errorMsg   = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reservation Details - Ocean View Resort</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/oceanview-styles.css">
</head>
<body>

<div class="app-layout">

    <!-- ========== LEFT SIDEBAR ========== -->
    <nav class="sidebar">
        <div class="sidebar-header">
            <img src="https://i.imgur.com/OceanViewLogo.png" alt="Logo"
                 style="width:55px;height:55px;border-radius:50%;margin-bottom:8px;border:2px solid rgba(255,255,255,0.3);">
            <h2>Ocean View</h2>
            <p>Resort &amp; Spa, Galle</p>
        </div>
        <div class="sidebar-nav">
            <a href="<%= request.getContextPath() %>/DashboardController">
                <span class="nav-icon">🏠</span><span>Home</span></a>
            <a href="<%= request.getContextPath() %>/ReservationController?action=showAddForm">
                <span class="nav-icon">➕</span><span>Add New Reservation</span></a>
            <a href="<%= request.getContextPath() %>/ReservationController?action=list" class="active">
                <span class="nav-icon">📋</span><span>Reservation List</span></a>
            <a href="<%= request.getContextPath() %>/ReservationController?action=search">
                <span class="nav-icon">🔍</span><span>Find Reservation</span></a>
            <a href="<%= request.getContextPath() %>/BillingController?action=list">
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
            <h1>Reservation Details</h1>
            <p class="breadcrumb">
                Ocean View Resort &gt;
                <a href="<%= request.getContextPath() %>/ReservationController?action=list">
                    Reservation List</a> &gt; View Details
            </p>
        </div>

        <!-- Messages -->
        <% if ("created".equals(successMsg)) { %>
            <div class="alert alert-success">✅ Reservation created successfully!</div>
        <% } else if ("updated".equals(successMsg)) { %>
            <div class="alert alert-success">✅ Reservation status updated successfully!</div>
        <% } else if ("update_failed".equals(errorMsg)) { %>
            <div class="alert alert-error">⚠️ Failed to update reservation status.</div>
        <% } %>

        <% if (reservation != null) {
               String status = reservation.getReservationStatus();
               String statusClass = "";
               if ("Confirmed".equals(status)) statusClass = "status-confirmed";
               else if ("Checked-In".equals(status)) statusClass = "status-checked-in";
               else if ("Checked-Out".equals(status)) statusClass = "status-checked-out";
               else if ("Cancelled".equals(status)) statusClass = "status-cancelled";
        %>

        <!-- Reservation Number Header -->
        <div class="data-card" style="padding:18px 25px; margin-bottom:20px;">
            <div style="display:flex; justify-content:space-between; align-items:center;">
                <div>
                    <h2 style="color:#6c3483; font-size:1.3rem;">
                        <%= reservation.getReservationNumber() %>
                    </h2>
                    <p style="color:#888; font-size:0.85rem;">
                        Created: <%= reservation.getCreatedAt() != null
                            ? reservation.getCreatedAt().toString().replace("T", " ").substring(0,16)
                            : "N/A" %>
                        <% if (reservation.getCreatedByName() != null &&
                               !reservation.getCreatedByName().isEmpty()) { %>
                        | By: <%= reservation.getCreatedByName() %>
                        <% } %>
                    </p>
                </div>
                <span class="status-badge <%= statusClass %>" style="font-size:0.95rem; padding:8px 20px;">
                    <%= status %>
                </span>
            </div>
        </div>

        <!-- Two Column Detail Cards -->
        <div class="detail-grid">

            <!-- Left: Guest Information -->
            <div class="detail-card">
                <h3>👤 Guest Information</h3>

                <div class="detail-row">
                    <span class="detail-label">Guest Name</span>
                    <span class="detail-value"><%= reservation.getGuestName() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Address</span>
                    <span class="detail-value"><%= reservation.getAddress() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Contact Number</span>
                    <span class="detail-value"><%= reservation.getContactNumber() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Email Address</span>
                    <span class="detail-value">
                        <%= (reservation.getGuestEmail() != null &&
                             !reservation.getGuestEmail().isEmpty())
                            ? reservation.getGuestEmail() : "Not provided" %>
                    </span>
                </div>
            </div>

            <!-- Right: Room & Booking Information -->
            <div class="detail-card">
                <h3>🛏️ Room &amp; Booking Information</h3>

                <div class="detail-row">
                    <span class="detail-label">Room Type</span>
                    <span class="detail-value"><%= reservation.getRoomType() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Room Number</span>
                    <span class="detail-value">
                        <%= (reservation.getRoomNumber() != null &&
                             !reservation.getRoomNumber().isEmpty())
                            ? reservation.getRoomNumber() : "N/A" %>
                    </span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Rate Per Night</span>
                    <span class="detail-value">
                        LKR <%= String.format("%,.2f", reservation.getRatePerNight()) %>
                    </span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Check-In Date</span>
                    <span class="detail-value"><%= reservation.getCheckInDate() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Check-Out Date</span>
                    <span class="detail-value"><%= reservation.getCheckOutDate() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Number of Nights</span>
                    <span class="detail-value">
                        <strong><%= reservation.getNumberOfNights() %> night(s)</strong>
                    </span>
                </div>
                <div class="detail-row" style="border-top:2px solid #f0f0f0; margin-top:5px; padding-top:10px;">
                    <span class="detail-label" style="font-weight:700; color:#6c3483;">
                        Estimated Total
                    </span>
                    <span class="detail-value" style="font-weight:700; color:#6c3483; font-size:1.1rem;">
                        LKR <%= String.format("%,.2f",
                            reservation.getRatePerNight() * reservation.getNumberOfNights()) %>
                    </span>
                </div>

                <!-- Cancellation Reason (if cancelled) -->
                <% if ("Cancelled".equals(status) &&
                       reservation.getCancelReason() != null &&
                       !reservation.getCancelReason().isEmpty()) { %>
                <div class="detail-row" style="margin-top:10px;">
                    <span class="detail-label" style="color:#e74c3c;">Cancel Reason</span>
                    <span class="detail-value" style="color:#e74c3c;">
                        <%= reservation.getCancelReason() %>
                    </span>
                </div>
                <% } %>
            </div>

        </div>

        <!-- ========== ACTION BUTTONS ========== -->
        <div class="data-card">
            <h3 style="margin-bottom:20px; color:#6c3483;">Actions</h3>
            <div class="btn-group">

                <!-- Back to List -->
                <a href="<%= request.getContextPath() %>/ReservationController?action=list"
                   class="btn-secondary">← Back to List</a>

                <!-- Generate Bill (Confirmed or Checked-Out) -->
                <% if ("Confirmed".equals(status) || "Checked-In".equals(status)) { %>
                <a href="<%= request.getContextPath() %>/BillingController?action=generate&reservationId=<%= reservation.getReservationId() %>"
                   class="btn-gold">🧾 Generate Bill</a>
                <% } %>

                <!-- Check-In Button (Confirmed only) -->
                <% if ("Confirmed".equals(status)) { %>
                <button type="button" class="btn-success"
                        onclick="updateStatus(<%= reservation.getReservationId() %>, 'Checked-In', '')">
                    ✅ Check-In Guest
                </button>
                <% } %>

                <!-- Check-Out Button (Checked-In only) -->
                <% if ("Checked-In".equals(status)) { %>
                <button type="button" class="btn-primary" style="width:auto;"
                        onclick="updateStatus(<%= reservation.getReservationId() %>, 'Checked-Out', '')">
                    🏁 Check-Out Guest
                </button>
                <% } %>

                <!-- Cancel Button (Confirmed only) -->
                <% if ("Confirmed".equals(status)) { %>
                <button type="button" class="btn-danger"
                        onclick="showCancelModal(<%= reservation.getReservationId() %>)">
                    ❌ Cancel Reservation
                </button>
                <% } %>

            </div>
        </div>

        <!-- ========== CANCEL MODAL ========== -->
        <div id="cancelModal"
             style="display:none; position:fixed; top:0; left:0; width:100%; height:100%;
                    background:rgba(0,0,0,0.5); z-index:999; align-items:center; justify-content:center;">
            <div style="background:#fff; border-radius:20px; padding:35px;
                        max-width:450px; width:90%; box-shadow:0 10px 40px rgba(0,0,0,0.2);">
                <h3 style="color:#e74c3c; margin-bottom:15px;">❌ Cancel Reservation</h3>
                <p style="color:#666; margin-bottom:20px;">
                    Please provide a reason for cancelling reservation
                    <strong><%= reservation.getReservationNumber() %></strong>.
                </p>
                <div class="form-group">
                    <label for="cancelReasonInput">Cancellation Reason *</label>
                    <textarea id="cancelReasonInput" rows="3"
                              placeholder="Enter reason for cancellation..."
                              style="width:100%;padding:10px;border:2px solid #e0e0e0;
                                     border-radius:12px;font-family:Poppins,sans-serif;
                                     resize:vertical;"></textarea>
                </div>
                <div class="btn-group" style="justify-content:flex-end;">
                    <button type="button" class="btn-secondary"
                            onclick="hideCancelModal()">Close</button>
                    <button type="button" class="btn-danger"
                            onclick="confirmCancel(<%= reservation.getReservationId() %>)">
                        Confirm Cancellation
                    </button>
                </div>
            </div>
        </div>

        <!-- Hidden Status Update Form -->
        <form id="statusUpdateForm"
              action="<%= request.getContextPath() %>/ReservationController"
              method="POST" style="display:none;">
            <input type="hidden" name="action" value="updateStatus">
            <input type="hidden" id="formReservationId" name="reservationId" value="">
            <input type="hidden" id="formNewStatus" name="newStatus" value="">
            <input type="hidden" id="formCancelReason" name="cancelReason" value="">
        </form>

        <% } else { %>
        <!-- Reservation not found -->
        <div class="data-card" style="text-align:center; padding:50px;">
            <p style="font-size:3rem;">🔍</p>
            <h3 style="color:#888; margin:15px 0;">Reservation Not Found</h3>
            <p style="color:#999; margin-bottom:20px;">
                The requested reservation could not be found.
            </p>
            <a href="<%= request.getContextPath() %>/ReservationController?action=list"
               class="btn-primary" style="width:auto; display:inline-block; padding:12px 30px;">
                Back to Reservation List
            </a>
        </div>
        <% } %>

        <footer class="app-footer">
            <p>&copy; 2026 Ocean View Resort. All rights reserved.</p>
            <p class="footer-address">Beach Road, Unawatuna, Galle, Sri Lanka | +94 91 223 4567</p>
        </footer>

    </main>
</div>

<!-- Event-Driven JavaScript -->
<script>
    /**
     * Shows the cancel confirmation modal.
     * @param {number} reservationId - ID of reservation to cancel
     */
    function showCancelModal(reservationId) {
        var modal = document.getElementById('cancelModal');
        modal.style.display = 'flex';
        document.getElementById('cancelReasonInput').focus();
    }

    /**
     * Hides the cancel confirmation modal.
     */
    function hideCancelModal() {
        document.getElementById('cancelModal').style.display = 'none';
        document.getElementById('cancelReasonInput').value = '';
    }

    /**
     * Confirms cancellation and submits the status update form.
     * @param {number} reservationId - ID of reservation to cancel
     */
    function confirmCancel(reservationId) {
        var reason = document.getElementById('cancelReasonInput').value.trim();

        if (!reason || reason.length < 3) {
            alert('Please enter a cancellation reason (minimum 3 characters).');
            document.getElementById('cancelReasonInput').focus();
            return;
        }

        if (confirm('Are you sure you want to cancel this reservation?')) {
            updateStatus(reservationId, 'Cancelled', reason);
        }
    }

    /**
     * Submits a status update for the reservation.
     * @param {number} reservationId - Reservation ID
     * @param {string} newStatus - New status value
     * @param {string} cancelReason - Reason (only for cancellations)
     */
    function updateStatus(reservationId, newStatus, cancelReason) {
        if (newStatus !== 'Cancelled') {
            var confirmMsg = '';
            if (newStatus === 'Checked-In') {
                confirmMsg = 'Confirm Check-In for this guest?';
            } else if (newStatus === 'Checked-Out') {
                confirmMsg = 'Confirm Check-Out for this guest?';
            }

            if (confirmMsg && !confirm(confirmMsg)) {
                return;
            }
        }

        document.getElementById('formReservationId').value = reservationId;
        document.getElementById('formNewStatus').value = newStatus;
        document.getElementById('formCancelReason').value = cancelReason;
        document.getElementById('statusUpdateForm').submit();
    }

    // Close modal when clicking outside
    document.addEventListener('DOMContentLoaded', function() {
        var modal = document.getElementById('cancelModal');
        if (modal) {
            modal.addEventListener('click', function(event) {
                if (event.target === modal) {
                    hideCancelModal();
                }
            });
        }
    });
</script>

</body>
</html>