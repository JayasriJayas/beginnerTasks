package com.bank.service;

import java.math.BigDecimal;
import java.sql.SQLException;

import exception.QueryException;

public interface AccountService {
    BigDecimal getAccountBalance(long accountId) throws QueryException, SQLException;
}
