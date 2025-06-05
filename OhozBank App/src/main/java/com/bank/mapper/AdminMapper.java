package com.bank.mapper;

import java.util.ArrayList;
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

}
