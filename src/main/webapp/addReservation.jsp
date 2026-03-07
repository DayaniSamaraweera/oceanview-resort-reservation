<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.oceanview.model.SystemUser" %>
<%@ page import="com.oceanview.model.ResortRoom" %>
<%@ page import="java.util.List" %>
<%

    SystemUser loggedUser = (SystemUser) session.getAttribute("loggedInUser");
    if (loggedUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String userRole = loggedUser.getUserRole();
    String errorMessage = (String) request.getAttribute("errorMessage");

    // Room lists loaded from DashboardController
    @SuppressWarnings("unchecked")
    List<ResortRoom> standardRooms =
            (List<ResortRoom>) request.getAttribute("standardRooms");
    @SuppressWarnings("unchecked")
    List<ResortRoom> superiorRooms =
            (List<ResortRoom>) request.getAttribute("superiorRooms");
    @SuppressWarnings("unchecked")
    List<ResortRoom> premiumRooms =
            (List<ResortRoom>) request.getAttribute("premiumRooms");
    @SuppressWarnings("unchecked")
    List<ResortRoom> executiveRooms =
            (List<ResortRoom>) request.getAttribute("executiveRooms");

    int standardCount = (standardRooms != null) ? standardRooms.size() : 0;
    int superiorCount = (superiorRooms != null) ? superiorRooms.size() : 0;
    int premiumCount  = (premiumRooms  != null) ? premiumRooms.size()  : 0;
    int executiveCount= (executiveRooms!= null) ? executiveRooms.size(): 0;

    // Today's date for min date validation
    java.time.LocalDate today = java.time.LocalDate.now();
    String todayStr = today.toString();
    String tomorrowStr = today.plusDays(1).toString();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New Reservation - Ocean View Resort</title>
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
            <a href="<%= request.getContextPath() %>/ReservationController?action=showAddForm" class="active">
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
            <h1>Add New Reservation</h1>
            <p class="breadcrumb">Ocean View Resort &gt; Add New Reservation</p>
        </div>

        <!-- Error Message -->
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="alert alert-error">⚠️ <%= errorMessage %></div>
        <% } %>

        <!-- Step Indicator -->
        <div class="step-indicator">
            <div class="step-dot active-step" id="dot1">1</div>
            <div class="step-line" id="line1"></div>
            <div class="step-dot" id="dot2">2</div>
            <div class="step-line" id="line2"></div>
            <div class="step-dot" id="dot3">3</div>
        </div>

        <!-- FORM -->
        <form action="<%= request.getContextPath() %>/ReservationController"
              method="POST" id="reservationForm">
            <input type="hidden" name="action" value="add">
            <input type="hidden" id="selectedRoomId" name="roomId" value="">
            <input type="hidden" id="selectedRoomType" name="roomType" value="">

            <!-- ===== STEP 1: Customer Details ===== -->
            <div class="data-card step-content active-content" id="step1">
                <h3 class="step-title">Step 1: Guest Details</h3>

                <div class="form-row">
                    <div class="form-group">
                        <label for="guestName">Guest Name *</label>
                        <input type="text" id="guestName" name="guestName"
                               placeholder="Full name (letters only)"
pattern="^[a-zA-Z\s]{2,}" title="Minimum 2 characters, letters only" required>                    </div>
                    <div class="form-group">
                        <label for="contactNumber">Contact Number *</label>
                        <input type="text" id="contactNumber" name="contactNumber"
                               placeholder="0771234567 (10 digits)"
                               pattern="^0\d{9}$" maxlength="10" required>
                    </div>
                </div>

                <div class="form-group">
    <label for="address">Address *</label>
    <textarea id="address" name="address" rows="3"
              placeholder="Residential address (min 5 characters)"
              required
              oninput="if(this.value.length < 5) this.setCustomValidity('Minimum 5 characters required'); else this.setCustomValidity('');"
              style="resize:vertical;"></textarea>
