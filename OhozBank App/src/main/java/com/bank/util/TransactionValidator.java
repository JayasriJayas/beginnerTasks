package com.bank.util;

import com.bank.models.Transaction;

import java.math.BigDecimal;

public class TransactionValidator {

    public static String validateDepositOrWithdraw(Transaction transaction) {
        if (transaction == null) return "Request body is empty";

        if (transaction.getAccountId() <= 0) {
            return "Invalid account ID.";
        }

        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return "Amount must be greater than zero.";
        }

        return null;
    }

    public static String validateTransfer(Transaction transaction) {
        if (transaction == null) return "Request body is empty";

        if (transaction.getAccountId() <= 0 || transaction.getTransactionAccountId() <= 0) {
            return "Both sender and receiver account IDs must be valid.";
        }

        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return "Transfer amount must be greater than zero.";
        }

        return null;
    }
}
