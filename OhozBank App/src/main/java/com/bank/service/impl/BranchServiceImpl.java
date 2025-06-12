package com.bank.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.models.Branch;
import com.bank.service.BranchService;
import com.bank.dao.BranchDAO;
import com.bank.dao.impl.BranchDAOImpl;
import exception.QueryException;

public class BranchServiceImpl implements BranchService{
	private final BranchDAO branchDAO = new BranchDAOImpl();
    private final Logger logger = Logger.getLogger(BranchServiceImpl.class.getName());
    
    @Override
	public boolean addBranch(Branch branch,long superadminadminId) throws SQLException, QueryException{
		 try {
			 if(branchDAO.isBranchExits(branch.getIfscCode())) {
				 logger.warning("Branch already exists: " + branch.getBranchName());
	                return false;
			 }
			 return branchDAO.saveBranch(branch,superadminadminId);
		 }
		
	    catch (Exception e) {
          logger.log(Level.SEVERE, "Error adding admin", e);
          return false;
       }
		 
	 }

    @Override
    public Branch getBranchById(long branchId) throws SQLException, QueryException{
        return branchDAO.getBranchById(branchId);
    }

    @Override
    public List<Branch> getAllBranches()throws SQLException, QueryException{
        return branchDAO.getAllBranches();
    }
 
}
