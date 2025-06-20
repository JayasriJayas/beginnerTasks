package com.bank.dao;

import java.sql.SQLException;
import java.util.List;

import com.bank.models.Branch;

import exception.QueryException;

public interface BranchDAO {
	   long getBranchIdByAdminId(long adminId)throws SQLException, QueryException;
	   boolean isBranchExits(String ifscCode) throws SQLException, QueryException;
	   boolean saveBranch(Branch branch,long superadminadminId) throws SQLException, QueryException;
	   boolean updateBranch(Branch branch) throws SQLException, QueryException;
	   Branch getBranchById(long branchId) throws SQLException, QueryException;
	   List<Branch> getAllBranches() throws SQLException, QueryException;
	   Branch findByIfscCode(String ifscCode)throws SQLException, QueryException;


}
