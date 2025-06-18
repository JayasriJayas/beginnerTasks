package com.bank.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.connection.DBConnectionPool;
import com.bank.dao.AccountRequestDAO;
import com.bank.mapper.AccountRequestMapper;
import com.bank.models.AccountRequest;
import com.dialect.MySQLDialect;
import com.querybuilder.QueryBuilder;
import com.querybuilder.QueryExecutor;

import exception.QueryException;

public class AccountRequestDAOImpl implements AccountRequestDAO {

    @Override
    public List<AccountRequest> fetchAllRequests() throws SQLException,QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("*").from("accountRequest");

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> rs = qe.executeQuery(qb.build());
            
            return AccountRequestMapper.mapToRequests(rs);
        }
    }

    @Override
    public List<AccountRequest> fetchRequestsByAdminBranch(long adminId)throws SQLException,QueryException {
        QueryBuilder qb = new QueryBuilder(new MySQLDialect());
        qb.select("ar.*")
          .from("accountRequest ar")
          .innerJoin("admin a", "a.branchId = ar.branchId")
          .where("a.adminId = ?", adminId);

        try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
            List<Map<String, Object>> rs = qe.executeQuery(qb.build(), qb.getParameters());
           
            return AccountRequestMapper.mapToRequests(rs);
        }
    }
    @Override
    public boolean save(AccountRequest request) throws SQLException,QueryException {
    	QueryBuilder qb = new QueryBuilder(new MySQLDialect());
    	qb.insertInto("accountRequest","userId","branchId","status","createdAt").values(request.getUserId(),request.getBranchId(),request.getStatus(),System.currentTimeMillis());

    	try (Connection conn = DBConnectionPool.getInstance().getConnection()) {
            QueryExecutor qe = new QueryExecutor(conn);
        return  qe.executeUpdate( qb.build(), qb.getParameters())>0;
    	}
    }

    
}
