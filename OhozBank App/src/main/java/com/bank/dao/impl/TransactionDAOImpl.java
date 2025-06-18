
package com.bank.dao.impl;
	
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.TransactionDAO;
import com.bank.mapper.TransactionMapper;
import com.bank.models.Transaction;
import com.dialect.MySQLDialect;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;  

import exception.QueryException;
	
public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public boolean saveTransaction(Transaction trans) throws QueryException,SQLException {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    	qb.insertInto("transaction","accountId","userId","transactionAccountId","amount","closingBalance","type","timestamp","status","description")
    	  .values(trans.getAccountId(),trans.getUserId(),trans.getTransactionAccountId(),trans.getAmount(),trans.getClosingBalance(),trans.getType(),trans.getTimestamp(),trans.getStatus(),trans.getDescription());
    	try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
		     
        return qe.executeUpdate(qb.build(), qb.getParameters())> 0;
    	}
        }
  

    @Override
    public List<Transaction> getTransactionsByAccountId(long accountId) throws SQLException, QueryException {
       
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("*").from("transaction").where("accountId = ?", accountId).orWhere("transactionAccountId = ?").orderBy("timestamp").orderDirection("DESC");
        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
        List<Map<String,Object>> rows = qe.executeQuery(qb.build(), qb.getParameters());
        return TransactionMapper.fromResultSet(rows);
        }
           
        
    }


    @Override
    public List<Transaction> getTransactionsByAccountIdAndDateRange(long accountId, long fromMillis, long toMillis) throws SQLException, QueryException {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("*").from("transaction").where("accountId = ?", accountId).orWhere("transactionAccountId = ?",accountId).andBetween("timestamp",fromMillis,toMillis).orderBy("timestamp").orderDirection("DESC");
        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
        List<Map<String,Object>> rows = qe.executeQuery(qb.build(), qb.getParameters());
        return TransactionMapper.fromResultSet(rows);
        }
    }
}
