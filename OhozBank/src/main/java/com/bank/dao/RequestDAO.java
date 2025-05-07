package com.bank.dao;

import com.bank.models.Request;
import java.util.List;

public interface RequestDAO {
    int saveRequest(Request request);
    Request getPendingRequestByUsername(String username);
    boolean markRequestAsApproved(String username);
    List<Request> getAllPendingRequests();
    boolean markRequestAsRejected(String username);

}
