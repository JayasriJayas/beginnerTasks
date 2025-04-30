package com.bank.mapper;

import com.bank.models.Request;
import com.bank.models.User;

public class UserMapper {

    public static User fromRequest(Request r) {
        User user = new User();
        user.setUsername(r.getUsername());
        user.setPassword(r.getPassword());
        user.setEmail(r.getEmail());
        user.setPhone(r.getPhone());
        user.setGender(r.getGender());
        user.setDob(r.getDob());
        user.setAddress(r.getAddress());
        user.setMaritalStatus(r.getMaritalStatus());
        user.setAadharNo(r.getAadharNo());
        user.setPanNo(r.getPanNo());
        user.setBranchId(r.getBranchId());
        user.setBranchName(r.getBranchName());
        user.setOccupation(r.getOccupation());
        user.setAnnualIncome(r.getAnnualIncome());
        user.setRoleId(3);  // default role
        user.setActive(true);
        return user;
    }
}
