package com.bank.mapper;

import com.bank.enums.TransactionStatus;
import com.bank.enums.TransactionType;
import com.bank.models.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransactionMapper {

    public static List<Transaction> fromResultSet(List<Map<String,Object>> rows) {
        List<Transaction> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Transaction txn = new Transaction();

            txn.setTransactionId((Long) row.get("transactionId"));
            txn.setAccountId((Long) row.get("accountId"));
            txn.setUserId((Long) row.get("userId"));
            Object txnAccId = row.get("transactionAccountId");
            if (txnAccId != null) {
                txn.setTransactionAccountId(((Number) txnAccId).longValue());
            } else {
                txn.setTransactionAccountId(null);
            }
            txn.setAmount((BigDecimal) row.get("amount"));
            txn.setClosingBalance((BigDecimal) row.get("closingBalance"));
            txn.setType(TransactionType.valueOf((String) row.get("type")));
            txn.setTimestamp((Long) row.get("timestamp"));
            txn.setDescription((String) row.get("description"));
            txn.setStatus(TransactionStatus.valueOf((String) row.get("status")));

            list.add(txn);
        }
        return list;
    }
}