</div>

                <div class="form-group">
                    <label for="guestEmail">Email Address (Optional)</label>
                    <input type="email" id="guestEmail" name="guestEmail"
                           placeholder="guest@example.com (for email notifications)">
                </div>

                <div class="btn-group" style="justify-content: flex-end;">
                    <button type="button" class="btn-primary"
                            onclick="goToStep(2)"
                            style="width:auto;">Next: Select Room →</button>
                </div>
            </div>

            <!-- ===== STEP 2: Room Selection ===== -->
            <div class="data-card step-content" id="step2">
                <h3 class="step-title">Step 2: Select Room Type</h3>

                <div class="room-selection-grid">

                    <!-- Standard Room -->
                    <div class="room-option" id="opt-Standard"
                         onclick="selectRoom('Standard', 5500, <%= standardCount %>)">
                       <img src="<%= request.getContextPath() %>/images/room-std.jpeg"
                         alt="Standard Room"
                             style="width:100%;height:130px;object-fit:cover;border-radius:12px;margin-bottom:10px;">
                        <div class="room-option-name">Standard Room</div>
                        <div class="room-option-rate">
                            LKR 5,500 <span>/ night</span>
                        </div>
                        <div class="room-option-avail">
                            <%= standardCount %> room<%= standardCount != 1 ? "s" : "" %> available
                        </div>
                    </div>

                    <!-- Superior Room -->
                    <div class="room-option" id="opt-Superior"
                         onclick="selectRoom('Superior', 8500, <%= superiorCount %>)">
                       <img src="<%= request.getContextPath() %>/images/room-sup.jpeg"
                         alt="Superior Room"
                             style="width:100%;height:130px;object-fit:cover;border-radius:12px;margin-bottom:10px;">
                        <div class="room-option-name">Superior Room</div>
                        <div class="room-option-rate">
                            LKR 8,500 <span>/ night</span>
                        </div>
                        <div class="room-option-avail">
                            <%= superiorCount %> room<%= superiorCount != 1 ? "s" : "" %> available
                        </div>
                    </div>

                    <!-- Premium Room -->
                    <div class="room-option" id="opt-Premium"
                         onclick="selectRoom('Premium', 13000, <%= premiumCount %>)">
                        <img src="<%= request.getContextPath() %>/images/room-pre.jpeg"
                         alt="Premium Room"
                             style="width:100%;height:130px;object-fit:cover;border-radius:12px;margin-bottom:10px;">
                        <div class="room-option-name">Premium Room</div>
                        <div class="room-option-rate">
                            LKR 13,000 <span>/ night</span>
                        </div>
                        <div class="room-option-avail">
                            <%= premiumCount %> room<%= premiumCount != 1 ? "s" : "" %> available
                        </div>
                    </div>

                    <!-- Executive Room -->
                    <div class="room-option" id="opt-Executive"
                         onclick="selectRoom('Executive', 22000, <%= executiveCount %>)">
                        <img src="<%= request.getContextPath() %>/images/room-exe.jpeg"
                         alt="Executive Suite"
                             style="width:100%;height:130px;object-fit:cover;border-radius:12px;margin-bottom:10px;">
                        <div class="room-option-name">Executive Suite</div>
                        <div class="room-option-rate">
                            LKR 22,000 <span>/ night</span>
                        </div>
                        <div class="room-option-avail">
                            <%= executiveCount %> room<%= executiveCount != 1 ? "s" : "" %> available
                        </div>
                    </div>

                </div>

                <!-- Room select hidden dropdown -->
                <div class="form-group" id="roomSelectWrapper"
                     style="display:none; margin-top:20px;">
                    <label id="roomSelectLabel">Select Specific Room</label>
                    <select id="specificRoomSelect"
                            onchange="document.getElementById('selectedRoomId').value = this.value;">
                        <option value="">-- Choose a room --</option>
                        <% if (standardRooms != null) {
                               for (ResortRoom r : standardRooms) { %>
                        <option value="<%= r.getRoomId() %>"
                                class="Standard-option">
                            Room <%= r.getRoomNumber() %> - Floor <%= r.getFloorNumber() %>
                        </option>
                        <% }} %>
                        <% if (superiorRooms != null) {
                               for (ResortRoom r : superiorRooms) { %>
                        <option value="<%= r.getRoomId() %>"
                                class="Superior-option">
                            Room <%= r.getRoomNumber() %> - Floor <%= r.getFloorNumber() %>
                        </option>
                        <% }} %>
                        <% if (premiumRooms != null) {
                               for (ResortRoom r : premiumRooms) { %>
                        <option value="<%= r.getRoomId() %>"
                                class="Premium-option">
                            Room <%= r.getRoomNumber() %> - Floor <%= r.getFloorNumber() %>
                        </option>
                        <% }} %>
                        <% if (executiveRooms != null) {
                               for (ResortRoom r : executiveRooms) { %>
                        <option value="<%= r.getRoomId() %>"
                                class="Executive-option">
                            Room <%= r.getRoomNumber() %> - Floor <%= r.getFloorNumber() %>
                        </option>
                        <% }} %>
                    </select>
                </div>

                <div class="btn-group" style="justify-content:space-between;margin-top:20px;">
                    <button type="button" class="btn-secondary"
                            onclick="goToStep(1)">← Back</button>
                    <button type="button" class="btn-primary"
                            onclick="goToStep(3)"
                            style="width:auto;">Next: Select Dates →</button>
                </div>
            </div>

            <!-- ===== STEP 3: Dates + Confirmation ===== -->
            <div class="data-card step-content" id="step3">
