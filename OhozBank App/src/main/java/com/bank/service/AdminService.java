package com.bank.service;

import com.bank.models.User;

public interface AdminService {
    boolean addAdmin(User user,long adminID);
}