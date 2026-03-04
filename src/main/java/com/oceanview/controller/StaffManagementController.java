package com.oceanview.controller;

import com.oceanview.model.SystemUser;
import com.oceanview.service.IAuditTrailOrchestrator;
import com.oceanview.service.AuditTrailOrchestratorImpl;
import com.oceanview.service.IStaffManagementOrchestrator;
import com.oceanview.service.StaffManagementOrchestratorImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller Servlet for staff management (Admin only).
 *
 * <p><b>RBAC:</b> This controller is accessible only by ADMIN users.
 * The AuthenticationFilter blocks RECEPTIONIST access to this URL.</p>
 *
 * <p><b>Password Change Flow:</b> When Admin creates a staff account,
 * mustChangePassword is set to TRUE. Staff member must change
 * temporary credentials on first login.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
@WebServlet("/StaffManagement")
public class StaffManagementController extends HttpServlet {

    /** Logger for staff management events */
    private static final Logger STAFF_LOGGER =
            Logger.getLogger(StaffManagementController.class.getName());

    /** Staff management service */
    private IStaffManagementOrchestrator staffService;

    /** Audit service */
    private IAuditTrailOrchestrator auditService;

    @Override
    public void init() throws ServletException {
        staffService = new StaffManagementOrchestratorImpl();
        auditService = new AuditTrailOrchestratorImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Load all active staff members
        List<SystemUser> staffList = staffService.getAllActiveStaff();
        request.setAttribute("staffList", staffList);

        request.getRequestDispatcher("/manageStaff.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);
        SystemUser loggedUser =
                (SystemUser) session.getAttribute("loggedInUser");

        if ("add".equals(action)) {
            addStaffMember(request, response, loggedUser);
        } else if ("deactivate".equals(action)) {
            deactivateStaffMember(request, response, loggedUser);
        } else {
            response.sendRedirect(request.getContextPath()
                    + "/StaffManagement");
        }
    }

    /**
     * Creates a new staff account with temporary credentials.
     */
    private void addStaffMember(HttpServletRequest request,
                                HttpServletResponse response,
                                SystemUser loggedUser)
            throws IOException {

        String username = request.getParameter("username");
        String tempPassword = request.getParameter("tempPassword");
        String fullName = request.getParameter("fullName");
        String userRole = request.getParameter("userRole");
        String emailAddress = request.getParameter("emailAddress");

        try {
            boolean created = staffService.createStaffAccount(
                    username, tempPassword, fullName,
                    userRole, emailAddress);

            if (created) {
                STAFF_LOGGER.info("Staff account created by admin: "
                        + username);

                auditService.logActivity(
                        loggedUser.getUserId(),
                        loggedUser.getUsername(),
                        "CREATE_STAFF",
                        "Created staff account: " + username
                                + " (Role: " + userRole + ")",
                        "users",
                        0,
                        request.getRemoteAddr());

                response.sendRedirect(request.getContextPath()
                        + "/StaffManagement?success=staff_created");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/StaffManagement?error=create_failed");
            }

        } catch (IllegalArgumentException validError) {
            response.sendRedirect(request.getContextPath()
                    + "/StaffManagement?error="
                    + java.net.URLEncoder.encode(
                    validError.getMessage(), "UTF-8"));

        } catch (Exception staffError) {
            STAFF_LOGGER.log(Level.SEVERE,
                    "Error creating staff", staffError);
            response.sendRedirect(request.getContextPath()
                    + "/StaffManagement?error=system_error");
        }
    }

    /**
     * Deactivates a staff account (soft delete).
     */
    private void deactivateStaffMember(HttpServletRequest request,
                                       HttpServletResponse response,
                                       SystemUser loggedUser)
            throws IOException {

        try {
            int userId = Integer.parseInt(
                    request.getParameter("userId"));

            boolean deactivated =
                    staffService.deactivateStaffAccount(userId);

            if (deactivated) {
                auditService.logActivity(
                        loggedUser.getUserId(),
                        loggedUser.getUsername(),
                        "DEACTIVATE_STAFF",
                        "Deactivated staff account ID: " + userId,
                        "users",
                        userId,
                        request.getRemoteAddr());

                response.sendRedirect(request.getContextPath()
                        + "/StaffManagement?success=staff_deactivated");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/StaffManagement?error=deactivate_failed");
            }

        } catch (Exception deactError) {
            STAFF_LOGGER.log(Level.SEVERE,
                    "Error deactivating staff", deactError);
            response.sendRedirect(request.getContextPath()
                    + "/StaffManagement?error=system_error");
        }
    }
}