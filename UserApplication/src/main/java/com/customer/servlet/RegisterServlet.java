package com.customer.servlet;

import com.customer.database.DataBaseConnection;
import com.customer.model.User;
import com.customer.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DataBaseConnection dbConnection;

    @Override
    public void init() throws ServletException {
        try {
            dbConnection = new DataBaseConnection();
        } catch (Exception e) {
            throw new ServletException("Database connection failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String id = request.getParameter("id");

        try {
            if ("delete".equals(action) && id != null) {
                dbConnection.deleteUser(Integer.parseInt(id));
                response.sendRedirect("RegisterServlet");
            } else if ("edit".equals(action) && id != null) {
                request.setAttribute("user", dbConnection.getUserById(Integer.parseInt(id)));
                request.getRequestDispatcher("register.jsp").forward(request, response);
            } else {
            	request.setAttribute("userList",dbConnection.listUsers());
                request.getRequestDispatcher("users.jsp").forward(request, response);
              
            }
        } catch (SQLException e) {
            throw new ServletException("Error accessing database", e);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String firstname = request.getParameter("firstname");
        String surname = request.getParameter("surname");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmpass");
        String dob = request.getParameter("dob");
        String gender = request.getParameter("gender");
        String address = request.getParameter("address");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        
        User user = new User(0, firstname, surname, username, password, dob, gender, address, email, phone);
        

        
        if (!Util.validateInputs(firstname, surname, username, password, dob, gender, address, email, phone)) {
            request.setAttribute("error", "Invalid input fields. Please check your details.");
            request.setAttribute("user", user);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        if (!Util.checkPassword(password, confirmPassword)) {
            request.setAttribute("error", "Password and Confirm Password do not match.");
            request.setAttribute("user", user);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        if (!Util.checkStrongPassword(password)) {
            request.setAttribute("error", "Password with 8 characters with one letter, digit, uppercase letter, and special character required.");
            request.setAttribute("user", user);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        if (!Util.checkMail(email)) {
            request.setAttribute("error", "Provide a valid email.");
            request.setAttribute("user", user);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        if (!Util.checkPhoneNo(phone)) {
            request.setAttribute("error", "Provide a valid phone number.");
            request.setAttribute("user", user);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }


        try {
            if (id == null || id.isEmpty()) {
                User userInsert = new User(0, firstname, surname, username, password, dob, gender, address, email, phone);
                dbConnection.insertUser(userInsert );
            } else {
                int userId = Integer.parseInt(id);
                User userUpdate = new User(userId, firstname, surname, username, password, dob, gender, address, email, phone);
                dbConnection.updateUser(userUpdate);
            }
        } 

    	catch (SQLException e) {
            throw new ServletException("Error processing user data", e);
        }

        response.sendRedirect("RegisterServlet");
    }

    @Override
    public void destroy() {
        try {
            dbConnection.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
