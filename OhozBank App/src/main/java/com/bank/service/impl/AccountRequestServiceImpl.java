package com.bank.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import com.bank.models.AccountRequest;
import com.bank.service.AccountRequestService;

import exception.QueryException;

import com.bank.dao.AccountRequestDAO;
import com.bank.dao.impl.AccountRequestDAOImpl;
import com.bank.enums.RequestStatus;


public class AccountRequestServiceImpl implements AccountRequestService {
	AccountRequestDAO accountReqDAO = new AccountRequestDAOImpl();
	  @Override
      public List<AccountRequest> getAllRequests() throws SQLException,QueryException {
          return accountReqDAO.fetchAllRequests();
      }

      @Override
      public List<AccountRequest> getRequestsByAdminBranch(long adminId) throws SQLException,QueryException {
          return accountReqDAO.fetchRequestsByAdminBranch(adminId);
      }
      @Override
      public boolean createAccountRequest(long userId, long branchId) throws SQLException,QueryException {
          AccountRequest request = new AccountRequest();
          request.setUserId(userId);
          request.setBranchId(branchId);
          request.setStatus(RequestStatus.PENDING);
          request.setCreatedAt(System.currentTimeMillis());

          return accountReqDAO.save(request);
      }

      @Override
      public boolean approveAccountRequest(long requestId, long adminId)throws SQLException,QueryException {
          try {
          	
              return accountReqDAO.approveRequest(requestId, adminId);
          } catch (Exception e) {
//              logger.log(Level.SEVERE, "Error approving user request", e);
              return false;
          }
      }
}
