
package com.bank.dao.impl;
	
import java.math.BigDecimal;
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
	public boolean saveTransaction(Transaction trans) throws QueryException, SQLException {
	    QueryBuilder qb = new QueryBuilder(new MySQLDialect());

	    if ("INTERNAL".equalsIgnoreCase(trans.getTransactionMode())) {
	        trans.setReceiverBank(null);
	        trans.setReceiverIFSC(null);
	    }

	    qb.insertInto("transaction",
	        "accountId",
	        "userId",
	        "transactionAccountId",
	        "amount",
	        "closingBalance",
	        "type",
	        "timestamp",
	        "status",
	        "description",
	        "transactionMode",
	        "receiverBank",
	        "receiverIFSC"
	    ).values(
	        trans.getAccountId(),
	        trans.getUserId(),
	        trans.getTransactionAccountId(),
	        trans.getAmount(),
	        trans.getClosingBalance(),
	        trans.getType(),
	        trans.getTimestamp(),
	        trans.getStatus(),
	        trans.getDescription(),
	        trans.getTransactionMode(),
	        trans.getReceiverBank(),
	        trans.getReceiverIFSC()
	    );

	    try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
	        QueryExecutor qe = new QueryExecutor(conn);
	        return qe.executeUpdate(qb.build(), qb.getParameters()) > 0;
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

        qb.select("t.*")
          .from("transaction t")
          .innerJoin("account a", "a.accountId = t.transactionAccountId") 
          .where("a.userId = ?", userId)                                   
          .andWhere("t.transactionMode = ?", "INTERNAL")                   
          .andBetween("t.timestamp", fromTimestamp, toTimestamp)
          .orderBy("t.timestamp").orderDirection("DESC")
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

        qb.select().aggregate("COUNT", "*").as("total")
          .from("transaction t")
          .innerJoin("account a", "a.accountId = t.transactionAccountId")  
          .where("a.userId = ?", userId)                             
          .andWhere("t.transactionMode = ?", "INTERNAL")                   
          .andBetween("t.timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> result = executor.executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("total")).intValue();
        }
    }

    @Override
    public List<Transaction> getReceivedTransactionsForAccount(long accountId, long fromTimestamp, long toTimestamp, int limit, int offset)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().from("transaction")
          .where("transactionAccountId = ?", accountId)
          .andWhere("transactionMode = ?", "INTERNAL")
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

        qb.select("t.*")
          .from("transaction t")
          .innerJoin("account a", "a.accountId = t.accountId")  
          .where("a.userId = ?", userId)                       
          .andWhere("t.type = ?", "TRANSFER")
          .andBetween("t.timestamp", fromTimestamp, toTimestamp)
          .orderBy("t.timestamp").orderDirection("DESC")
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

        qb.select().aggregate("COUNT", "*").as("total")
          .from("transaction t")
          .innerJoin("account a", "a.accountId = t.accountId")  
          .where("a.userId = ?", userId)
          .andWhere("t.type = ?", "TRANSFER")
          .andBetween("t.timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            List<Map<String, Object>> result = new QueryExecutor(conn)
                    .executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("total")).intValue();
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
    @Override
    public BigDecimal getTotalIncomeByUser(long userId) throws SQLException, QueryException {
        
//        QueryBuilder accountIdsSubQuery = new QueryBuilder(new MySQLDialect())
//            .select("accountId")
//            .from("account")
//            .where("userId = ?", userId);
       
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());

        
        qb.select().aggregate("SUM", "amount").as("total")
          .from("transaction")
          .openGroup()
            .where("type = ?", "DEPOSIT") 
            .andWhere("accountId IN ( select accountId from account where userId ="+ userId+")") 
          .closeGroup()
          .orWhere("transactionAccountId IN ( select accountId from account where userId ="+ userId+")") 
          .andWhere("type = ?", "TRANSFER") 
          .andWhere("MONTH(FROM_UNIXTIME(timestamp / 1000)) = MONTH(CURRENT_DATE)") 
          .andWhere("YEAR(FROM_UNIXTIME(timestamp / 1000)) = YEAR(CURRENT_DATE)"); 



        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> result = qe.executeQuery(qb.build(),qb.getParameters());

            if (!result.isEmpty()) {
                Object value = result.get(0).get("total");
                return value != null ? new BigDecimal(value.toString()) : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getTotalExpenseByUser(long userId) throws SQLException, QueryException {
      System.out.println("i am g");
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
//    	QueryBuilder accountIdsSubQuery = new QueryBuilder(new MySQLDialect())
//    	    .select("accountId")
//    	    .from("account")
//    	    .where("userId = ?", userId); 
    	
    		  qb  .select().aggregate("SUM", "amount").as("total")
    		    .from("transaction")
    		    .openGroup()
    		        .where("type = ?", "WITHDRAWAL")
    		        .andWhere("accountId IN ( select accountId from account where userId ="+ userId+")") 
    		    .closeGroup()
    		    .orWhere("accountId IN ( select accountId from account where userId ="+ userId+")")
    		    .andWhere("type = ?", "TRANSFER")
    		    .andWhere("MONTH(FROM_UNIXTIME(timestamp / 1000)) = MONTH(CURRENT_DATE)")
    		    .andWhere("YEAR(FROM_UNIXTIME(timestamp / 1000)) = YEAR(CURRENT_DATE)");
    

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> result = qe.executeQuery(qb.build(), qb.getParameters());

            if (!result.isEmpty()) {
                Object value = result.get(0).get("total");
                return value != null ? new BigDecimal(value.toString()) : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        }
    }
    @Override
    public BigDecimal getTotalIncomeByAccount(long accountId) throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());

        qb.select().aggregate("SUM", "amount").as("total")
          .from("transaction")
          .openGroup()
            .where("type = ?", "DEPOSIT") 
            .andWhere("accountId = ?", accountId) 
            .closeGroup()
          .orWhere("transactionAccountId = ?", accountId) 
          .andWhere("type = ?", "TRANSFER") 
          .andWhere("MONTH(FROM_UNIXTIME(timestamp / 1000)) = MONTH(CURRENT_DATE)") 
          .andWhere("YEAR(FROM_UNIXTIME(timestamp / 1000)) = YEAR(CURRENT_DATE)");

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> result = qe.executeQuery(qb.build(), qb.getParameters());

            if (!result.isEmpty()) {
                Object value = result.get(0).get("total");
                return value != null ? new BigDecimal(value.toString()) : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        }
    }
    @Override
    public BigDecimal getTotalExpenseByAccount(long accountId) throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());

        qb.select().aggregate("SUM", "amount").as("total")
          .from("transaction")
          .openGroup()
            .where("type = ?", "WITHDRAWAL")
            .andWhere("accountId = ?", accountId) 
          .closeGroup()
          .orWhere("accountId = ?", accountId)
          .andWhere("type = ?", "TRANSFER")
          .andWhere("MONTH(FROM_UNIXTIME(timestamp / 1000)) = MONTH(CURRENT_DATE)") 
          .andWhere("YEAR(FROM_UNIXTIME(timestamp / 1000)) = YEAR(CURRENT_DATE)");

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> result = qe.executeQuery(qb.build(), qb.getParameters());

            if (!result.isEmpty()) {
                Object value = result.get(0).get("total");
                return value != null ? new BigDecimal(value.toString()) : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        }
    }
    @Override
    public List<Transaction> getDepositTransactionsForAll(long fromTimestamp, long toTimestamp, int limit, int offset) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("*")
          .from("transaction")
          .where("type = ?", "DEPOSIT")
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
    public int countDepositTransactionsForAll(long fromTimestamp, long toTimestamp) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().aggregate("COUNT", "*").as("total")
          .from("transaction")
          .where("type = ?", "DEPOSIT")
          .andBetween("timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> result = executor.executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("total")).intValue();
        }
    }
    @Override
    public List<Transaction> getTransferTransactionsForAll(long fromTimestamp, long toTimestamp, int limit, int offset)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("*")
          .from("transaction")
          .where("type = ?", "TRANSFER")
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
    public int countTransferTransactionsForAll(long fromTimestamp, long toTimestamp)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().aggregate("COUNT", "*").as("total")
          .from("transaction")
          .where("type = ?", "TRANSFER")
          .andBetween("timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            List<Map<String, Object>> result = new QueryExecutor(conn).executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("total")).intValue();
        }
    }
    @Override
    public List<Transaction> getWithdrawTransactionsForAll(long fromTimestamp, long toTimestamp, int limit, int offset)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("*")
          .from("transaction")
          .where("type = ?", "WITHDRAWAL")
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
    public int countWithdrawTransactionsForAll(long fromTimestamp, long toTimestamp)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select().aggregate("COUNT", "*").as("total")
          .from("transaction")
          .where("type = ?", "WITHDRAWAL")
          .andBetween("timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            List<Map<String, Object>> result = new QueryExecutor(conn).executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("total")).intValue();
        }
    }
    @Override
    public List<Transaction> getDepositTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp, int limit, int offset)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());

        qb.select("t.*")
          .from("transaction t")
          .innerJoin("account a", "a.accountId = t.accountId")
          .where("a.branchId = ?", branchId)
          .andWhere("t.type = ?", "DEPOSIT")
          .andBetween("t.timestamp", fromTimestamp, toTimestamp)
          .orderBy("t.timestamp").orderDirection("DESC")
          .limit(limit)
          .offset(offset);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> rows = executor.executeQuery(qb.build(), qb.getParameters());
            return TransactionMapper.fromResultSet(rows);
        }
    }
    @Override
    public int countDepositTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());

        qb.select().aggregate("COUNT", "*").as("total")
          .from("transaction t")
          .innerJoin("account a", "a.accountId = t.accountId")  // Join to get branchId
          .where("a.branchId = ?", branchId)                    // Correct branch filter
          .andWhere("t.type = ?", "DEPOSIT")
          .andBetween("t.timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> result = executor.executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("total")).intValue();
        }
        

    }
    @Override
    public List<Transaction> getTransferTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp, int limit, int offset)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("t.*")
          .from("transaction t")
          .innerJoin("account a", "a.accountId = t.accountId")  // 🔗 join with account
          .where("a.branchId = ?", branchId)                    // ✅ filter using branchId from account
          .andWhere("t.type = ?", "TRANSFER")
          
          .andBetween("t.timestamp", fromTimestamp, toTimestamp)
          .orderBy("t.timestamp").orderDirection("DESC")
          .limit(limit)
          .offset(offset);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> rows = executor.executeQuery(qb.build(), qb.getParameters());
            return TransactionMapper.fromResultSet(rows);
        }
    }
    @Override
    public int countTransferTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());

        qb.select().aggregate("COUNT", "*").as("total")
          .from("transaction t")
          .innerJoin("account a", "a.accountId = t.accountId")
          .where("a.branchId = ?", branchId)
          .andWhere("t.type = ?", "TRANSFER")
          .andBetween("t.timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            List<Map<String, Object>> result = new QueryExecutor(conn).executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("total")).intValue();
        }
    }
    @Override
    public List<Transaction> getWithdrawTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp, int limit, int offset)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("t.*")
          .from("transaction t")
          .innerJoin("account a", "a.accountId = t.accountId") 
          .where("a.branchId = ?", branchId)                  
          .andWhere("t.type = ?", "WITHDRAWAL")
          .andBetween("t.timestamp", fromTimestamp, toTimestamp)
          .orderBy("t.timestamp").orderDirection("DESC")
          .limit(limit)
          .offset(offset);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            List<Map<String, Object>> rows = executor.executeQuery(qb.build(), qb.getParameters());
            return TransactionMapper.fromResultSet(rows);
        }
    }
    @Override
    public int countWithdrawTransactionsByBranch(long branchId, long fromTimestamp, long toTimestamp)
            throws SQLException, QueryException {

        QueryBuilder qb = new QueryBuilder(new MySQLDialect());

        qb.select().aggregate("COUNT", "*").as("total")
          .from("transaction t")
          .innerJoin("account a", "a.accountId = t.accountId")
          .where("a.branchId = ?", branchId)
          .andWhere("t.type = ?", "WITHDRAWAL")
          .andBetween("t.timestamp", fromTimestamp, toTimestamp);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            List<Map<String, Object>> result = new QueryExecutor(conn).executeQuery(qb.build(), qb.getParameters());
            return result.isEmpty() ? 0 : ((Number) result.get(0).get("total")).intValue();
        }
    }
    @Override
    public List<Map<String, Object>> getCurrentMonthOutgoingPerBranch() throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());

        qb.select("a.branchId")
          .aggregate("SUM", "t.amount").as("totalOutgoing")
          .from("transaction t")
          .innerJoin("account a", "a.accountId = t.accountId")
          .where("t.transactionMode = ?", "INTERNAL")
          .andWhere("t.type = ?", "TRANSFER")
          .andWhere("MONTH(FROM_UNIXTIME(t.timestamp / 1000)) = MONTH(CURRENT_DATE())")
          .andWhere("YEAR(FROM_UNIXTIME(t.timestamp / 1000)) = YEAR(CURRENT_DATE())")
          .groupBy("a.branchId");

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            return executor.executeQuery(qb.build(), qb.getParameters());
        }
    }
    @Override
    public List<Map<String, Object>> getCurrentMonthIncomingPerBranch() throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());

        qb.select("a.branchId")
          .aggregate("SUM", "t.amount").as("totalIncoming")
          .from("transaction t")
          .innerJoin("account a", "a.accountId = t.transactionAccountId")
          .where("t.transactionMode = ?", "INTERNAL")
          .andWhere("MONTH(FROM_UNIXTIME(t.timestamp / 1000)) = MONTH(CURRENT_DATE())")
          .andWhere("YEAR(FROM_UNIXTIME(t.timestamp / 1000)) = YEAR(CURRENT_DATE())")
          .groupBy("a.branchId");

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor executor = new QueryExecutor(conn);
            return executor.executeQuery(qb.build(), qb.getParameters());
        }
    }
    @Override
    public BigDecimal getTotalAmountByTypeAndBranch(String type, long branchId) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select()
        .aggregate("SUM", "t.amount").as("total")
        .from("transaction t")
        .innerJoin("account a", "t.accountId = a.accountId")
        .where("a.branchId = ?", branchId)
        .andWhere("t.type =?",type);


        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> result = qe.executeQuery(qb.build(), qb.getParameters());
          
            if (result.isEmpty() || result.get(0).get("total") == null) {
                return BigDecimal.ZERO;
            }
            BigDecimal amount =new BigDecimal(result.get(0).get("total").toString());
            return amount;
        }
    }
    @Override
    public BigDecimal getTotalAmountByType(String type) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select()
          .aggregate("SUM", "amount").as("total")
          .from("transaction")
          .where("type = ?", type);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> result = qe.executeQuery(qb.build(), qb.getParameters());

            if (result.isEmpty() || result.get(0).get("total") == null) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(result.get(0).get("total").toString());
        }
    }
    @Override
    public List<Map<String, Object>> getTopBranchesByTransactionCount(int limit) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("b.branchId", "b.branchName", "COUNT(t.transactionId) AS transactionCount")
          .from("transaction t")
          .innerJoin("account a", "t.accountId = a.accountId")
          .innerJoin("branch b", "a.branchId = b.branchId")
          .groupBy("b.branchId", "b.branchName")
          .orderBy("transactionCount").orderDirection("DESC") 
          .limit(limit);
        String query = qb.build();
        System.out.println(query);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            return qe.executeQuery(query);
        }
    }
    @Override
    public List<Map<String, Object>> getDailyTransactionCountsForBranch(long branchId, long fromTimestamp, long toTimestamp) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("DATE(FROM_UNIXTIME(t.timestamp / 1000)) AS date", 
                  "COUNT(t.transactionId) AS transactionCount")
          .from("transaction t")
          .innerJoin("account a", "t.accountId = a.accountId")
          .where("a.branchId = ?", branchId)
          .andBetween("t.timestamp", fromTimestamp, toTimestamp)
          .groupBy("date")
          .orderBy("date");

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            return qe.executeQuery(qb.build(), qb.getParameters());
        }
    }
    @Override
    public List<Map<String, Object>> getAccountTransactionSummaryByBranch(long branchId, int limit) throws SQLException, QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("a.accountId",
                  "SUM(CASE WHEN t.type = 'WITHDRAWAL' THEN t.amount ELSE 0 END) AS withdrawal",
                  "SUM(CASE WHEN t.type = 'DEPOSIT' THEN t.amount ELSE 0 END) AS deposit",
                  "SUM(CASE WHEN t.type = 'TRANSFER' THEN t.amount ELSE 0 END) AS transfer",
                  "SUM(t.amount) AS total")
          .from("transaction t")
          .innerJoin("account a", "t.accountId = a.accountId")
          .where("a.branchId = ?", branchId)
          .groupBy("a.accountId")
          .orderBy("total DESC")  
          .limit(limit); 

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            return qe.executeQuery(qb.build(), qb.getParameters());
        }
    }





}
