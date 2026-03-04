<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.oceanview.model.SystemUser" %>
<%@ page import="java.util.List" %>
<%
    SystemUser loggedUser = (SystemUser) session.getAttribute("loggedInUser");
    if (loggedUser == null || !"ADMIN".equals(loggedUser.getUserRole())) {
        response.sendRedirect(request.getContextPath() + "/DashboardController");
        return;
    }

    @SuppressWarnings("unchecked")
    List<SystemUser> staffList =
            (List<SystemUser>) request.getAttribute("staffList");

    String successMsg = request.getParameter("success");
    String errorMsg   = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Staff Management - Ocean View Resort</title>
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
            <a href="<%= request.getContextPath() %>/StaffManagement" class="active">
                <span class="nav-icon">👥</span><span>Staff Management</span></a>
            <a href="<%= request.getContextPath() %>/AuditLog">
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
            <h1>Staff Management</h1>
            <p class="breadcrumb">Ocean View Resort &gt; Staff Management</p>
        </div>

        <% if ("staff_created".equals(successMsg)) { %>
        <div class="alert alert-success">
            ✅ Staff account created. They must change credentials on first login.
        </div>
        <% } else if ("staff_deactivated".equals(successMsg)) { %>
        <div class="alert alert-success">✅ Staff account deactivated successfully.</div>
        <% } else if (errorMsg != null && !errorMsg.isEmpty()) { %>
        <div class="alert alert-error">⚠️ <%= errorMsg %></div>
        <% } %>

        <!-- Add Staff Form -->
        <div class="staff-form-card">
            <h3>➕ Create New Staff Account</h3>
            <p style="color:#888; font-size:0.85rem; margin-bottom:20px;">
                The new staff member will be prompted to change these temporary
                credentials on their first login.
            </p>

            <form action="<%= request.getContextPath() %>/StaffManagement"
                  method="POST" id="addStaffForm">
                <input type="hidden" name="action" value="add">

                <div class="form-row">
                    <div class="form-group">
                        <label for="fullName">Full Name *</label>
                        <input type="text" id="fullName" name="fullName"
                               placeholder="e.g. Kasun Perera" required minlength="2">
                    </div>
                    <div class="form-group">
                        <label for="emailAddress">Email Address</label>
                        <input type="email" id="emailAddress" name="emailAddress"
                               placeholder="staff@oceanviewresort.lk">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="username">Temporary Username *</label>
                        <input type="text" id="username" name="username"
                               placeholder="Min 3 characters" required minlength="3">
                    </div>
                    <div class="form-group">
                        <label for="tempPassword">Temporary Password *</label>
                        <input type="password" id="tempPassword" name="tempPassword"
                               placeholder="Min 5 characters" required minlength="5">
                    </div>
                </div>

                <div class="form-group" style="max-width:300px;">
                    <label for="userRole">Role *</label>
                    <select id="userRole" name="userRole" required>
                        <option value="RECEPTIONIST">Receptionist (Staff)</option>
                        <option value="ADMIN">Admin (Resort Manager)</option>
                    </select>
                </div>

                <div style="margin-top:15px;">
                    <button type="submit" class="btn-primary" style="width:auto;">
                        👤 Create Staff Account
                    </button>
                </div>
            </form>
        </div>

        <!-- Staff List -->
        <div class="data-card">
            <div class="card-header">
                <h3>Active Staff Members (<%= staffList != null ? staffList.size() : 0 %>)</h3>
            </div>

            <% if (staffList != null && !staffList.isEmpty()) { %>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Full Name</th>
                        <th>Username</th>
                        <th>Role</th>
                        <th>Email</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                <% for (SystemUser staff : staffList) { %>
                    <tr>
                        <td><strong><%= staff.getFullName() %></strong></td>
                        <td><%= staff.getUsername() %></td>
                        <td>
                            <span class="status-badge
                                <%= "ADMIN".equals(staff.getUserRole())
                                    ? "status-confirmed" : "status-checked-in" %>">
                                <%= staff.getUserRole() %>
                            </span>
                        </td>
                        <td><%= staff.getEmailAddress() != null
                                    ? staff.getEmailAddress() : "-" %></td>
                        <td>
                            <% if (staff.getMustChangePassword()) { %>
                            <span class="status-badge status-cancelled"
                                  style="font-size:0.75rem;">
                                Must Change PWD
                            </span>
                            <% } else { %>
                            <span class="status-badge status-confirmed"
                                  style="font-size:0.75rem;">Active</span>
                            <% } %>
                        </td>
                        <td>
                            <% if (staff.getUserId() != loggedUser.getUserId()) { %>
                            <form action="<%= request.getContextPath() %>/StaffManagement"
                                  method="POST" style="display:inline;"
                                  onsubmit="return confirm('Deactivate this account?');">
                                <input type="hidden" name="action" value="deactivate">
                                <input type="hidden" name="userId" value="<%= staff.getUserId() %>">
                                <button type="submit" class="btn-danger btn-sm">
                                    Deactivate
                                </button>
                            </form>
                            <% } else { %>
                            <span style="color:#888; font-size:0.8rem;">(You)</span>
                            <% } %>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
            <% } else { %>
            <div style="text-align:center; padding:30px; color:#999;">
                <p>No active staff members found.</p>
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
        document.getElementById('addStaffForm')
            .addEventListener('submit', function(event) {
                var pwd = document.getElementById('tempPassword').value;
                if (pwd.length < 5) {
                    event.preventDefault();
                    alert('Password must be at least 5 characters.');
                    return false;
                }
            });
    });
</script>

</body>
</html>