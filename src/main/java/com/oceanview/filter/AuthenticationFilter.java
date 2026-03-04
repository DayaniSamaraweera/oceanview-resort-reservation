package com.oceanview.filter;

import com.oceanview.model.SystemUser;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Authentication and Authorization Filter for Ocean View Resort.
 *
 * <p><b>RBAC (Role-Based Access Control):</b> This filter intercepts
 * all incoming requests and enforces:
 * 1. Authentication - user must be logged in
 * 2. Authorization - user must have the correct role
 * 3. Password change - new staff must change temp credentials</p>
 *
 * <p><b>Security:</b> Prevents unauthorized URL access. Even if a
 * RECEPTIONIST manually types /StaffManagement or /Reports in the
 * browser, this filter blocks access and redirects to dashboard.</p>
 *
 * <p><b>Requirement Traceability:</b> Implements "User Authentication
 * (Login)" security requirement with role-based redirection.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    /** Logger for filter events */
    private static final Logger FILTER_LOGGER =
            Logger.getLogger(AuthenticationFilter.class.getName());

    /** Pages accessible without login */
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/login.jsp",
            "/LoginController",
            "/css/",
            "/js/",
            "/error404.jsp",
            "/error500.jsp",
            "/api/"
    );

    /** Pages only ADMIN can access */
    private static final List<String> ADMIN_ONLY_PATHS = Arrays.asList(
            "/StaffManagement",
            "/manageStaff.jsp",
            "/AuditLog",
            "/auditLog.jsp",
            "/ReportController",
            "/reports.jsp",
            "/ExportCSV"
    );

    /** Password change page - accessible when mustChangePassword = true */
    private static final String CHANGE_PASSWORD_PATH = "/ChangePassword";
    private static final String CHANGE_PASSWORD_PAGE = "/changePassword.jsp";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        FILTER_LOGGER.info(
                "AuthenticationFilter initialized - RBAC security active");
    }

    /**
     * Main filter logic - intercepts every request.
     *
     * <p>Decision flow:
     * 1. Is it a public path? → Allow through
     * 2. Is user logged in? → If not, redirect to login
     * 3. Must user change password? → If yes, redirect to change page
     * 4. Is it an admin-only path? → Check role, block if RECEPTIONIST
     * 5. All checks passed → Allow through</p>
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestPath = httpRequest.getServletPath();
        String contextPath = httpRequest.getContextPath();

        // ---- Step 1: Allow public paths ----
        if (isPublicPath(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        // ---- Step 2: Check if user is logged in ----
        HttpSession currentSession = httpRequest.getSession(false);

        if (currentSession == null
                || currentSession.getAttribute("loggedInUser") == null) {
            FILTER_LOGGER.warning(
                    "Unauthorized access attempt to: " + requestPath);
            httpResponse.sendRedirect(
                    contextPath + "/login.jsp?error=session_expired");
            return;
        }

        SystemUser loggedInUser =
                (SystemUser) currentSession.getAttribute("loggedInUser");

        // ---- Step 3: Check if user must change password ----
        if (loggedInUser.getMustChangePassword()) {
            // Allow access only to password change page
            if (!requestPath.equals(CHANGE_PASSWORD_PATH)
                    && !requestPath.equals(CHANGE_PASSWORD_PAGE)) {
                FILTER_LOGGER.info("Redirecting user "
                        + loggedInUser.getUsername()
                        + " to change password (first login)");
                httpResponse.sendRedirect(
                        contextPath + "/changePassword.jsp");
                return;
            }
        }

        // ---- Step 4: Check admin-only paths ----
        if (isAdminOnlyPath(requestPath)
                && !"ADMIN".equals(loggedInUser.getUserRole())) {
            FILTER_LOGGER.warning("RECEPTIONIST "
                    + loggedInUser.getUsername()
                    + " attempted to access admin page: " + requestPath);
            httpResponse.sendRedirect(
                    contextPath + "/DashboardController");
            return;
        }

        // ---- Step 5: All checks passed ----
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        FILTER_LOGGER.info("AuthenticationFilter destroyed");
    }

    /**
     * Checks if the request path is publicly accessible.
     *
     * @param path the servlet path
     * @return true if the path does not require authentication
     */
    private boolean isPublicPath(String path) {
        if (path == null || path.equals("/") || path.isEmpty()) {
            return true;
        }
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the request path requires ADMIN role.
     *
     * @param path the servlet path
     * @return true if only ADMIN users can access this path
     */
    private boolean isAdminOnlyPath(String path) {
        for (String adminPath : ADMIN_ONLY_PATHS) {
            if (path.startsWith(adminPath)) {
                return true;
            }
        }
        return false;
    }
}