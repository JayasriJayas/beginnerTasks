package com.bank.mapper;

import com.bank.enums.TransactionStatus;
import com.bank.enums.TransactionType;
import com.bank.models.Transaction;

import java.math.BigDecimal;

public class TransactionMapper {

    public static Transaction mapToTransaction(long accountId, long userId, Long transactionAccountId,
                                               BigDecimal amount, BigDecimal closingBalance,
                                               TransactionType type, String description,TransactionStatus status) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setUserId(userId);
        transaction.setTransactionAccountId(transactionAccountId);
        transaction.setAmount(amount);
        transaction.setClosingBalance(closingBalance);
        transaction.setType(type);
        transaction.setTimestamp(System.currentTimeMillis());
        transaction.setDescription(description);
        transaction.setStatus(status);
        return transaction;
    }
}
