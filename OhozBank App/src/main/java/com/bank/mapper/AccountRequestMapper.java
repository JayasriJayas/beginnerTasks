package com.bank.mapper;

import com.bank.enums.RequestStatus;
import com.bank.models.AccountRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccountRequestMapper {

        public static AccountRequest fromResultSet(List<Map<String, Object>> rows) {
    
        if (rows == null || rows.isEmpty()) {
            return null;
        }
        Map<String, Object> row = rows.get(0);

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setRequestId(((Number) row.get("requestId")).longValue());
        accountRequest.setUserId(((Number) row.get("userId")).longValue());
        accountRequest.setBranchId(((Number) row.get("branchId")).longValue());
        accountRequest.setStatus(RequestStatus.valueOf((String) row.get("status")));
        accountRequest.setCreatedAt(((Number) row.get("createdAt")).longValue());
        Object approvedByObj = row.get("approvedBy");
        if (approvedByObj != null) {
            accountRequest.setApprovedBy(((Number) approvedByObj).longValue());
        } else {
            accountRequest.setApprovedBy(null);
        }

        Object approvedAtObj = row.get("approvedAt");
        if (approvedAtObj != null) {
            accountRequest.setApprovedAt(((Number) approvedAtObj).longValue());
        } else {
            accountRequest.setApprovedAt(null); 
        }
        return accountRequest;
    }
        public static List<AccountRequest> mapToRequests(List<Map<String, Object>> rows) {
            List<AccountRequest> requests = new ArrayList<>();
            if (rows == null) return requests;

            for (Map<String, Object> row : rows) {
                AccountRequest req = new AccountRequest();

                req.setRequestId(((Number) row.get("requestId")).longValue());
                req.setUserId(((Number) row.get("userId")).longValue());
                req.setBranchId(((Number) row.get("branchId")).longValue());
                req.setStatus(RequestStatus.valueOf((String) row.get("status")));
                req.setCreatedAt(((Number) row.get("createdAt")).longValue());
             
                requests.add(req);
            }

            return requests;
        }
}
