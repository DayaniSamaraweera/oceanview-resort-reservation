<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.oceanview.model.SystemUser" %>
<%


    SystemUser loggedUser = (SystemUser) session.getAttribute("loggedInUser");
    if (loggedUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String userRole = loggedUser.getUserRole();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Help Section - Ocean View Resort</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/oceanview-styles.css">
</head>
<body>

<div class="app-layout">

    
    <nav class="sidebar">
        <div class="sidebar-header">
            <img src="<%= request.getContextPath() %>/images/resortLogo.png" alt="Logo"
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
            <a href="<%= request.getContextPath() %>/HelpController" class="active">
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
            <h1>Help Section</h1>
            <p class="breadcrumb">Ocean View Resort &gt; Help Section</p>
        </div>

        <!-- Intro Card -->
        <div class="data-card" style="margin-bottom:20px; text-align:center; padding:25px;">
            <p style="font-size:2.5rem;">📖</p>
            <h2 style="color:#6c3483; margin:10px 0 5px;">System User Guide</h2>
            <p style="color:#888;">
                Welcome to the Ocean View Resort Reservation System.
                This guide helps new staff members understand how to use the system effectively.
            </p>
        </div>

        <!-- Accordion FAQ Sections -->

        <!-- Section 1: Login -->
        <div class="accordion">
            <button class="accordion-header" onclick="toggleAccordion(this)">
                🔐 1. How to Log In to the System
                <span class="accordion-icon">▼</span>
            </button>
            <div class="accordion-body">
                <p><strong>Step 1:</strong> Open your web browser and navigate to the
                   Ocean View Resort Reservation System URL.</p>
                <p><strong>Step 2:</strong> Enter your <strong>Username</strong> and
                   <strong>Password</strong> provided by the Resort Manager (Admin).</p>
                <p><strong>Step 3:</strong> Click the <strong>Sign In</strong> button.</p>
                <p><strong>Step 4:</strong> If this is your first login, you will be
                   prompted to change your temporary credentials. Choose a new username
                   and secure password.</p>
                <br>
                <p><strong>Troubleshooting:</strong></p>
                <ul style="margin-left:20px; line-height:2;">
                    <li>If you see "Invalid credentials", check your username and password.</li>
                    <li>If your session expires (30 minutes of inactivity), log in again.</li>
                    <li>Contact the Resort Manager if you are locked out.</li>
                </ul>
                <br>
                <p><strong>💡 Tip:</strong> Use the "Remember Me" checkbox to save your
                   username for 7 days on the same device.</p>
            </div>
        </div>

        <!-- Section 2: Add Reservation -->
        <div class="accordion">
            <button class="accordion-header" onclick="toggleAccordion(this)">
                ➕ 2. How to Add a New Reservation
                <span class="accordion-icon">▼</span>
            </button>
            <div class="accordion-body">
                <p>The Add New Reservation feature uses a <strong>3-Step Form</strong>:</p>
                <br>
                <p><strong>Step 1 - Guest Details:</strong></p>
                <ul style="margin-left:20px; line-height:2;">
                    <li><strong>Guest Name:</strong> Full name (letters and spaces only, min 2 characters).</li>
                    <li><strong>Contact Number:</strong> 10-digit Sri Lankan number starting with 0 (e.g., 0771234567).</li>
                    <li><strong>Address:</strong> Residential address (minimum 5 characters).</li>
                    <li><strong>Email:</strong> Optional. Provide for automatic confirmation emails.</li>
                </ul>
                <br>
                <p><strong>Step 2 - Room Selection:</strong></p>
                <ul style="margin-left:20px; line-height:2;">
                    <li>Select a room type: Standard (LKR 5,500), Superior (LKR 8,500),
                        Premium (LKR 13,000), or Executive (LKR 22,000).</li>
                    <li>Choose a specific room from the dropdown that appears.</li>
                    <li>Rooms showing 0 availability cannot be selected.</li>
                </ul>
                <br>
                <p><strong>Step 3 - Dates &amp; Confirmation:</strong></p>
                <ul style="margin-left:20px; line-height:2;">
                    <li>Select <strong>Check-In Date</strong> (today or future only).</li>
                    <li>Select <strong>Check-Out Date</strong> (must be after check-in).</li>
                    <li>Review the Booking Summary showing nights and estimated total.</li>
                    <li>Click <strong>Confirm Reservation</strong> to complete.</li>
                </ul>
                <br>
                <p><strong>⚠️ Note:</strong> The system automatically detects booking
                   conflicts. If a room is already booked for the selected dates,
                   you will be notified to choose different dates or a different room.</p>
            </div>
        </div>

        <!-- Section 3: Display Reservation Details -->
        <div class="accordion">
            <button class="accordion-header" onclick="toggleAccordion(this)">
                🔍 3. How to Display Reservation Details
                <span class="accordion-icon">▼</span>
            </button>
            <div class="accordion-body">
                <p>There are two ways to view reservation details:</p>
                <br>
                <p><strong>Method 1 - From Reservation List:</strong></p>
                <ul style="margin-left:20px; line-height:2;">
                    <li>Click <strong>Reservation List</strong> in the sidebar.</li>
                    <li>Use the filter tabs (All, Confirmed, Checked-In, etc.) to narrow results.</li>
                    <li>Use the search bar to find by guest name or reservation number.</li>
                    <li>Click the <strong>View</strong> button next to any reservation.</li>
                </ul>
                <br>
                <p><strong>Method 2 - Find Reservation:</strong></p>
                <ul style="margin-left:20px; line-height:2;">
                    <li>Click <strong>Find Reservation</strong> in the sidebar.</li>
                    <li>Enter the reservation number (format: RES-YYYY-NNNNN).</li>
                    <li>As you type, live search results appear automatically.</li>
                    <li>Click <strong>View Full Details</strong> on the result.</li>
                </ul>
                <br>
                <p>The detail view shows: Guest information, Room details, Check-in/out dates,
                   Number of nights, Estimated total, and current Status.</p>
            </div>
        </div>

        <!-- Section 4: Generate Bill -->
        <div class="accordion">
            <button class="accordion-header" onclick="toggleAccordion(this)">
                🧾 4. How to Calculate and Print Bill
                <span class="accordion-icon">▼</span>
            </button>
            <div class="accordion-body">
                <p>To generate a bill for a guest:</p>
                <br>
                <p><strong>Method 1 - From Reservation Details:</strong></p>
                <ul style="margin-left:20px; line-height:2;">
                    <li>Open the reservation details page.</li>
                    <li>Click the <strong>🧾 Generate Bill</strong> button.</li>
                    <li>Review the billing summary.</li>
                    <li>Click <strong>Generate Bill Now</strong> to confirm.</li>
                </ul>
                <br>
                <p><strong>Method 2 - From Reservation List:</strong></p>
                <ul style="margin-left:20px; line-height:2;">
                    <li>Find the reservation in the list.</li>
                    <li>Click the <strong>Bill</strong> button (gold) in the Actions column.</li>
                </ul>
                <br>
                <p><strong>Bill Calculation:</strong></p>
                <ul style="margin-left:20px; line-height:2;">
                    <li>Subtotal = Rate Per Night × Number of Nights</li>
                    <li>Tax = 0% (Ocean View Resort policy)</li>
                    <li>Total = Subtotal + Tax</li>
                </ul>
                <br>
                <p><strong>To Print:</strong> Click <strong>🖨️ Print Invoice</strong>
                   on the bill view page. The sidebar and buttons are automatically
                   hidden when printing.</p>
                <br>
                <p><strong>⚠️ Note:</strong> A bill can only be generated once per
                   reservation. If a bill already exists, the existing bill will be shown.</p>
            </div>
        </div>

        <!-- Section 5: Status Management -->
        <div class="accordion">
            <button class="accordion-header" onclick="toggleAccordion(this)">
                🔄 5. How to Manage Reservation Status
                <span class="accordion-icon">▼</span>
            </button>
            <div class="accordion-body">
                <p>Reservation status follows this flow:</p>
                <br>
                <p style="text-align:center; font-size:0.9rem;">
                    <span class="status-badge status-confirmed">Confirmed</span>
                    &nbsp;→&nbsp;
                    <span class="status-badge status-checked-in">Checked-In</span>
                    &nbsp;→&nbsp;
                    <span class="status-badge status-checked-out">Checked-Out</span>
                </p>
                <p style="text-align:center; font-size:0.9rem; margin-top:5px;">
                    <span class="status-badge status-confirmed">Confirmed</span>
                    &nbsp;→&nbsp;
                    <span class="status-badge status-cancelled">Cancelled</span>
                    (with reason)
                </p>
                <br>
                <p>To change status, open the reservation and use the action buttons:</p>
                <ul style="margin-left:20px; line-height:2;">
                    <li><strong>✅ Check-In Guest:</strong> Guest has arrived.</li>
                    <li><strong>🏁 Check-Out Guest:</strong> Guest has departed.</li>
                    <li><strong>❌ Cancel Reservation:</strong> Must provide a reason.</li>
                </ul>
                <br>
                <p><strong>Note:</strong> When a room is checked out or cancelled,
                   it automatically becomes available for new bookings.</p>
            </div>
        </div>

        <!-- Section 6: Admin Functions (Admin only) -->
        <% if ("ADMIN".equals(userRole)) { %>
        <div class="accordion">
            <button class="accordion-header" onclick="toggleAccordion(this)">
                👥 6. Staff Management (Admin Only)
                <span class="accordion-icon">▼</span>
            </button>
            <div class="accordion-body">
                <p>As Resort Manager (Admin), you can manage staff accounts:</p>
                <br>
                <p><strong>To Add New Staff:</strong></p>
                <ul style="margin-left:20px; line-height:2;">
                    <li>Go to <strong>Staff Management</strong> in the sidebar.</li>
                    <li>Fill in the staff member's details and temporary credentials.</li>
                    <li>Click <strong>Create Account</strong>.</li>
                    <li>The staff member will be prompted to change their credentials
                        on first login.</li>
                </ul>
                <br>
                <p><strong>To Deactivate Staff:</strong></p>
                <ul style="margin-left:20px; line-height:2;">
                    <li>Find the staff member in the list.</li>
                    <li>Click <strong>Deactivate</strong>.</li>
                    <li>The account is deactivated (not deleted) for record keeping.</li>
                </ul>
                <br>
                <p><strong>Reports:</strong> View decision-making reports including
                   occupancy rate, revenue by room type, and reservation status
                   breakdown. Export data as CSV for offline analysis.</p>
                <br>
                <p><strong>Activity Log:</strong> View all system actions performed
                   by all users for security auditing.</p>
            </div>
        </div>
        <% } %>

        <!-- Section 7: Exit -->
        <div class="accordion">
            <button class="accordion-header" onclick="toggleAccordion(this)">
                🚪 7. How to Exit the System
                <span class="accordion-icon">▼</span>
            </button>
            <div class="accordion-body">
                <p>To safely exit the system:</p>
                <br>
                <ul style="margin-left:20px; line-height:2;">
                    <li>Click <strong>🚪 Sign Out</strong> at the bottom of the sidebar.</li>
                    <li>You will be redirected to the login page.</li>
                    <li>Your session is invalidated and all data is secured.</li>
                </ul>
                <br>
                <p><strong>Session Timeout:</strong> The system automatically logs you
                   out after <strong>30 minutes of inactivity</strong> for security.</p>
                <br>
                <p><strong>⚠️ Important:</strong> Always sign out when leaving the
                   workstation, especially on shared computers.</p>
            </div>
        </div>

        <!-- Contact Info -->
        <div class="data-card" style="margin-top:20px; text-align:center;">
            <h3 style="color:#6c3483; margin-bottom:10px;">Need More Help?</h3>
            <p style="color:#888;">Contact the Resort Manager or IT Support</p>
            <p style="color:#8e44ad; font-weight:600; margin-top:8px;">
                📧 admin@oceanviewresort.lk &nbsp;|&nbsp;
                📞 +94 91 223 4567
            </p>
        </div>

        <footer class="app-footer">
            <p>&copy; 2026 Ocean View Resort. All rights reserved.</p>
            <p class="footer-address">
                Beach Road, Unawatuna, Galle, Sri Lanka | +94 91 223 4567
            </p>
        </footer>

    </main>
</div>

<!-- Event-Driven: Accordion JavaScript -->
<script>
    /**
     * Toggles an accordion section open or closed.
     * Event-Driven: attached to button click events.
     * @param {HTMLElement} headerButton - The clicked accordion header
     */
    function toggleAccordion(headerButton) {
        var accordionBody =
            headerButton.nextElementSibling;
        var isOpen =
            headerButton.classList.contains('open');

        // Close all open accordions
        document.querySelectorAll('.accordion-header.open')
            .forEach(function(openHeader) {
                openHeader.classList.remove('open');
                openHeader.nextElementSibling
                          .classList.remove('open');
            });

        // Open clicked accordion if it was closed
        if (!isOpen) {
            headerButton.classList.add('open');
            accordionBody.classList.add('open');
        }
    }

    // Open first accordion by default on page load
    document.addEventListener('DOMContentLoaded', function() {
        var firstHeader =
            document.querySelector('.accordion-header');
        if (firstHeader) {
            toggleAccordion(firstHeader);
        }
    });
</script>

</body>
</html>