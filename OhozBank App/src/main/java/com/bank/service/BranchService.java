package com.bank.service;

import java.sql.SQLException;
import java.util.List;

import com.bank.models.Branch;

import exception.QueryException;

public interface BranchService {
    boolean addBranch(Branch branch,long adminId) throws SQLException, QueryException;
    Branch getBranchById(long branchId) throws  SQLException, QueryException;
    List<Branch> getAllBranches() throws  SQLException, QueryException;
    boolean updateBranchDetails(long userId,Branch updates) throws SQLException, QueryException;
    Branch getBranchByIfscCode(String ifscCode) throws SQLException, QueryException;
} 