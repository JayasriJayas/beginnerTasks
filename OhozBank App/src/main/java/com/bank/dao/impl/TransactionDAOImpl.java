
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
    public List<Transaction> getTransactionsByAccountIdAndDateRange(long accountId, long fromMillis, long toMillis, int pageSize, int offset) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        
        qb.select("*")
          .from("transaction")
          .openGroup()
          .where("accountId = ?", accountId)
          .orWhere("transactionAccountId = ?", accountId)
          .closeGroup()
          .andBetween("timestamp", fromMillis, toMillis)
          .orderBy("timestamp").orderDirection("DESC")
          .limit(pageSize)
          .offset(offset); 
System.out.println(qb.build());
        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> rows = qe.executeQuery(qb.build(), qb.getParameters());
            System.out.println(qb.getParameters());
            return TransactionMapper.fromResultSet(rows);
        }
    }
    @Override
    public int countTransactionsByAccountIdAndDateRange(long accountId, long fromMillis, long toMillis) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());

        qb.select().aggregate("COUNT", "*").as("total")
          .from("transaction")
          .openGroup()
          .where("accountId = ?", accountId)
          .orWhere("transactionAccountId = ?", accountId)
          .closeGroup()
          .andBetween("timestamp", fromMillis, toMillis);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> rows = qe.executeQuery(qb.build(), qb.getParameters());
            
            if (!rows.isEmpty()) {
                Object totalObj = rows.get(0).get("total");
                return totalObj != null ? ((Number) totalObj).intValue() : 0;
            } else {
                return 0;
            }
        }
    }
    @Override
    public List<Transaction> getReceivedTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int limit, int offset)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().from("transaction")
          .where("transactionAccountId IS NOT NULL")
          .andWhere("userId = ?", userId)
          .andBetween("timestamp", fromTimestamp, toTimestamp)
          .orderBy("timestamp").orderDirection("DESC")
          .limit(limit)
          .offset(offset);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> rows = executor.executeQuery(qb.build(), qb.getParameters());
            return TransactionMapper.fromResultSet(rows);
        }
    }
    @Override
    public int countReceivedTransactionsForUser(long userId, long fromTimestamp, long toTimestamp)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().aggregate("COUNT", "*").from("transaction")
          .where("transactionAccountId IS NOT NULL")
          .andWhere("userId = ?", userId).openGroup()
          .andBetween("timestamp", fromTimestamp, toTimestamp).closeGroup();

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> result = executor.executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("COUNT(*)")).intValue();
        }
    }
    @Override
    public List<Transaction> getReceivedTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int limit, int offset)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().from("transaction")
          .where("transactionAccountId = ?", accountId)
          .andBetween("timestamp", fromTimestamp, toTimestamp)
          .orderBy("timestamp").orderDirection("DESC")
          .limit(limit)
          .offset(offset);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> rows = executor.executeQuery(qb.build(), qb.getParameters());
            return TransactionMapper.fromResultSet(rows);
        }
    }
    @Override
    public int countReceivedTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().aggregate("COUNT", "*").from("transaction")
        .where("transactionAccountId = ?", accountId)
          .andBetween("timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> result = executor.executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("COUNT(*)")).intValue();
        }
    }		
    @Override
    public List<Transaction> getDepositTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int limit, int offset)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().from("transaction")
          .where("userId = ?", userId)
          .andWhere("type = ?", "DEPOSIT")
          .andBetween("timestamp", fromTimestamp, toTimestamp)
          .orderBy("timestamp").orderDirection("DESC")
          .limit(limit)
          .offset(offset);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> rows = executor.executeQuery(qb.build(), qb.getParameters());
            return TransactionMapper.fromResultSet(rows);
        }
    }
    @Override
    public int countDepositTransactionsForUser(long userId, long fromTimestamp, long toTimestamp)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().aggregate("COUNT", "*").from("transaction")
          .where("userId = ?", userId)
          .andWhere("type = ?", "DEPOSIT")
          .andBetween("timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            List<Map<String, Object>> result = new QueryExecutor(conn)
                    .executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("COUNT(*)")).intValue();
        }
    }
    @Override
    public List<Transaction> getDepositTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int limit, int offset)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().from("transaction")
          .where("accountId = ?", accountId)
          .andWhere("type = ?", "DEPOSIT")
          .andBetween("timestamp", fromTimestamp, toTimestamp)
          .orderBy("timestamp").orderDirection("DESC")
          .limit(limit)
          .offset(offset);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> rows = executor.executeQuery(qb.build(), qb.getParameters());
            return TransactionMapper.fromResultSet(rows);
        }
    }
    @Override
    public int countDepositTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().aggregate("COUNT", "*").from("transaction")
          .where("accountId = ?", accountId)
          .andWhere("type = ?", "DEPOSIT")
          .andBetween("timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            List<Map<String, Object>> result = new QueryExecutor(conn)
                    .executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("COUNT(*)")).intValue();
        }
    }
    @Override
    public List<Transaction> getWithdrawTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int limit, int offset)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().from("transaction")
          .where("userId = ?", userId)
          .andWhere("type = ?", "WITHDRAWAL")
          .andBetween("timestamp", fromTimestamp, toTimestamp)
          .orderBy("timestamp").orderDirection("DESC")
          .limit(limit)
          .offset(offset);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> rows = executor.executeQuery(qb.build(), qb.getParameters());
            return TransactionMapper.fromResultSet(rows);
        }
    }
    @Override
    public int countWithdrawTransactionsForUser(long userId, long fromTimestamp, long toTimestamp)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().aggregate("COUNT", "*").from("transaction")
          .where("userId = ?", userId)
          .andWhere("type = ?", "WITHDRAWAL")
          .andBetween("timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            List<Map<String, Object>> result = new QueryExecutor(conn)
                    .executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("COUNT(*)")).intValue();
        }
    }
    @Override
    public List<Transaction> getWithdrawTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int limit, int offset)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().from("transaction")
          .where("accountId = ?", accountId)
          .andWhere("type = ?", "WITHDRAWAL")
          .andBetween("timestamp", fromTimestamp, toTimestamp)
          .orderBy("timestamp").orderDirection("DESC")
          .limit(limit)
          .offset(offset);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> rows = executor.executeQuery(qb.build(), qb.getParameters());
            return TransactionMapper.fromResultSet(rows);
        }
    }
    @Override
    public int countWithdrawTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().aggregate("COUNT", "*").from("transaction")
          .where("accountId = ?", accountId)
          .andWhere("type = ?", "WITHDRAWAL")
          .andBetween("timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            List<Map<String, Object>> result = new QueryExecutor(conn)
                    .executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("COUNT(*)")).intValue();
        }
    }
    @Override
    public List<Transaction> getTransferTransactionsForUser(long userId, long fromTimestamp, long toTimestamp, int limit, int offset)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().from("transaction")
          .where("userId = ?", userId)
          .andWhere("type = ?", "TRANSFER")
          .andBetween("timestamp", fromTimestamp, toTimestamp)
          .orderBy("timestamp").orderDirection("DESC")
          .limit(limit)
          .offset(offset);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> rows = executor.executeQuery(qb.build(), qb.getParameters());
            return TransactionMapper.fromResultSet(rows);
        }
    }
    @Override
    public int countTransferTransactionsForUser(long userId, long fromTimestamp, long toTimestamp)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().aggregate("COUNT", "*").from("transaction")
          .where("userId = ?", userId)
          .andWhere("type = ?", "TRANSFER")
          .andBetween("timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            List<Map<String, Object>> result = new QueryExecutor(conn)
                    .executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("COUNT(*)")).intValue();
        }
    }
    @Override
    public List<Transaction> getTransferTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int limit, int offset)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().from("transaction")
          .where("accountId = ?", accountId)
          .andWhere("type = ?", "TRANSFER")
          .andBetween("timestamp", fromTimestamp, toTimestamp)
          .orderBy("timestamp").orderDirection("DESC")
          .limit(limit)
          .offset(offset);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> rows = executor.executeQuery(qb.build(), qb.getParameters());
            return TransactionMapper.fromResultSet(rows);
        }
    }
    @Override
    public int countTransferTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().aggregate("COUNT", "*").from("transaction")
          .where("accountId = ?", accountId)
          .andWhere("type = ?", "TRANSFER")
          .andBetween("timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            List<Map<String, Object>> result = new QueryExecutor(conn)
                    .executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("COUNT(*)")).intValue();
        }
    }

    @Override
    public List<Transaction> getRecentTransactionsForUser(long userId, int limit) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());

        qb.select("*")
          .from("transaction")
          .where("userId = ?", userId)
          .orderBy("timestamp").orderDirection("DESC")
          .limit(limit);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> rows = qe.executeQuery(qb.build(), qb.getParameters());
            return TransactionMapper.fromResultSet(rows);
        }
    }
    









}
