package com.bank.handler;

import com.bank.models.Request;
import com.bank.service.UserService;
import com.bank.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.time.LocalDate;

public class SignupHandler {

    private final UserService userService = new UserServiceImpl();

    public void handleSignup(HttpServletRequest req, HttpServletResponse res) {
        try {
            Request userRequest = new Request();
            userRequest.setUsername(req.getParameter("username"));
            userRequest.setPassword(req.getParameter("password"));
            userRequest.setEmail(req.getParameter("email"));
            userRequest.setPhone(req.getParameter("phone"));
            userRequest.setGender(req.getParameter("gender"));
            userRequest.setDob(LocalDate.parse(req.getParameter("dob")));
            userRequest.setAddress(req.getParameter("address"));
            userRequest.setMaritalStatus(req.getParameter("maritalStatus"));
            userRequest.setAadharNo(req.getParameter("aadhar"));
            userRequest.setPanNo(req.getParameter("pan"));
            userRequest.setBranchId(Integer.parseInt(req.getParameter("branchId")));
            userRequest.setBranchName(req.getParameter("branchName"));
            userRequest.setOccupation(req.getParameter("occupation"));
            userRequest.setAnnualIncome(Double.parseDouble(req.getParameter("income")));

            boolean registered = userService.registerRequest(userRequest);

            res.setContentType("text/html");
            PrintWriter out = res.getWriter();
            if (registered) {
                out.println("Signup request submitted. Awaiting approval.");
            } else {
                out.println("Signup failed. Try again later.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                res.getWriter().println("Error: Invalid input or server error.");
            } catch (Exception ignored) {}
        }
    }
}
