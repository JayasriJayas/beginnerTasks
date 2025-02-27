package com.customer.servlet;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private String jdbcURL = "jdbc:mysql://localhost:3306/application";
    private String jdbcUsername = "root";
    private String jdbcPassword = "root";
    
    private static final String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
    private static final String phoneRegex= "^[0-9]{10}$";
    private static final String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    
    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("MySQL Driver not found", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String id = request.getParameter("id");

        if("delete".equals(action)) {
            deleteUser(request, response);
        } else if ("edit".equals(action) && id != null) {
            getUserDetails(request, response, id);
        } else {
            listUsers(request, response);
        }
    }

    private void getUserDetails(HttpServletRequest request, HttpServletResponse response, String id) throws ServletException, IOException {
        User user = null;
        try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id=?")) {
            stmt.setInt(1, Integer.parseInt(id));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(
                    rs.getInt("id"),
                    rs.getString("firstname"),
                    rs.getString("surname"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("dob"),
                    rs.getString("gender"),
                    rs.getString("address"),
                    rs.getString("email"),
                    rs.getString("phone")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.setAttribute("user", user);
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }


    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<User> userList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                userList.add(new User(rs.getInt("id"), rs.getString("firstname"), rs.getString("surname"),
                        rs.getString("username"), rs.getString("password"), rs.getString("dob"),
                        rs.getString("gender"), rs.getString("address"), rs.getString("email"), rs.getString("phone")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.setAttribute("userList", userList);
        request.getRequestDispatcher("users.jsp").forward(request, response);
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        if (id != null && !id.isEmpty()) {
            try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id=?")) {
                stmt.setInt(1, Integer.parseInt(id));
                stmt.executeUpdate(); 
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

     
        listUsers(request, response);
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
        if (!validateInputs(firstname, surname, username, password, dob, gender, address, email, phone)) {
        	System.out.println(validateInputs(firstname, surname, username, password, dob, gender, address, email, phone));
            request.setAttribute("error", "Invalid input fields. Please check your details.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        if(!checkPassword(password,confirmPassword)) {
        	request.setAttribute("error", " Password and ConfirmPassword are not matching ");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        if(!checkStrongPassword(password)) {
        	request.setAttribute("error","Please provide a password with 8 characters with atleast one letter,digit,uppercase letter and special characters");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        if(!checkMail(email)) {
        	request.setAttribute("error", " Provide a valid email");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        if(!checkPhoneNo(phone)) {
        	request.setAttribute("error", " Provide a valid phone number");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }


        try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)) {
            if (id == null || id.isEmpty()) {
                String sql = "INSERT INTO users (firstname, surname, username, password, dob, gender, address, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, firstname);
                    stmt.setString(2, surname);
                    stmt.setString(3, username);
                    stmt.setString(4, password);
                    stmt.setString(5, dob);
                    stmt.setString(6, gender);
                    stmt.setString(7, address);
                    stmt.setString(8, email);
                    stmt.setString(9, phone);
                    stmt.executeUpdate();
                }
            } else {
                String sql = "UPDATE users SET firstname=?, surname=?, username=?, dob=?, gender=?, address=?, email=?, phone=? WHERE id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, firstname);
                    stmt.setString(2, surname);
                    stmt.setString(3, username);
                    stmt.setString(4, dob);
                    stmt.setString(5, gender);
                    stmt.setString(6, address);
                    stmt.setString(7, email);
                    stmt.setString(8, phone);
                    stmt.setInt(9, Integer.parseInt(id));
                    stmt.executeUpdate();
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.sendRedirect("RegisterServlet");

    }
    private boolean validateInputs(String firstname, String surname, String username, String password, String dob, String gender, String address, String email, String phone) {
    	
        return firstname != null && !firstname.isEmpty() &&
               surname != null && !surname.isEmpty() &&
               username != null && !username.isEmpty() &&
               password != null && !password.isEmpty() &&
               dob != null && !dob.isEmpty() &&
               gender != null && !gender.isEmpty() &&
               address != null && !address.isEmpty() &&
               email != null &&
               phone != null;
    }
    private boolean checkPassword(String password ,String confirmPassword ) {
    	return password.equals(confirmPassword);
    }
    private boolean checkStrongPassword(String password) {
    	if(!Pattern.matches(passwordRegex,password)) {
    		return false;
    	}
    	return true;
    }
    private boolean checkMail(String email) {
    	if(! Pattern.matches(emailRegex, email)) {
    		return false;
    	}
    	return true;
    }
    private boolean checkPhoneNo(String phone) {
    	if(!Pattern.matches(phoneRegex, phone)) {
    		return false;
    	}
    	return true;
    }
  

}



