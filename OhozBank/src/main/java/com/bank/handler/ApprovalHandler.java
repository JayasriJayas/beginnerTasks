package com.bank.handler;

import com.bank.dao.RequestDAO;
import com.bank.dao.UserDAO;
import com.bank.dao.impl.RequestDAOImpl;
import com.bank.dao.impl.UserDAOImpl;
import com.bank.mapper.UserMapper;
import com.bank.models.Request;
import com.bank.models.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class ApprovalHandler {

    private final RequestDAO requestDAO = new RequestDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();


    public void handleApproval(HttpServletRequest req, HttpServletResponse res) {
        String username = req.getParameter("username");

        try {
            Request request = requestDAO.getPendingRequestByUsername(username);
            if (request == null) {
                res.getWriter().println("No such pending request.");
                return;
            }

            User user = UserMapper.fromRequest(request);
            int result = userDAO.addUser(user);
            if (result > 0 && requestDAO.markRequestAsApproved(username)) {
                PrintWriter out = res.getWriter();
                out.println("User approved and added successfully.");
            } else {
                res.getWriter().println("Approval failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
