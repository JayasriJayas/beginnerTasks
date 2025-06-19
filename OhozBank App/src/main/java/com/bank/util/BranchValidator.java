package com.bank.util;

import com.bank.models.Branch;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BranchValidator {

    public static boolean isValidBranch(Branch branch, HttpServletResponse res) throws IOException {
        if (branch == null) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Branch data is missing.");
            return false;
        }
        if (branch.getBranchName() == null || branch.getBranchName().trim().isEmpty()) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Branch name is required.");
            return false;
        }
        if (branch.getIfscCode() == null || branch.getIfscCode().trim().isEmpty()) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "IFSC code is required.");
            return false;
        }
        if (branch.getLocation() == null || branch.getLocation().trim().isEmpty()) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Location is required.");
            return false;
        }
        if (branch.getContact() == null || String.valueOf(branch.getContact()).length() != 10) {
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Contact must be a 10-digit number.");
            return false;
        }
        return true;
    }
}
