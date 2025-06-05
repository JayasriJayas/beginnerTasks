package com.bank.mapper;

import java.util.List;
import java.util.Map;

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
        
        public static User fromResultSet(List<Map<String,Object>> rows) {
        	 
        	 if (rows == null || rows.isEmpty()) return null;
    		 Map<String, Object> row = rows.get(0); 
    		 User user = new User();

    		 if (row.get("userId") != null)
    		     user.setUserId(((Number) row.get("userId")).longValue());

    		 if (row.get("username") != null)
    		     user.setUsername((String) row.get("username"));

    		 if (row.get("password") != null)
    		     user.setPassword((String) row.get("password"));

    		 if (row.get("name") != null)
    		     user.setName((String) row.get("name"));

    		 if (row.get("email") != null)
    		     user.setEmail((String) row.get("email"));

    		 if (row.get("phone") != null)
    		     user.setPhone(((Number) row.get("phone")).longValue());

    		 if (row.get("gender") != null)
    		     user.setGender(Gender.valueOf(row.get("gender").toString().toUpperCase()));

    		 if(row.get("roleId")!=null)
    			 user.setRoleId(((Integer) row.get("roleId")));
    		 if (row.get("status") != null)
    		     user.setStatus(UserStatus.valueOf(row.get("status").toString().toUpperCase()));

    		 if (row.get("createdAt") != null)
    		     user.setCreatedDate(((Number) row.get("createdAt")).longValue());

    		 if (row.get("modifiedBy") != null)
    		     user.setModifiedBy(((Number) row.get("modifiedBy")).longValue());

    		 if (row.get("modifiedAt") != null)
    		     user.setModifiedAt(((Number) row.get("modifiedAt")).longValue());

    		 if (row.get("branchId") != null)
    		     user.setBranchId(((Number) row.get("branchId")).longValue());

    		 return user;

    		 
    		 
        	
        	
        }
}


