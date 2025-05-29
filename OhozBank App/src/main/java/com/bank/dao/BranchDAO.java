package com.bank.dao;

import java.sql.SQLException;

import com.bank.models.Branch;

import exception.QueryException;

public interface BranchDAO {
	   long getBranchIdByAdminId(long adminId)throws SQLException, QueryException;
	   boolean isBranchExits(String ifscCode) throws SQLException, QueryException;
	   boolean saveBranch(Branch branch,long superadminadminId) throws SQLException, QueryException;
}
