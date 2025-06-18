package com.bank.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bank.models.Admin;

public class AdminMapper {
	public static Admin fromResultSet(List<Map<String,Object>> rows){
		 
		 if (rows == null || rows.isEmpty()) return null;
		
		    Map<String, Object> row = rows.get(0); 
	
			Admin admin = new Admin();
			
			admin.setAdminId((Long) row.get("adminId"));
			admin.setBranchId((Long)row.get("branchId"));
		
		return admin;
	}

    public static List<Map<String, Object>> mapToAdminMaps(List<Map<String, Object>> rows) {
        List<Map<String, Object>> adminList = new ArrayList<>();
        if (rows == null) return adminList;

        for (Map<String, Object> row : rows) {
            Map<String, Object> adminMap = new HashMap<>();

            adminMap.put("adminId", getLong(row.get("adminId")));
            adminMap.put("branchId", getLong(row.get("branchId")));
            adminMap.put("username", row.get("username"));
            adminMap.put("name", row.get("name"));
            adminMap.put("email", row.get("email"));
            adminMap.put("phone", row.get("phone"));
            adminMap.put("status", row.get("status"));
            adminMap.put("createdAt", getLong(row.get("createdAt")));
            adminMap.put("modifiedAt", getLong(row.get("modifiedAt")));
            adminMap.put("modifiedBy", getLong(row.get("modifiedBy")));

            adminList.add(adminMap);
        }

        return adminList;
    }

    private static Long getLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

}
