package com.oceanview.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

//Loads the help/guide page for new staff members.//
@WebServlet("/HelpController")

public class HelpController extends HttpServlet {
	private static final long serialVersionUID = 1L;
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