package com.bank.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bank.models.Branch;

import exception.QueryException;

public interface BranchDAO {
	   long getBranchIdByAdminId(long adminId)throws SQLException, QueryException;
	   boolean isBranchExits(String ifscCode) throws SQLException, QueryException;
	   boolean saveBranch(Branch branch,long superadminadminId) throws SQLException, QueryException;
	   boolean updateBranch(Branch branch) throws SQLException, QueryException;
	   Branch getBranchById(long branchId) throws SQLException, QueryException;
	   List<Branch> getAllBranches(int limit, int offset) throws SQLException, QueryException;
	   Branch findByIfscCode(String ifscCode)throws SQLException, QueryException;
	   int countGetAllBranch() throws SQLException, QueryException ;
	   int countAllBranches() throws SQLException, QueryException;
	   List<Map<String, Object>> getBranchFunds() throws SQLException, QueryException;
	   List<Map<String, Object>> getAllBranches() throws SQLException, QueryException;





}
