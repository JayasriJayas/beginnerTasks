package com.bank.factory;

import com.bank.service.UserService;
import com.bank.service.AuthenticationService;
import com.bank.service.TransactionService;
import com.bank.service.AdminService;
import com.bank.service.RequestService;
import com.bank.service.BranchService;
import com.bank.service.AccountRequestService;
import com.bank.service.AccountService;
import com.bank.service.impl.UserServiceImpl;
import com.bank.service.impl.AuthenticationServiceImpl;
import com.bank.service.impl.BranchServiceImpl;
import com.bank.service.impl.RequestServiceImpl;
import com.bank.service.impl.TransactionServiceImpl;
import com.bank.service.impl.AccountRequestServiceImpl;
import com.bank.service.impl.AccountServiceImpl;
import com.bank.service.impl.AdminServiceImpl;

public class ServiceFactory {

    private static final UserService userService = new UserServiceImpl();
    private static final AuthenticationService authService = new AuthenticationServiceImpl();
    private static final AdminService adminService = new AdminServiceImpl();
    private static final TransactionService transactionService = new TransactionServiceImpl();
    private static final RequestService requestService = new RequestServiceImpl();
    private static final BranchService branchService = new BranchServiceImpl();
    private static final AccountService accountService = new AccountServiceImpl();
    private static final AccountRequestService accountRequestService = new AccountRequestServiceImpl();

    public static UserService getUserService() {
        return userService;
    }

    public static AuthenticationService getAuthService() {
        return authService;
    }

    public static AdminService getAdminService() {
        return adminService;
    }
    public static TransactionService getTransactionService() {
        return  transactionService;
    }
    public static RequestService getRequestService() {
        return  requestService;
    }
    public static BranchService getBranchService() {
    	return branchService;
    }
    public static AccountRequestService getAccountRequestService() {
        return  accountRequestService;
    }
    public static AccountService getAccountService() {
    	return accountService;
    }

}
