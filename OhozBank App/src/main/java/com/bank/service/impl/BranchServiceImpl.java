package com.bank.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.dao.BranchDAO;
import com.bank.factory.DaoFactory;
import com.bank.models.Branch;
import com.bank.models.PaginatedResponse;
import com.bank.models.Transaction;
import com.bank.service.BranchService;
import com.bank.util.PaginationUtil;

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
    public  PaginatedResponse<Branch> getAllBranches(int pageNumber, int pageSize) throws SQLException, QueryException {
    	pageNumber = PaginationUtil.validatePageNumber(pageNumber);
        pageSize = PaginationUtil.validatePageSize(pageSize);
        int offset = PaginationUtil.calculateOffset(pageNumber, pageSize);
        int total = branchDAO.countGetAllBranch();
        List<Branch> branch = branchDAO.getAllBranches(pageSize, offset);
       

        return new PaginatedResponse<>(branch, pageNumber, pageSize, total);
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
    @Override
    public int getTotalBranchCount() throws SQLException, QueryException {
        return branchDAO.countAllBranches();
    }
    @Override
    public List<Branch> getBranchFunds() throws SQLException, QueryException {
        List<Map<String, Object>> rows = branchDAO.getBranchFunds();
        List<Branch> summaries = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            Branch summary = new Branch();
            summary.setBranchId(((Number) row.get("branchId")).longValue());
            summary.setBranchName((String) row.get("branchName"));
            Object total = row.get("totalFunds");
            summary.setTotalFunds(total != null ? (BigDecimal) total : BigDecimal.ZERO);
            summaries.add(summary);
        }

        return summaries;
    }


}
