package com.bank.mapper;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bank.enums.Gender;
import com.bank.enums.RequestStatus;
import com.bank.models.Request;

public class RequestMapper {
	public static Request fromResultSet(List<Map<String, Object>> rows) {
	    if (rows == null || rows.isEmpty()) return null;

	    Map<String, Object> row = rows.get(0); 

	    Request request = new Request();
	    request.setId(((Number) row.get("id")).longValue());
	    request.setUsername((String) row.get("username"));
	    request.setPassword((String) row.get("password"));
	    request.setName((String) row.get("name"));
	    request.setEmail((String) row.get("email"));
	    request.setPhone(((Number) row.get("phone")).longValue());
	    request.setGender(Gender.valueOf((String) row.get("gender")));
	    request.setDob((Date) row.get("dob"));
	    request.setAddress((String) row.get("address"));
	    request.setMaritalStatus((String) row.get("maritalStatus"));
	    request.setAadharNo(((Number) row.get("aadharNo")).longValue());
	    request.setPanNo((String) row.get("panNo"));
	    request.setOccupation((String) row.get("occupation"));
	    request.setAnnualIncome(((Number) row.get("annualIncome")).doubleValue());
	    request.setBranchId(((Number) row.get("branchId")).longValue());
	    request.setStatus(RequestStatus.valueOf((String) row.get("status")));
	    request.setRequestTimestamp(((Number) row.get("requestDate")).longValue());
	    return request;
	}
	public static List<Request> toMapResult(List<Map<String,Object>> rows) {
        List<Request> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Request req = new Request();
            req.setId(((Number) row.get("id")).longValue());
    	    req.setUsername((String) row.get("username"));
    	    req.setPassword((String) row.get("password"));
    	    req.setName((String) row.get("name"));
    	    req.setEmail((String) row.get("email"));
    	    req.setPhone(((Number) row.get("phone")).longValue());
    	    req.setGender(Gender.valueOf((String) row.get("gender")));
    	    req.setDob((Date) row.get("dob"));
    	    req.setAddress((String) row.get("address"));
    	    req.setMaritalStatus((String) row.get("maritalStatus"));
    	    req.setAadharNo(((Number) row.get("aadharNo")).longValue());
    	    req.setPanNo((String) row.get("panNo"));
    	    req.setOccupation((String) row.get("occupation"));
    	    req.setAnnualIncome(((Number) row.get("annualIncome")).doubleValue());
    	    req.setBranchId(((Number) row.get("branchId")).longValue());
    	    req.setStatus(RequestStatus.valueOf((String) row.get("status")));
    	    req.setRequestTimestamp(((Number) row.get("requestDate")).longValue());
            list.add(req);
        }
        return list;
    }



}
