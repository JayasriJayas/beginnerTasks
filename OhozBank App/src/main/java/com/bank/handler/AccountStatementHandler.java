package com.bank.handler;

//import com.bank.models.Transaction;
//import com.bank.service.TransactionService;
//import com.bank.service.impl.TransactionServiceImpl;
//import com.bank.util.AuthorizationUtil;
//import com.google.gson.Gson;
//
//import javax.servlet.http.*;
//import java.io.PrintWriter;
//import java.util.List;
//
//public class AccountStatementHandler {
//    private final TransactionService transactionService = new TransactionServiceImpl();
//    private final Gson gson = new Gson();
//
//    public void handleStatement(HttpServletRequest req, HttpServletResponse res) {
//        res.setContentType("application/json");
//
//        try (PrintWriter out = res.getWriter()) {
//            HttpSession session = req.getSession(false);
//            if (session == null || session.getAttribute("role") == null) {
//                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                out.write("{\"error\":\"Unauthorized access.\"}");
//                return;
//            }
//
//            long accountId = Long.parseLong(req.getParameter("accountId"));
//
//            if (!AuthorizationUtil.canAccessAccount(session, accountId)) {
//                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                out.write("{\"error\":\"Access denied to account.\"}");
//                return;
//            }
//
//            List<Transaction> transactions = transactionService.getStatement(accountId);
//            out.write(gson.toJson(transactions));
//        } catch (Exception e) {
//            try {
//                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        }
//    }
//}
public class AccountStatementHandler{
	public void handleStatement() {
		
	}
	
}
