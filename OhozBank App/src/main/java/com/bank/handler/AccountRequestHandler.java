package com.bank.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.bank.service.AccountService;
import com.bank.service.impl.AccountServiceImpl;
import com.bank.util.RequestParser;
import com.bank.util.ResponseUtil;
import com.bank.util.SessionUtil;
import com.google.gson.Gson;
import com.bank.models.AccountRequest;

public class AccountRequestHandler {

    private final AccountService accountService = new AccountServiceImpl();

    public void request(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	try {
        HttpSession session = req.getSession(false);
        if (!SessionUtil.isSessionAvailable(session, res)) return;


        long userId = (Long) session.getAttribute("userId");

      AccountRequest request = RequestParser.parseRequest(req, AccountRequest.class); 
            long branchId = request.getBranchId();
            

            boolean success = accountService.createAccountRequest(userId, branchId);

            if (success) {
                ResponseUtil.sendSuccess(res, HttpServletResponse.SC_OK, "Account request submitted.");
            } else {
                ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Request failed.");
            }
        } catch (Exception e) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error.");
        }
    }
}