Step 3: Check-In &amp; Check-Out Dates
                <div class="form-row">
                    <div class="form-group">
                        <label for="checkInDate">Check-In Date *</label>
                        <input type="date" id="checkInDate" name="checkInDate"
                               min="<%= todayStr %>" required>
                    </div>
                    <div class="form-group">
                        <label for="checkOutDate">Check-Out Date *</label>
                        <input type="date" id="checkOutDate" name="checkOutDate"
                               min="<%= tomorrowStr %>" required>
                    </div>
                </div>

                <!-- Booking Summary -->
                <div id="bookingSummary"
                     style="display:none; background:#faf5ff; border-radius:15px;
                            padding:20px; margin:20px 0; border-left:4px solid #8e44ad;">
                    <h4 style="color:#6c3483; margin-bottom:15px;">📋 Booking Summary</h4>
                    <div class="detail-row">
                        <span class="detail-label">Guest Name</span>
                        <span class="detail-value" id="sum-guestName">-</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Contact Number</span>
                        <span class="detail-value" id="sum-contact">-</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Room Type</span>
                        <span class="detail-value" id="sum-roomType">-</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Rate Per Night</span>
                        <span class="detail-value" id="sum-rate">-</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Check-In Date</span>
                        <span class="detail-value" id="sum-checkIn">-</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Check-Out Date</span>
                        <span class="detail-value" id="sum-checkOut">-</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Number of Nights</span>
                        <span class="detail-value" id="sum-nights">-</span>
                    </div>
                    <div class="detail-row" style="border-top:2px solid #8e44ad; padding-top:10px; margin-top:5px;">
                        <span class="detail-label" style="font-weight:700; color:#6c3483;">
                            Estimated Total
                        </span>
                        <span class="detail-value" style="font-weight:700; color:#6c3483; font-size:1.1rem;"
                              id="sum-total">-</span>
                    </div>
                </div>

                <div class="btn-group" style="justify-content:space-between;">
                    <button type="button" class="btn-secondary"
                            onclick="goToStep(2)">← Back</button>
                    <button type="submit" class="btn-success"
                            id="submitBtn" style="width:auto;" disabled>
                        ✅ Confirm Reservation
                    </button>
                </div>
            </div>

        </form>

        <footer class="app-footer">
            <p>&copy; 2026 Ocean View Resort. All rights reserved.</p>
            <p class="footer-address">Beach Road, Unawatuna, Galle, Sri Lanka | +94 91 223 4567</p>
        </footer>

    </main>
</div>


