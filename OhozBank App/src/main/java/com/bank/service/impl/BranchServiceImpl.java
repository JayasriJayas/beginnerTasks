package com.bank.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.BranchDAO;
import com.bank.factory.DaoFactory;
import com.bank.models.Branch;
import com.bank.service.BranchService;

import exception.QueryException;

public class BranchServiceImpl implements BranchService {

    private final BranchDAO branchDAO = DaoFactory.getBranchDAO();
    private static final Logger logger = Logger.getLogger(BranchServiceImpl.class.getName());

    @Override
    public boolean addBranch(Branch branch, long superadminId) throws SQLException, QueryException {
        try {
            if (branchDAO.isBranchExits(branch.getIfscCode())) {
                logger.warning("Branch already exists: " + branch.getBranchName());
                return false;
            }

            boolean result = branchDAO.saveBranch(branch, superadminId);
            if (result) {
                logger.info("Branch added successfully: " + branch.getBranchName());
            }
            return result;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding branch", e);
            return false;
        }
    }

    @Override
    public Branch getBranchById(long branchId) throws SQLException, QueryException {
        return branchDAO.getBranchById(branchId);
    }

    @Override
    public List<Branch> getAllBranches() throws SQLException, QueryException {
        return branchDAO.getAllBranches();
    }

    @Override
    public boolean updateBranchDetails(long userId, Branch updates) throws SQLException, QueryException {
        if (updates.getBranchId() == null) {
            logger.warning("BranchId is required for update.");
            return false;
        }

        Branch branch = new Branch();
        branch.setBranchId(updates.getBranchId());

        if (updates.getBranchName() != null) branch.setBranchName(updates.getBranchName());
        if (updates.getIfscCode() != null) branch.setIfscCode(updates.getIfscCode());
        if (updates.getLocation() != null) branch.setLocation(updates.getLocation());
        if (updates.getContact() != null) branch.setContact(updates.getContact());
        if (updates.getAdminId() != null) branch.setAdminId(updates.getAdminId());

        branch.setModifiedAt(System.currentTimeMillis());
        branch.setModifiedBy(userId);

        boolean updated = branchDAO.updateBranch(branch);
        if (updated) {
            logger.info("Branch updated: ID " + branch.getBranchId());
        }
        return updated;
    }

    @Override
    public Branch getBranchByIfscCode(String ifscCode) throws SQLException, QueryException {
        return branchDAO.findByIfscCode(ifscCode);
    }
}
