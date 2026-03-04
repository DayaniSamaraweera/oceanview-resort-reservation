package com.oceanview.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Controller Servlet for the Help Section.
 *
 * <p><b>Requirement Traceability:</b> Implements "Help Section"
 * feature - provides guidelines on how to use the reservation
 * system for new staff members.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
@WebServlet("/HelpController")
public class HelpController extends HttpServlet {

    private static final Logger HELP_LOGGER =
            Logger.getLogger(HelpController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HELP_LOGGER.info("Help section accessed");
        request.getRequestDispatcher("/help.jsp")
                .forward(request, response);
    }
}