<script>
 

    // State variables
    var currentStep = 1;
    var selectedRoomType = '';
    var selectedRatePerNight = 0;

    // Room data organized by type (for dropdown filtering)
    var roomsByType = {
        'Standard':  [<% if (standardRooms != null) { for (ResortRoom r : standardRooms) { %>{id:<%= r.getRoomId() %>,num:'<%= r.getRoomNumber() %>',floor:<%= r.getFloorNumber() %>},<% }} %>],
        'Superior':  [<% if (superiorRooms != null) { for (ResortRoom r : superiorRooms) { %>{id:<%= r.getRoomId() %>,num:'<%= r.getRoomNumber() %>',floor:<%= r.getFloorNumber() %>},<% }} %>],
        'Premium':   [<% if (premiumRooms != null) { for (ResortRoom r : premiumRooms) { %>{id:<%= r.getRoomId() %>,num:'<%= r.getRoomNumber() %>',floor:<%= r.getFloorNumber() %>},<% }} %>],
        'Executive': [<% if (executiveRooms != null) { for (ResortRoom r : executiveRooms) { %>{id:<%= r.getRoomId() %>,num:'<%= r.getRoomNumber() %>',floor:<%= r.getFloorNumber() %>},<% }} %>]
    };

    
    function goToStep(targetStep) {
        // Validate before moving forward
        if (targetStep > currentStep) {
            if (currentStep === 1 && !validateStep1()) return;
            if (currentStep === 2 && !validateStep2()) return;
        }

        // Hide current step
        document.getElementById('step' + currentStep).classList.remove('active-content');
        document.getElementById('dot' + currentStep).classList.remove('active-step');
        document.getElementById('dot' + currentStep).classList.add('completed-step');

        // Show target step
        currentStep = targetStep;
        document.getElementById('step' + currentStep).classList.add('active-content');
        document.getElementById('dot' + currentStep).classList.add('active-step');
        document.getElementById('dot' + currentStep).classList.remove('completed-step');

        // Update step lines
        if (currentStep >= 2) {
            document.getElementById('line1').classList.add('completed-line');
        }
        if (currentStep >= 3) {
            document.getElementById('line2').classList.add('completed-line');
            updateSummary();
        }

        // Scroll to top of form
        window.scrollTo({top: 0, behavior: 'smooth'});
    }

    
    function validateStep1() {
        var guestName = document.getElementById('guestName').value.trim();
        var contact = document.getElementById('contactNumber').value.trim();
        var address = document.getElementById('address').value.trim();

        if (guestName.length < 2) {
            alert('Please enter guest name (minimum 2 characters, letters only).');
            document.getElementById('guestName').focus();
            return false;
        }

        if (!/^[a-zA-Z\s]+$/.test(guestName)) {
            alert('Guest name must contain letters and spaces only.');
            document.getElementById('guestName').focus();
            return false;
        }

        if (!/^0\d{9}$/.test(contact)) {
            alert('Contact number must be 10 digits starting with 0 (e.g., 0771234567).');
            document.getElementById('contactNumber').focus();
            return false;
        }

        if (address.length < 5) {
            alert('Address must be at least 5 characters long.');
            document.getElementById('address').focus();
            return false;
        }

        return true;
    }

    
    function validateStep2() {
        if (!selectedRoomType) {
            alert('Please select a room type before proceeding.');
            return false;
        }

        var roomId = document.getElementById('selectedRoomId').value;
        if (!roomId) {
            alert('Please select a specific room from the dropdown.');
            return false;
        }

        return true;
    }

    
    function selectRoom(roomType, ratePerNight, availableCount) {
        if (availableCount === 0) {
            alert('Sorry, no ' + roomType + ' rooms are available at this time.');
            return;
        }

        // Remove selection from all options
        var allOptions = document.querySelectorAll('.room-option');
        allOptions.forEach(function(opt) {
            opt.classList.remove('selected-room');
        });

        // Highlight selected option
        document.getElementById('opt-' + roomType).classList.add('selected-room');

        // Update state
        selectedRoomType = roomType;
        selectedRatePerNight = ratePerNight;
        document.getElementById('selectedRoomType').value = roomType;

        // Populate room dropdown for specific room selection
        var roomSelect = document.getElementById('specificRoomSelect');
        roomSelect.innerHTML = '<option value="">-- Choose a specific room --</option>';

        var rooms = roomsByType[roomType];
        if (rooms && rooms.length > 0) {
            rooms.forEach(function(room) {
                var opt = document.createElement('option');
                opt.value = room.id;
                opt.textContent = 'Room ' + room.num + ' - Floor ' + room.floor;
                roomSelect.appendChild(opt);
            });

            // Auto-select first available room
            roomSelect.value = rooms[0].id;
            document.getElementById('selectedRoomId').value = rooms[0].id;
        }

        // Show room dropdown
        document.getElementById('roomSelectWrapper').style.display = 'block';
        document.getElementById('roomSelectLabel').textContent =
            'Select Specific ' + roomType + ' Room';
    }

    // Listen for specific room selection change
    document.addEventListener('DOMContentLoaded', function() {
        document.getElementById('specificRoomSelect').addEventListener('change', function() {
            document.getElementById('selectedRoomId').value = this.value;
        });

        // Check-in date change event
        document.getElementById('checkInDate').addEventListener('change', function() {
            var checkIn = new Date(this.value);
            var nextDay = new Date(checkIn);
            nextDay.setDate(nextDay.getDate() + 1);
            var nextDayStr = nextDay.toISOString().split('T')[0];

            // Set minimum check-out date
            document.getElementById('checkOutDate').min = nextDayStr;

            // If check-out is before new check-in, clear it
            var checkOut = document.getElementById('checkOutDate').value;
            if (checkOut && checkOut <= this.value) {
                document.getElementById('checkOutDate').value = '';
            }

            updateSummary();
        });

        // Check-out date change event
        document.getElementById('checkOutDate').addEventListener('change', function() {
            updateSummary();
        });
    });

    
    function updateSummary() {
        var guestName = document.getElementById('guestName').value.trim();
        var contact = document.getElementById('contactNumber').value.trim();
        var checkIn = document.getElementById('checkInDate').value;
        var checkOut = document.getElementById('checkOutDate').value;

        document.getElementById('sum-guestName').textContent =
            guestName || '-';
        document.getElementById('sum-contact').textContent =
            contact || '-';
        document.getElementById('sum-roomType').textContent =
            selectedRoomType || '-';
        document.getElementById('sum-rate').textContent =
            selectedRatePerNight > 0
                ? 'LKR ' + selectedRatePerNight.toLocaleString() + ' / night'
                : '-';
        document.getElementById('sum-checkIn').textContent =
            checkIn || '-';
        document.getElementById('sum-checkOut').textContent =
            checkOut || '-';

        var submitBtn = document.getElementById('submitBtn');
        var summaryDiv = document.getElementById('bookingSummary');

        if (checkIn && checkOut && checkOut > checkIn && selectedRoomType) {
            // Calculate nights
            var inDate = new Date(checkIn);
            var outDate = new Date(checkOut);
            var diffMs = outDate - inDate;
            var nights = Math.round(diffMs / (1000 * 60 * 60 * 24));
            var total = nights * selectedRatePerNight;

            document.getElementById('sum-nights').textContent =
                nights + ' night' + (nights > 1 ? 's' : '');
            document.getElementById('sum-total').textContent =
                'LKR ' + total.toLocaleString();

            summaryDiv.style.display = 'block';
            submitBtn.disabled = false;
            submitBtn.style.opacity = '1';
        } else {
            document.getElementById('sum-nights').textContent = '-';
            document.getElementById('sum-total').textContent = '-';
            summaryDiv.style.display = 'none';
            submitBtn.disabled = true;
            submitBtn.style.opacity = '0.5';
        }
    }

    // Final form validation before submit
    document.addEventListener('DOMContentLoaded', function() {
        document.getElementById('reservationForm').addEventListener('submit', function(event) {
            var roomId = document.getElementById('selectedRoomId').value;
            var checkIn = document.getElementById('checkInDate').value;
            var checkOut = document.getElementById('checkOutDate').value;

            if (!roomId) {
                event.preventDefault();
                alert('Please select a specific room.');
                return false;
            }

            if (!checkIn || !checkOut) {
                event.preventDefault();
                alert('Please select both check-in and check-out dates.');
                return false;
            }

            if (checkOut <= checkIn) {
                event.preventDefault();
                alert('Check-out date must be after check-in date.');
                return false;
            }
        });
    });
</script>

</body>
</html>