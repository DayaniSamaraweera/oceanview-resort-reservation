<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.oceanview.model.SystemUser" %>
<%@ page import="com.oceanview.model.GuestReservation" %>
<%@ page import="java.util.List" %>
<%


    SystemUser loggedUser = (SystemUser) session.getAttribute("loggedInUser");
    if (loggedUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String userRole = loggedUser.getUserRole();
    String currentFilter = (String) request.getAttribute("currentFilter");
    if (currentFilter == null) currentFilter = "All";

    @SuppressWarnings("unchecked")
    List<GuestReservation> reservations =
            (List<GuestReservation>) request.getAttribute("reservations");

    Integer allCount       = (Integer) request.getAttribute("allCount");
    Integer confirmedCount = (Integer) request.getAttribute("confirmedCount");
    Integer checkedOutCount= (Integer) request.getAttribute("checkedOutCount");
    Integer cancelledCount = (Integer) request.getAttribute("cancelledCount");

    if (allCount == null) allCount = 0;
    if (confirmedCount == null) confirmedCount = 0;
    if (checkedOutCount == null) checkedOutCount = 0;
    if (cancelledCount == null) cancelledCount = 0;

    // Checked-In count = active - confirmed
    String successMsg = request.getParameter("success");
    String errorMsg   = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reservation List - Ocean View Resort</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/oceanview-styles.css">
</head>
<body>

<div class="app-layout">

    <!-- ========== LEFT SIDEBAR ========== -->
    <nav class="sidebar">
        <div class="sidebar-header">
            <img src="<%= request.getContextPath() %>/images/resortLogo.png" alt="Logo"
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

   
    <main class="main-content">

        <div class="page-header">
            <h1>Reservation List</h1>
            <p class="breadcrumb">Ocean View Resort &gt; Reservation List</p>
        </div>

        <!-- Success / Error messages -->
        <% if ("created".equals(successMsg)) { %>
            <div class="alert alert-success">✅ Reservation created successfully!</div>
        <% } else if ("updated".equals(successMsg)) { %>
            <div class="alert alert-success">✅ Reservation status updated successfully!</div>
        <% } else if ("update_failed".equals(errorMsg)) { %>
            <div class="alert alert-error">⚠️ Failed to update reservation status.</div>
        <% } %>

        <!-- Filter Tabs -->
        <div class="filter-tabs">
            <a href="<%= request.getContextPath() %>/ReservationController?action=list&status=All"
               class="filter-tab <%= "All".equals(currentFilter) ? "active-tab" : "" %>">
                All <span class="tab-count"><%= allCount %></span>
            </a>
            <a href="<%= request.getContextPath() %>/ReservationController?action=list&status=Confirmed"
               class="filter-tab <%= "Confirmed".equals(currentFilter) ? "active-tab" : "" %>">
                Confirmed <span class="tab-count"><%= confirmedCount %></span>
            </a>
            <a href="<%= request.getContextPath() %>/ReservationController?action=list&status=Checked-In"
               class="filter-tab <%= "Checked-In".equals(currentFilter) ? "active-tab" : "" %>">
                Checked-In
            </a>
            <a href="<%= request.getContextPath() %>/ReservationController?action=list&status=Checked-Out"
               class="filter-tab <%= "Checked-Out".equals(currentFilter) ? "active-tab" : "" %>">
                Checked-Out <span class="tab-count"><%= checkedOutCount %></span>
            </a>
            <a href="<%= request.getContextPath() %>/ReservationController?action=list&status=Cancelled"
               class="filter-tab <%= "Cancelled".equals(currentFilter) ? "active-tab" : "" %>">
                Cancelled <span class="tab-count"><%= cancelledCount %></span>
            </a>
        </div>

        <!-- Reservations Table -->
        <div class="data-card">
            <div class="card-header">
                <h3>
                    <%= "All".equals(currentFilter) ? "All Reservations" : currentFilter + " Reservations" %>
                    (<%= reservations != null ? reservations.size() : 0 %>)
                </h3>
                <a href="<%= request.getContextPath() %>/ReservationController?action=showAddForm"
                   class="btn-primary btn-sm" style="width:auto;">+ Add New</a>
            </div>

            <% if (reservations != null && !reservations.isEmpty()) { %>

                <!-- Search bar for client-side filtering -->
                <div style="margin-bottom:15px;">
                    <input type="text" id="tableSearchInput"
                           placeholder="🔍 Search by guest name or reservation number..."
                           style="width:100%;padding:10px 16px;border:2px solid #e0e0e0;
                                  border-radius:25px;font-family:Poppins,sans-serif;
                                  font-size:0.9rem;outline:none;">
                </div>

                <table class="data-table" id="reservationsTable">
                    <thead>
                        <tr>
                            <th>Reservation No.</th>
                            <th>Guest Name</th>
                            <th>Room Type</th>
                            <th>Check-In</th>
                            <th>Check-Out</th>
                            <th>Nights</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="reservationsTableBody">
                    <% for (GuestReservation res : reservations) {
                           String sClass = "";
                           String st = res.getReservationStatus();
                           if ("Confirmed".equals(st)) sClass = "status-confirmed";
                           else if ("Checked-In".equals(st)) sClass = "status-checked-in";
                           else if ("Checked-Out".equals(st)) sClass = "status-checked-out";
                           else if ("Cancelled".equals(st)) sClass = "status-cancelled";
                    %>
                        <tr>
                            <td><strong><%= res.getReservationNumber() %></strong></td>
                            <td><%= res.getGuestName() %></td>
                            <td><%= res.getRoomType() %></td>
                            <td><%= res.getCheckInDate() %></td>
                            <td><%= res.getCheckOutDate() %></td>
                            <td><%= res.getNumberOfNights() %></td>
                            <td>
                                <span class="status-badge <%= sClass %>">
                                    <%= st %>
                                </span>
                            </td>
                            <td>
                                <div class="btn-group">
                                    <a href="<%= request.getContextPath() %>/ReservationController?action=view&id=<%= res.getReservationId() %>"
                                       class="btn-secondary btn-sm">View</a>
                                    <% if ("Confirmed".equals(st)) { %>
                                    <a href="<%= request.getContextPath() %>/BillingController?action=generate&reservationId=<%= res.getReservationId() %>"
                                       class="btn-gold btn-sm">Bill</a>
                                    <% } %>
                                </div>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>

            <% } else { %>
                <div style="text-align:center;padding:40px;color:#999;">
                    <p style="font-size:2.5rem;">📋</p>
                    <p style="font-size:1.1rem;margin-bottom:15px;">
                        No <%= "All".equals(currentFilter) ? "" : currentFilter + " " %>reservations found.
                    </p>
                    <a href="<%= request.getContextPath() %>/ReservationController?action=showAddForm"
                       class="btn-primary" style="width:auto;display:inline-block;padding:12px 30px;">
                        + Add New Reservation
                    </a>
                </div>
            <% } %>
        </div>

        <footer class="app-footer">
            <p>&copy; 2026 Ocean View Resort. All rights reserved.</p>
            <p class="footer-address">Beach Road, Unawatuna, Galle, Sri Lanka | +94 91 223 4567</p>
        </footer>

    </main>
</div>

<!-- Event-Driven: Client-side table search -->
<script>
    document.addEventListener('DOMContentLoaded', function() {

        var searchInput = document.getElementById('tableSearchInput');

        if (searchInput) {
            // Listen for keyup events on the search input
            searchInput.addEventListener('keyup', function() {
                var searchTerm = this.value.toLowerCase().trim();
                var tableRows = document.querySelectorAll('#reservationsTableBody tr');

                tableRows.forEach(function(row) {
                    var rowText = row.textContent.toLowerCase();
                    if (rowText.includes(searchTerm)) {
                        row.style.display = '';
                    } else {
                        row.style.display = 'none';
                    }
                });
            });
        }
    });
</script>

</body>
</html>