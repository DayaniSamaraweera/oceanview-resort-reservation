<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.oceanview.model.SystemUser" %>
<%@ page import="com.oceanview.model.GuestReservation" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    /*
     * dashboard.jsp - Main Dashboard Page
     * 
     * Requirement Traceability: Provides system overview and
     * quick navigation to all core features.
     * 
     * RBAC Layout:
     * - ADMIN: Image Slider + 4 Stats Cards + 2x2 Room Availability
     *          Grid + Recent Bookings Table (all bookings)
     * - RECEPTIONIST: Welcome message + 2x2 Quick Action Buttons
     *          + Simple Availability Table + Own Recent Bookings
     * 
     * Both roles share the Left Sidebar navigation with role-based
     * menu items (Staff Management and Reports hidden for Receptionist).
     * 
     * Author: Dayani Samaraweera
     */

    // Get session data
    SystemUser loggedUser = (SystemUser) session.getAttribute("loggedInUser");
    if (loggedUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String userRole = loggedUser.getUserRole();
    String fullName = loggedUser.getFullName();

    // Get dashboard data from controller
    Integer totalRooms = (Integer) request.getAttribute("totalRooms");
    Integer activeBookings = (Integer) request.getAttribute("activeBookings");
    Integer availableRooms = (Integer) request.getAttribute("availableRooms");
    Double totalRevenue = (Double) request.getAttribute("totalRevenue");
    Double occupancyRate = (Double) request.getAttribute("occupancyRate");

    // Null safety
    if (totalRooms == null) totalRooms = 0;
    if (activeBookings == null) activeBookings = 0;
    if (availableRooms == null) availableRooms = 0;
    if (totalRevenue == null) totalRevenue = 0.0;
    if (occupancyRate == null) occupancyRate = 0.0;

    @SuppressWarnings("unchecked")
    Map<String, Integer> roomAvailability = 
            (Map<String, Integer>) request.getAttribute("roomAvailability");

    @SuppressWarnings("unchecked")
    List<GuestReservation> recentBookings = 
            (List<GuestReservation>) request.getAttribute("recentBookings");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Ocean View Resort</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/oceanview-styles.css">
</head>
<body>

    <div class="app-layout">

        <!-- ========== LEFT SIDEBAR ========== -->
        <nav class="sidebar">
            <div class="sidebar-header">
                <img src="https://i.imgur.com/YOUR_LOGO_ID.png" 
     alt="Ocean View Resort Logo" 
     style="width: 60px; height: 60px; border-radius: 50%; margin-bottom: 10px;">
                <p>Resort &amp; Spa, Galle</p>
            </div>

            <div class="sidebar-nav">
                <a href="<%= request.getContextPath() %>/DashboardController" class="active">
                    <span class="nav-icon">🏠</span>
                    <span>Home</span>
                </a>
                <a href="<%= request.getContextPath() %>/ReservationController?action=showAddForm">
                    <span class="nav-icon">➕</span>
                    <span>Add New Reservation</span>
                </a>
                <a href="<%= request.getContextPath() %>/ReservationController?action=list">
                    <span class="nav-icon">📋</span>
                    <span>Reservation List</span>
                </a>
                <a href="<%= request.getContextPath() %>/ReservationController?action=search">
                    <span class="nav-icon">🔍</span>
                    <span>Find Reservation</span>
                </a>
                <a href="<%= request.getContextPath() %>/BillingController?action=list">
                    <span class="nav-icon">🧾</span>
                    <span>Generate Bill</span>
                </a>

                <% if ("ADMIN".equals(userRole)) { %>
                    <div class="nav-divider"></div>
                    <a href="<%= request.getContextPath() %>/ReportController">
                        <span class="nav-icon">📊</span>
                        <span>Reports</span>
                    </a>
                    <a href="<%= request.getContextPath() %>/StaffManagement">
                        <span class="nav-icon">👥</span>
                        <span>Staff Management</span>
                    </a>
                    <a href="<%= request.getContextPath() %>/AuditLog">
                        <span class="nav-icon">📝</span>
                        <span>Activity Log</span>
                    </a>
                <% } %>

                <div class="nav-divider"></div>
                <a href="<%= request.getContextPath() %>/HelpController">
                    <span class="nav-icon">❓</span>
                    <span>Help Section</span>
                </a>
            </div>

            <div class="sidebar-footer">
                <div class="user-info">
                    Signed in as <strong><%= fullName %></strong>
                    <br><small><%= userRole %></small>
                </div>
                <a href="<%= request.getContextPath() %>/LoginController?action=logout" 
                   class="signout-btn">🚪 Sign Out</a>
            </div>
        </nav>

        <!-- ========== MAIN CONTENT ========== -->
        <main class="main-content">

            <div class="page-header">
                <h1>Dashboard</h1>
                <p class="breadcrumb">
                    Ocean View Resort &gt; 
                    <%= "ADMIN".equals(userRole) ? "Admin Dashboard" : "Receptionist Dashboard" %>
                </p>
            </div>

            <% if ("ADMIN".equals(userRole)) { %>
                <!-- ==================== ADMIN DASHBOARD ==================== -->

                <!-- Image Slider -->
                <div class="image-slider" id="resortSlider">
                    <div class="slide active-slide">
                        <img src="https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=900" 
                             alt="Ocean View Resort">
                        <div class="slide-overlay">
                            <h3>Welcome to Ocean View Resort</h3>
                            <p>Luxury beachside hospitality in Galle</p>
                        </div>
                    </div>
                    <div class="slide">
                        <img src="https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=900" 
                             alt="Resort Beach">
                        <div class="slide-overlay">
                            <h3>Stunning Ocean Views</h3>
                            <p>14 rooms across 4 luxury categories</p>
                        </div>
                    </div>
                    <div class="slide">
                        <img src="https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=900" 
                             alt="Hotel Room">
                        <div class="slide-overlay">
                            <h3>Premium Accommodation</h3>
                            <p>From Standard to Executive Suites</p>
                        </div>
                    </div>
                </div>

                <!-- 4 Stats Cards -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-icon">🏨</div>
                        <div class="stat-value"><%= totalRooms %></div>
                        <div class="stat-label">Total Rooms</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">📅</div>
                        <div class="stat-value"><%= activeBookings %></div>
                        <div class="stat-label">Active Bookings</div>
                    </div>
                    <div class="stat-card green-border">
                        <div class="stat-icon">✅</div>
                        <div class="stat-value"><%= availableRooms %></div>
                        <div class="stat-label">Available Rooms</div>
                    </div>
                    <div class="stat-card gold-border">
                        <div class="stat-icon">💰</div>
                        <div class="stat-value">LKR <%= String.format("%,.2f", totalRevenue) %></div>
                        <div class="stat-label">Total Revenue</div>
                    </div>
                </div>

                <!-- 2x2 Room Availability Grid -->
                <% if (roomAvailability != null) { %>
                <div class="data-card">
                    <div class="card-header">
                        <h3>Room Availability by Type</h3>
                    </div>
                    <div class="room-grid">
                        <div class="room-type-card">
                            <div>
                                <div class="room-type-name">Standard</div>
                                <div class="room-type-rate">LKR 5,500 / night</div>
                            </div>
                            <div class="room-available-count">
                                <%= roomAvailability.getOrDefault("Standard", 0) %>
                            </div>
                        </div>
                        <div class="room-type-card">
                            <div>
                                <div class="room-type-name">Superior</div>
                                <div class="room-type-rate">LKR 8,500 / night</div>
                            </div>
                            <div class="room-available-count">
                                <%= roomAvailability.getOrDefault("Superior", 0) %>
                            </div>
                        </div>
                        <div class="room-type-card">
                            <div>
                                <div class="room-type-name">Premium</div>
                                <div class="room-type-rate">LKR 13,000 / night</div>
                            </div>
                            <div class="room-available-count">
                                <%= roomAvailability.getOrDefault("Premium", 0) %>
                            </div>
                        </div>
                        <div class="room-type-card">
                            <div>
                                <div class="room-type-name">Executive</div>
                                <div class="room-type-rate">LKR 22,000 / night</div>
                            </div>
                            <div class="room-available-count">
                                <%= roomAvailability.getOrDefault("Executive", 0) %>
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>

            <% } else { %>
                <!-- ==================== RECEPTIONIST DASHBOARD ==================== -->

                <!-- Welcome Message -->
                <div class="data-card" style="text-align: center; padding: 30px;">
                    <h2 style="color: #6c3483;">Welcome, <%= fullName %>! 👋</h2>
                    <p style="color: #888; margin-top: 5px;">
                        What would you like to do today?
                    </p>
                </div>

<!-- 2x2 Quick Action Buttons -->
                <div class="quick-actions">
                    <a href="<%= request.getContextPath() %>/ReservationController?action=showAddForm" 
                       class="quick-action-btn">
                        <span class="action-icon">➕</span>
                        <span class="action-label">Add New Reservation</span>
                    </a>
                    <a href="<%= request.getContextPath() %>/ReservationController?action=search" 
                       class="quick-action-btn">
                        <span class="action-icon">🔍</span>
                        <span class="action-label">Find Reservation</span>
                    </a>
                    <a href="<%= request.getContextPath() %>/BillingController?action=list" 
                       class="quick-action-btn">
                        <span class="action-icon">🧾</span>
                        <span class="action-label">Generate Bill</span>
                    </a>
                    <a href="<%= request.getContextPath() %>/HelpController" 
                       class="quick-action-btn">
                        <span class="action-icon">❓</span>
                        <span class="action-label">Help Section</span>
                    </a>
                </div>

                <!-- Simple Room Availability -->
                <% if (roomAvailability != null) { %>
                <div class="data-card">
                    <div class="card-header">
                        <h3>Current Room Availability</h3>
                    </div>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Room Type</th>
                                <th>Rate Per Night</th>
                                <th>Available</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Standard</td>
                                <td>LKR 5,500.00</td>
                                <td><span class="status-badge status-confirmed">
                                    <%= roomAvailability.getOrDefault("Standard", 0) %> rooms
                                </span></td>
                            </tr>
                            <tr>
                                <td>Superior</td>
                                <td>LKR 8,500.00</td>
                                <td><span class="status-badge status-confirmed">
                                    <%= roomAvailability.getOrDefault("Superior", 0) %> rooms
                                </span></td>
                            </tr>
                            <tr>
                                <td>Premium</td>
                                <td>LKR 13,000.00</td>
                                <td><span class="status-badge status-confirmed">
                                    <%= roomAvailability.getOrDefault("Premium", 0) %> rooms
                                </span></td>
                            </tr>
                            <tr>
                                <td>Executive</td>
                                <td>LKR 22,000.00</td>
                                <td><span class="status-badge status-confirmed">
                                    <%= roomAvailability.getOrDefault("Executive", 0) %> rooms
                                </span></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <% } %>

            <% } %>

            <!-- ========== RECENT BOOKINGS TABLE (Both Roles) ========== -->
            <div class="data-card">
                <div class="card-header">
                    <h3>
                        <%= "ADMIN".equals(userRole) ? "Recent Bookings" : "My Recent Bookings" %>
                    </h3>
                    <a href="<%= request.getContextPath() %>/ReservationController?action=list" 
                       class="btn-secondary btn-sm">View All</a>
                </div>

                <% if (recentBookings != null && !recentBookings.isEmpty()) { %>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Reservation No.</th>
                            <th>Guest Name</th>
                            <th>Room Type</th>
                            <th>Check-In</th>
                            <th>Check-Out</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (GuestReservation booking : recentBookings) { 
                            String statusClass = "";
                            String currentStatus = booking.getReservationStatus();
                            if ("Confirmed".equals(currentStatus)) statusClass = "status-confirmed";
                            else if ("Checked-In".equals(currentStatus)) statusClass = "status-checked-in";
                            else if ("Checked-Out".equals(currentStatus)) statusClass = "status-checked-out";
                            else if ("Cancelled".equals(currentStatus)) statusClass = "status-cancelled";
                        %>
                        <tr>
                            <td><strong><%= booking.getReservationNumber() %></strong></td>
                            <td><%= booking.getGuestName() %></td>
                            <td><%= booking.getRoomType() %></td>
                            <td><%= booking.getCheckInDate() %></td>
                            <td><%= booking.getCheckOutDate() %></td>
                            <td>
                                <span class="status-badge <%= statusClass %>">
                                    <%= currentStatus %>
                                </span>
                            </td>
                            <td>
                                <a href="<%= request.getContextPath() %>/ReservationController?action=view&id=<%= booking.getReservationId() %>" 
                                   class="btn-secondary btn-sm">View</a>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <% } else { %>
                <div style="text-align: center; padding: 30px; color: #999;">
                    <p style="font-size: 2rem;">📋</p>
                    <p>No recent bookings found.</p>
                    <a href="<%= request.getContextPath() %>/ReservationController?action=showAddForm" 
                       class="btn-primary" style="margin-top: 15px; display: inline-block; width: auto; padding: 10px 25px;">
                        Add New Reservation
                    </a>
                </div>
                <% } %>
            </div>

            <!-- ========== FOOTER ========== -->
            <footer class="app-footer">
                <p>&copy; 2026 Ocean View Resort. All rights reserved.</p>
                <p class="footer-address">Beach Road, Unawatuna, Galle, Sri Lanka | +94 91 223 4567</p>
            </footer>

        </main>

    </div>

    <!-- Event-Driven: Image Slider Auto-rotation (Admin only) -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {

            // Image slider logic (only runs if slider exists - Admin dashboard)
            var sliderElement = document.getElementById('resortSlider');
            
            if (sliderElement) {
                var allSlides = sliderElement.querySelectorAll('.slide');
                var currentSlideIndex = 0;

                function rotateSlides() {
                    // Remove active class from current slide
                    allSlides[currentSlideIndex].classList.remove('active-slide');
                    
                    // Move to next slide (loop back to first)
                    currentSlideIndex = (currentSlideIndex + 1) % allSlides.length;
                    
                    // Add active class to new slide
                    allSlides[currentSlideIndex].classList.add('active-slide');
                }

                // Auto-rotate every 4 seconds
                setInterval(rotateSlides, 4000);
            }
        });
    </script>

</body>
</html>