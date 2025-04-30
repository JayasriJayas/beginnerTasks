package com.bank.service;

import com.bank.models.User;
import com.bank.models.Request;

public interface UserService {
    boolean approveRequest(String username);
    boolean registerRequest(Request request);
    User authenticate(String username, String password);
    boolean rejectRequest(String username);

}
