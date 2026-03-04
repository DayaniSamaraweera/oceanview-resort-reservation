package com.oceanview.controller;

import com.oceanview.model.ActivityAuditEntry;
import com.oceanview.service.IAuditTrailOrchestrator;
import com.oceanview.service.AuditTrailOrchestratorImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller Servlet for activity audit log (Admin only).
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
@WebServlet("/AuditLog")
public class AuditLogController extends HttpServlet {

    private static final Logger AUDIT_LOGGER =
            Logger.getLogger(AuditLogController.class.getName());

    private IAuditTrailOrchestrator auditService;

    @Override
    public void init() throws ServletException {
        auditService = new AuditTrailOrchestratorImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        List<ActivityAuditEntry> auditEntries =
                auditService.getAllAuditEntries();
        request.setAttribute("auditEntries", auditEntries);

        AUDIT_LOGGER.info("Audit log loaded: "
                + auditEntries.size() + " entries");

        request.getRequestDispatcher("/auditLog.jsp")
                .forward(request, response);
    }
}