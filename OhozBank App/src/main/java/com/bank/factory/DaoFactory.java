package com.bank.factory;

import com.bank.dao.CustomerDAO;
import com.bank.dao.AdminDAO;
import com.bank.dao.BranchDAO;
import com.bank.dao.UserDAO;
import com.bank.dao.AccountDAO;
import com.bank.dao.RequestDAO;
import com.bank.dao.AccountRequestDAO;
import com.bank.dao.TransactionDAO;
import com.bank.dao.impl.AccountDAOImpl;
import com.bank.dao.impl.AccountRequestDAOImpl;
import com.bank.dao.impl.AdminDAOImpl;
import com.bank.dao.impl.BranchDAOImpl;
import com.bank.dao.impl.CustomerDAOImpl;
import com.bank.dao.impl.RequestDAOImpl;
import com.bank.dao.impl.TransactionDAOImpl;
import com.bank.dao.impl.UserDAOImpl;

public class DaoFactory {
    private static final UserDAO userDAO = new UserDAOImpl();
    private static final AdminDAO adminDAO = new AdminDAOImpl();
    private static final CustomerDAO customerDAO = new CustomerDAOImpl();
    private static final AccountDAO accountDAO = new AccountDAOImpl();
    private static final TransactionDAO transactionDAO = new TransactionDAOImpl();
    private static final RequestDAO requestDAO = new RequestDAOImpl();
    private static final AccountRequestDAO accountRequestDAO = new AccountRequestDAOImpl();
    private static final BranchDAO branchDAO = new BranchDAOImpl();

    public static UserDAO getUserDAO() {
        return userDAO;
    }

    public static AdminDAO getAdminDAO() {
        return adminDAO;
    }

    public static CustomerDAO getCustomerDAO() {
        return customerDAO;
    }
    public static AccountDAO getAccountDAO() {
        return accountDAO;
    }

    public static TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }
    public static RequestDAO getRequestDAO() {
        return requestDAO;
    }

    public static AccountRequestDAO getAccountRequestDAO() {
        return accountRequestDAO;
    }
    public static BranchDAO getBranchDAO() {
        return branchDAO;
    }

  
    
}
