<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.oceanview.model.SystemUser" %>
<%@ page import="com.oceanview.model.InvoiceRecord" %>
<%@ page import="java.util.List" %>
<%


    SystemUser loggedUser = (SystemUser) session.getAttribute("loggedInUser");
    if (loggedUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String userRole = loggedUser.getUserRole();

    @SuppressWarnings("unchecked")
    List<InvoiceRecord> bills =
            (List<InvoiceRecord>) request.getAttribute("bills");

    Double totalRevenue = (Double) request.getAttribute("totalRevenue");
    Integer totalBillCount = (Integer) request.getAttribute("totalBillCount");

    if (totalRevenue == null) totalRevenue = 0.0;
    if (totalBillCount == null) totalBillCount = 0;

    String successMsg = request.getParameter("success");
    String errorMsg   = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bill List - Ocean View Resort</title>
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

  
    <main class="main-content">

        <div class="page-header">
            <h1>Bill List</h1>
            <p class="breadcrumb">Ocean View Resort &gt; Generate Bill &gt; All Bills</p>
        </div>

        <!-- Messages -->
        <% if ("generated".equals(successMsg)) { %>
        <div class="alert alert-success">✅ Bill generated successfully!</div>
        <% } else if ("system".equals(errorMsg)) { %>
        <div class="alert alert-error">⚠️ A system error occurred. Please try again.</div>
        <% } %>

        <!-- Summary Stats -->
        <div class="summary-stats">
            <div class="summary-stat-item">
                <div class="summary-value"><%= totalBillCount %></div>
                <div class="summary-label">Total Bills Generated</div>
            </div>
            <div class="summary-stat-item" style="border-top-color:#f39c12;">
                <div class="summary-value" style="color:#f39c12;">
                    LKR <%= String.format("%,.2f", totalRevenue) %>
                </div>
                <div class="summary-label">Total Revenue Collected</div>
            </div>
        </div>

        <!-- Bills Table -->
        <div class="data-card">
            <div class="card-header">
                <h3>All Generated Bills (<%= bills != null ? bills.size() : 0 %>)</h3>
                <% if ("ADMIN".equals(userRole)) { %>
                <a href="<%= request.getContextPath() %>/ExportCSV?type=bills"
                   class="btn-success btn-sm">📥 Export CSV</a>
                <% } %>
            </div>

            <% if (bills != null && !bills.isEmpty()) { %>

            <!-- Client-side search -->
            <div style="margin-bottom:15px;">
                <input type="text" id="billSearchInput"
                       placeholder="🔍 Search by bill number or guest name..."
                       style="width:100%;padding:10px 16px;border:2px solid #e0e0e0;
                              border-radius:25px;font-family:Poppins,sans-serif;
                              font-size:0.9rem;outline:none;">
            </div>

            <table class="data-table" id="billsTable">
                <thead>
                    <tr>
                        <th>Bill Number</th>
                        <th>Reservation No.</th>
                        <th>Guest Name</th>
                        <th>Room Type</th>
                        <th>Nights</th>
                        <th>Rate/Night</th>
                        <th>Total Amount</th>
                        <th>Generated At</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody id="billsTableBody">
                <% for (InvoiceRecord b : bills) { %>
                    <tr>
                        <td><strong><%= b.getBillNumber() %></strong></td>
                        <td><%= b.getReservationNumber() %></td>
                        <td><%= b.getGuestName() %></td>
                        <td><%= b.getRoomType() %></td>
                        <td><%= b.getNumberOfNights() %></td>
                        <td>LKR <%= String.format("%,.2f", b.getRatePerNight()) %></td>
                        <td style="font-weight:600; color:#6c3483;">
                            LKR <%= String.format("%,.2f", b.getTotalAmount()) %>
                        </td>
                        <td style="font-size:0.8rem; color:#888;">
                            <%= b.getGeneratedAt() != null
                                ? b.getGeneratedAt().toString()
                                               .replace("T"," ")
                                               .substring(0,16)
                                : "N/A" %>
                        </td>
                        <td>
                            <div class="btn-group">
                                <a href="<%= request.getContextPath() %>/BillingController?action=view&billNumber=<%= b.getBillNumber() %>"
                                   class="btn-secondary btn-sm">View</a>
                                <a href="<%= request.getContextPath() %>/BillingController?action=view&billNumber=<%= b.getBillNumber() %>"
                                   class="btn-gold btn-sm"
                                   onclick="setTimeout(function(){window.print();},500);">
                                   Print
                                </a>
                            </div>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>

            <% } else { %>
            <div style="text-align:center; padding:40px; color:#999;">
                <p style="font-size:2.5rem;">🧾</p>
                <p style="font-size:1.1rem; margin-bottom:15px;">
                    No bills have been generated yet.
                </p>
                <a href="<%= request.getContextPath() %>/ReservationController?action=list"
                   class="btn-primary"
                   style="width:auto; display:inline-block; padding:12px 30px;">
                    View Reservations to Generate Bills
                </a>
            </div>
            <% } %>
        </div>

        <footer class="app-footer">
            <p>&copy; 2026 Ocean View Resort. All rights reserved.</p>
            <p class="footer-address">
                Beach Road, Unawatuna, Galle, Sri Lanka | +94 91 223 4567
            </p>
        </footer>

    </main>
</div>

<!-- Event-Driven: Client-side bill table search -->
<script>
    document.addEventListener('DOMContentLoaded', function() {

        var billSearch = document.getElementById('billSearchInput');

        if (billSearch) {
            billSearch.addEventListener('keyup', function() {
                var searchTerm = this.value.toLowerCase().trim();
                var tableRows  =
                    document.querySelectorAll('#billsTableBody tr');

                tableRows.forEach(function(row) {
                    var rowText = row.textContent.toLowerCase();
                    row.style.display =
                        rowText.includes(searchTerm) ? '' : 'none';
                });
            });
        }
    });
</script>

</body>
</html>