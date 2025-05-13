package com.bank.mapper;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.sql.Date;

import com.bank.enums.RequestStatus;
import com.bank.models.Request;

public class RequestMapper {
	public static Request fromResultSet(List<Map<String, Object>> rows) {
	    if (rows == null || rows.isEmpty()) return null;

	    Map<String, Object> row = rows.get(0); 

	    Request request = new Request();
	    request.setId((Long) row.get("id"));
	    request.setUsername((String) row.get("username"));
	    request.setPassword((String) row.get("password"));
	    request.setEmail((String) row.get("email"));
	    request.setPhone((Integer) row.get("phone"));
	    request.setGender((String) row.get("gender"));
	    request.setDob((Date) row.get("dob"));
	    request.setAddress((String) row.get("address"));
	    request.setMaritalStatus((String) row.get("maritalStatus"));
	    request.setAadharNo((Long) row.get("aadharNo"));
	    request.setPanNo((String) row.get("panNo"));
	    request.setOccupation((String) row.get("occupation"));
	    request.setAnnualIncome((Double) row.get("annualIncome"));
	    request.setBranchId((Long) row.get("branchId"));
	    request.setBranchName((String) row.get("branchName"));
	    request.setStatus(RequestStatus.valueOf((String) row.get("status")));

	    return request;
	}



}
