<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.oceanview.model.SystemUser" %>
<%
    /*
     * searchReservation.jsp - Find Reservation Page
     *
     * Layout: Centered search card with large search icon,
     * styled input, and recent searches hint.
     *
     * Uses AJAX (fetch API) to search via REST API endpoint
     * for live search results without page reload.
     *
     * Author: Dayani Samaraweera
     */

    SystemUser loggedUser = (SystemUser) session.getAttribute("loggedInUser");
    if (loggedUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String userRole = loggedUser.getUserRole();
    String searchError  = (String) request.getAttribute("searchError");
    String searchNumber = (String) request.getAttribute("searchNumber");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Find Reservation - Ocean View Resort</title>
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
            <a href="<%= request.getContextPath() %>/ReservationController?action=list">
                <span class="nav-icon">📋</span><span>Reservation List</span></a>
            <a href="<%= request.getContextPath() %>/ReservationController?action=search" class="active">
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
            <h1>Find Reservation</h1>
            <p class="breadcrumb">Ocean View Resort &gt; Find Reservation</p>
        </div>

        <!-- Main Search Card -->
        <div class="search-card">

            <div class="search-icon">🔍</div>
            <h2>Search Reservation</h2>
            <p style="color:#888; margin-bottom:25px; font-size:0.9rem;">
                Enter a reservation number to find complete booking details
            </p>

            <!-- Error Message -->
            <% if (searchError != null && !searchError.isEmpty()) { %>
            <div class="alert alert-error" style="text-align:left;">
                ⚠️ <%= searchError %>
            </div>
            <% } %>

            <!-- Search Form (traditional form submit) -->
            <form action="<%= request.getContextPath() %>/ReservationController"
                  method="GET" id="searchForm">
                <input type="hidden" name="action" value="searchResult">

                <div class="search-input-wrapper">
                    <input type="text" id="searchInput" name="reservationNumber"
                           placeholder="e.g. RES-2026-00001"
                           value="<%= searchNumber != null ? searchNumber : "" %>"
                           autocomplete="off"
                           style="text-transform:uppercase;">
                    <button type="submit" class="btn-primary"
                            style="width:auto; padding:12px 25px;">Search</button>
                </div>
            </form>

            <p class="search-hint">
                💡 Format: RES-YYYY-NNNNN (e.g., RES-2026-00001)
            </p>

            <!-- Live Search Results (via REST API) -->
            <div id="liveSearchResults"
                 style="display:none; margin-top:20px; text-align:left;">
            </div>

        </div>

        <!-- Quick Links -->
        <div style="max-width:600px; margin:20px auto;">
            <div class="data-card">
                <h3 style="margin-bottom:15px; color:#6c3483;">Quick Actions</h3>
                <div class="btn-group">
                    <a href="<%= request.getContextPath() %>/ReservationController?action=list"
                       class="btn-secondary">📋 View All Reservations</a>
                    <a href="<%= request.getContextPath() %>/ReservationController?action=showAddForm"
                       class="btn-primary" style="width:auto;">➕ Add New Reservation</a>
                </div>
            </div>
        </div>

        <footer class="app-footer">
            <p>&copy; 2026 Ocean View Resort. All rights reserved.</p>
            <p class="footer-address">Beach Road, Unawatuna, Galle, Sri Lanka | +94 91 223 4567</p>
        </footer>

    </main>
</div>

<!-- Event-Driven: Live search via REST API -->
<script>
    document.addEventListener('DOMContentLoaded', function() {

        var searchInput = document.getElementById('searchInput');
        var liveResults = document.getElementById('liveSearchResults');
        var contextPath = '<%= request.getContextPath() %>';
        var searchTimer = null;

        // Auto-uppercase the input as user types
        searchInput.addEventListener('input', function() {
            this.value = this.value.toUpperCase();

            var searchTerm = this.value.trim();

            // Clear previous timer
            clearTimeout(searchTimer);

            // Hide results if input is too short
            if (searchTerm.length < 4) {
                liveResults.style.display = 'none';
                return;
            }

            // Debounce: wait 400ms after typing stops
            searchTimer = setTimeout(function() {
                performLiveSearch(searchTerm);
            }, 400);
        });

        /**
         * Performs a live search via the REST API endpoint.
         * Uses fetch() for asynchronous JSON data retrieval.
         * @param {string} searchTerm - Reservation number to search
         */
        function performLiveSearch(searchTerm) {
            liveResults.style.display = 'block';
            liveResults.innerHTML = '<p style="color:#888; font-size:0.9rem;">🔄 Searching...</p>';

            // Call REST API endpoint
            fetch(contextPath + '/api/reservations?number=' + encodeURIComponent(searchTerm))
                .then(function(response) {
                    return response.json();
                })
                .then(function(data) {
                    if (data.status === 'success' && data.reservation) {
                        var res = data.reservation;
                        var statusColor = getStatusColor(res.reservationStatus);

                        liveResults.innerHTML =
                            '<div style="background:#faf5ff; border-radius:15px; padding:20px; ' +
                            'border-left:4px solid #8e44ad;">' +
                            '<div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">' +
                            '<h4 style="color:#6c3483; margin:0;">' + res.reservationNumber + '</h4>' +
                            '<span style="padding:4px 14px; border-radius:25px; font-size:0.8rem; ' +
                            'background:' + statusColor.bg + '; color:' + statusColor.text + '; font-weight:600;">' +
                            res.reservationStatus + '</span>' +
                            '</div>' +
                            '<div style="display:grid; grid-template-columns:1fr 1fr; gap:8px; font-size:0.88rem;">' +
                            '<div><span style="color:#888;">Guest:</span> <strong>' + res.guestName + '</strong></div>' +
                            '<div><span style="color:#888;">Room:</span> <strong>' + res.roomType + '</strong></div>' +
                            '<div><span style="color:#888;">Check-In:</span> ' + res.checkInDate + '</div>' +
                            '<div><span style="color:#888;">Check-Out:</span> ' + res.checkOutDate + '</div>' +
                            '<div><span style="color:#888;">Nights:</span> ' + res.numberOfNights + '</div>' +
                            '<div><span style="color:#888;">Contact:</span> ' + res.contactNumber + '</div>' +
                            '</div>' +
                            '<div style="margin-top:15px;">' +
                            '<a href="' + contextPath + '/ReservationController?action=view&id=' +
                            res.reservationId + '" class="btn-primary" ' +
                            'style="width:auto; display:inline-block; padding:10px 25px;">' +
                            'View Full Details →</a>' +
                            '</div>' +
                            '</div>';
                    } else {
                        liveResults.innerHTML =
                            '<div class="alert alert-warning">' +
                            '🔍 No reservation found with number: <strong>' + searchTerm + '</strong>' +
                            '</div>';
                    }
                })
                .catch(function(error) {
                    liveResults.innerHTML =
                        '<div class="alert alert-error">' +
                        '⚠️ Search error. Please try again or use the search button.' +
                        '</div>';
                });
        }

        /**
         * Returns color scheme for a reservation status badge.
         * @param {string} status - The reservation status
         * @returns {object} Object with bg and text color strings
         */
        function getStatusColor(status) {
            var colors = {
                'Confirmed':   { bg: '#e8f8f0', text: '#27ae60' },
                'Checked-In':  { bg: '#eaf2f8', text: '#2980b9' },
                'Checked-Out': { bg: '#f4f6f9', text: '#7f8c8d' },
                'Cancelled':   { bg: '#fce8e6', text: '#e74c3c' }
            };
            return colors[status] || { bg: '#f4f6f9', text: '#333' };
        }

        // Focus search input on page load
        searchInput.focus();
    });
</script>

</body>
</html>