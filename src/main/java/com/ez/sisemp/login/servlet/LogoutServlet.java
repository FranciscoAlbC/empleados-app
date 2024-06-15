package com.ez.sisemp.login.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final String LOGIN_JSP = "/login/login.jsp";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false); //get session

        if(session != null) {
            session.invalidate(); //cerrar sesion
        }
        resp.sendRedirect(req.getContextPath() + LOGIN_JSP); //Redirigir al login
    }
}
