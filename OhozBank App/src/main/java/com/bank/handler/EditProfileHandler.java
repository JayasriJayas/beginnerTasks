package com.bank.handler;

import com.bank.models.Customer;
import com.bank.service.UserService;
import com.bank.service.impl.UserServiceImpl;
import com.bank.util.ResponseUtil;
import com.google.gson.Gson;

import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class EditProfileHandler {
    private final Logger logger = Logger.getLogger(EditProfileHandler.class.getName());
    private final UserService userService = new UserServiceImpl();
    private final Gson gson = new Gson();

    public void handleEditProfile(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access.");
            return;
        }

        long userId = (Long) session.getAttribute("userId");

        try (BufferedReader reader = req.getReader()) {
            Map<String, String> payload = gson.fromJson(reader, Map.class);

            Customer customer = new Customer();
            customer.setUserId(userId);
            customer.setAddress(payload.get("address"));
            customer.setMaritalStatus(payload.get("maritalStatus"));
            customer.setOccupation(payload.get("occupation"));
            customer.setAnnualIncome(Double.parseDouble(payload.get("annualIncome")));

            boolean updated = userService.updateCustomerProfile(customer);
            if (updated) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Profile updated successfully.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Failed to update profile.");
            }

        } catch (Exception e) {
            logger.severe("Profile update failed: " + e.getMessage());
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }
}
