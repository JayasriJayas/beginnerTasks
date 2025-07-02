package com.bank.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.models.Branch;
import com.bank.models.PaginatedResponse;

import exception.QueryException;

public interface BranchService {
    boolean addBranch(Branch branch,long adminId) throws SQLException, QueryException;
    Branch getBranchById(long branchId) throws  SQLException, QueryException;
    boolean updateBranchDetails(long userId,Branch updates) throws SQLException, QueryException;
    Branch getBranchByIfscCode(String ifscCode) throws SQLException, QueryException;
    PaginatedResponse<Branch> getAllBranches(int pageNumber, int pageSize) throws SQLException, QueryException;
    int getTotalBranchCount() throws SQLException, QueryException;
    public List<Branch> getBranchFunds() throws SQLException, QueryException;
    List<Map<String, Object>> getAllBranch() throws SQLException, QueryException;


} 