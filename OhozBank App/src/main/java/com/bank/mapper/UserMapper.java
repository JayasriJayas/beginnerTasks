package com.bank.mapper;

import com.bank.enums.Gender;
import com.bank.enums.UserStatus;
import com.bank.models.Request;
import com.bank.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserMapper {

        public static User fromRequest(Request req) {
            if (req == null) return null;

            User user = new User();

            user.setUserId(req.getId()); 
            user.setUsername(req.getUsername());
            user.setPassword(req.getPassword());
            System.out.println(req.getName());
            user.setName(req.getName());
            user.setEmail(req.getEmail());
            user.setPhone((long) req.getPhone());
            user.setGender(req.getGender());
            user.setRoleId(3);              
            user.setStatus(UserStatus.ACTIVE);   
            user.setCreatedDate(System.currentTimeMillis());
            user.setModifiedBy(req.getId());

            return user;
        }
}

