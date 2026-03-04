<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.oceanview.model.SystemUser" %>
<%@ page import="com.oceanview.model.ActivityAuditEntry" %>
<%@ page import="java.util.List" %>
<%
    SystemUser loggedUser = (SystemUser) session.getAttribute("loggedInUser");
    if (loggedUser == null || !"ADMIN".equals(loggedUser.getUserRole())) {
        response.sendRedirect(request.getContextPath() + "/DashboardController");
        return;
    }

    @SuppressWarnings("unchecked")
    List<ActivityAuditEntry> auditEntries =
            (List<ActivityAuditEntry>) request.getAttribute("auditEntries");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Activity Log - Ocean View Resort</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/oceanview-styles.css">
</head>
<body>

<div class="app-layout">

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
            <a href="<%= request.getContextPath() %>/BillingController?action=list">
                <span class="nav-icon">🧾</span><span>Generate Bill</span></a>
            <div class="nav-divider"></div>
            <a href="<%= request.getContextPath() %>/ReportController">
                <span class="nav-icon">📊</span><span>Reports</span></a>
            <a href="<%= request.getContextPath() %>/StaffManagement">
                <span class="nav-icon">👥</span><span>Staff Management</span></a>
            <a href="<%= request.getContextPath() %>/AuditLog" class="active">
                <span class="nav-icon">📝</span><span>Activity Log</span></a>
            <div class="nav-divider"></div>
            <a href="<%= request.getContextPath() %>/HelpController">
                <span class="nav-icon">❓</span><span>Help Section</span></a>
        </div>
        <div class="sidebar-footer">
            <div class="user-info">
                Signed in as <strong><%= loggedUser.getFullName() %></strong>
                <br><small>ADMIN</small>
            </div>
            <a href="<%= request.getContextPath() %>/LoginController?action=logout"
               class="signout-btn">🚪 Sign Out</a>
        </div>
    </nav>

    <main class="main-content">

        <div class="page-header">
            <h1>Activity Log</h1>
            <p class="breadcrumb">Ocean View Resort &gt; Activity Log</p>
        </div>

        <div class="data-card">
            <div class="card-header">
                <h3>System Activity Trail
                    (<%= auditEntries != null ? auditEntries.size() : 0 %> entries)
                </h3>
            </div>

            <!-- Search -->
            <div style="margin-bottom:15px;">
                <input type="text" id="auditSearchInput"
                       placeholder="🔍 Search by username or action type..."
                       style="width:100%;padding:10px 16px;border:2px solid #e0e0e0;
                              border-radius:25px;font-family:Poppins,sans-serif;
                              font-size:0.9rem;outline:none;">
            </div>

            <% if (auditEntries != null && !auditEntries.isEmpty()) { %>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Username</th>
                        <th>Action</th>
                        <th>Description</th>
                        <th>Timestamp</th>
                        <th>IP Address</th>
                    </tr>
                </thead>
                <tbody id="auditTableBody">
                <% int logNum = 1; for (ActivityAuditEntry entry : auditEntries) {
                       String actionColor = "#6c3483";
                       String act = entry.getActionType();
                       if (act != null) {
                           if (act.contains("LOGIN"))  actionColor = "#27ae60";
                           else if (act.contains("LOGOUT")) actionColor = "#888";
                           else if (act.contains("CREATE")) actionColor = "#2980b9";
                           else if (act.contains("UPDATE") || act.contains("STATUS"))
                               actionColor = "#f39c12";
                           else if (act.contains("DEACTIVATE") || act.contains("CANCEL"))
                               actionColor = "#e74c3c";
                       }
                %>
                <tr>
                    <td style="color:#888;"><%= logNum++ %></td>
                    <td><strong><%= entry.getUsername() != null
                                      ? entry.getUsername() : "SYSTEM" %></strong></td>
                    <td>
                        <span style="font-size:0.78rem; font-weight:600;
                                     color:<%= actionColor %>; background:<%= actionColor %>22;
                                     padding:3px 10px; border-radius:25px;">
                            <%= entry.getActionType() != null
                                ? entry.getActionType() : "UNKNOWN" %>
                        </span>
                    </td>
                    <td style="font-size:0.85rem; max-width:300px;">
                        <%= entry.getActionDescription() != null
                            ? entry.getActionDescription() : "-" %>
                    </td>
                    <td style="font-size:0.8rem; color:#888; white-space:nowrap;">
                        <%= entry.getActionTimestamp() != null
                            ? entry.getActionTimestamp().toString()
                                           .replace("T"," ").substring(0,16)
                            : "N/A" %>
                    </td>
                    <td style="font-size:0.8rem; color:#888;">
                        <%= entry.getIpAddress() != null
                            ? entry.getIpAddress() : "-" %>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
            <% } else { %>
            <div style="text-align:center; padding:40px; color:#999;">
                <p style="font-size:2rem;">📝</p>
                <p>No activity log entries found.</p>
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

<script>
    document.addEventListener('DOMContentLoaded', function() {
        var auditSearch = document.getElementById('auditSearchInput');
        if (auditSearch) {
            auditSearch.addEventListener('keyup', function() {
                var term = this.value.toLowerCase().trim();
                document.querySelectorAll('#auditTableBody tr')
                    .forEach(function(row) {
                        row.style.display =
                            row.textContent.toLowerCase().includes(term)
                            ? '' : 'none';
                    });
            });
        }
    });
</script>

</body>
</html>