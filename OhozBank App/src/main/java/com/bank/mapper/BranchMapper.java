package com.bank.mapper;

import com.bank.models.Branch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BranchMapper {

    public static Branch fromResultSet(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) return null;

        Map<String, Object> row = rows.get(0);
        Branch branch = new Branch();
        branch.setBranchId(((Number) row.get("branchId")).longValue());
        branch.setBranchName((String) row.get("branchName"));
        branch.setIfscCode((String) row.get("ifscCode"));
        branch.setLocation((String) row.get("location"));
        branch.setContact(((Number) row.get("contact")).longValue());

        return branch;
    }

    public static List<Branch> toMapResult(List<Map<String, Object>> rows) {
        List<Branch> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Branch branch = new Branch();
            branch.setBranchId(((Number) row.get("branchId")).longValue());
            branch.setBranchName((String) row.get("branchName"));
            branch.setIfscCode((String) row.get("ifscCode"));
            branch.setLocation((String) row.get("location"));
            branch.setContact(((Number) row.get("contact")).longValue());

            list.add(branch);
        }
        return list;
    }
}
