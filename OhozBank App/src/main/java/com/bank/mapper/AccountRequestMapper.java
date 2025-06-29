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
        accountRequest.setApprovedBy(approvedByObj != null ? ((Number) approvedByObj).longValue() : null);

        Object approvedAtObj = row.get("approvedAt");
        accountRequest.setApprovedAt(approvedAtObj != null ? ((Number) approvedAtObj).longValue() : null);

        Object rejectionReasonObj = row.get("rejectionReason");
        accountRequest.setRejectionReason(rejectionReasonObj != null ? rejectionReasonObj.toString() : null);

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

            Object approvedByObj = row.get("approvedBy");
            req.setApprovedBy(approvedByObj != null ? ((Number) approvedByObj).longValue() : null);

            Object approvedAtObj = row.get("approvedAt");
            req.setApprovedAt(approvedAtObj != null ? ((Number) approvedAtObj).longValue() : null);

            Object rejectionReasonObj = row.get("rejectionReason");
            req.setRejectionReason(rejectionReasonObj != null ? rejectionReasonObj.toString() : null);

            requests.add(req);
        }

        return requests;
    }
}
