package com.bank.dao.impl;
	
import com.bank.connection.DBConnectionPool;
import com.bank.dao.TransactionDAO;
import com.bank.enums.TransactionType;
import com.bank.models.Transaction;
	
import com.dialect.MySQLDialect;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;
	
import exception.QueryException;
	
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
	
public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public boolean saveTransaction(Transaction trans) throws QueryException,SQLException {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    	qb.insertInto("transaction","accountId","userId","transactionAccountId","amount","closingBalance","type","timestamp","status","description")
    	  .values(trans.getAccountId(),trans.getUserId(),trans.getTransactionAccountId(),trans.getAmount(),trans.getClosingBalance(),trans.getType(),trans.getTimestamp(),trans.getStatus(),trans.getDescription());
       	String query = qb.build();
    	List<Object> params = qb.getParameters();
	    QueryExecutor qe = new QueryExecutor(DBConnectionPool.getInstance().getConnection());
		     
        return qe.executeUpdate(query, params)> 0;
	       
        }
  }

//    @Override
//    public List<Transaction> getTransactionsByAccountId(long accountId) {
//        List<Transaction> list = new ArrayList<>();
//        String sql = "SELECT * FROM transactions " +
//                     "WHERE from_account_id = ? OR to_account_id = ? ORDER BY timestamp DESC";
//
//        try (Connection conn = QueryBuilder.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setLong(1, accountId);
//            ps.setLong(2, accountId);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                Transaction txn = new Transaction();
//                txn.setTransactionId(rs.getLong("transaction_id"));
//                txn.setFromAccountId(rs.getLong("from_account_id"));
//
//                long toAccountId = rs.getLong("to_account_id");
//                txn.setToAccountId(rs.wasNull() ? null : toAccountId);
//
//                txn.setAmount(rs.getDouble("amount"));
//                txn.setType(TransactionType.valueOf(rs.getString("type")));
//                txn.setTimestamp(rs.getLong("timestamp"));
//                txn.setDescription(rs.getString("description"));
//
//                list.add(txn);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace(); // Optional: log
//        }
//
//        return list;
//    }

