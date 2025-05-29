package com.bank.service;

import java.sql.SQLException;

import com.bank.models.Branch;

import exception.QueryException;

public interface BranchService {
    boolean addBranch(Branch branch,long adminId) throws SQLException, QueryException;
} 