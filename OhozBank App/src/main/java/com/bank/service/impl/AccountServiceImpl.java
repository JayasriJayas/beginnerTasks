package com.bank.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.bank.dao.AccountDAO;
import com.bank.dao.impl.AccountDAOImpl;
import com.bank.models.Account;
import com.bank.service.AccountService;

import exception.QueryException;

import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO = new AccountDAOImpl();

    @Override
    public BigDecimal getAccountBalance(long accountId) throws QueryException, SQLException {
        Account account = accountDAO.getAccountById(accountId);
        return account != null ? account.getBalance() : null;
    }
}

