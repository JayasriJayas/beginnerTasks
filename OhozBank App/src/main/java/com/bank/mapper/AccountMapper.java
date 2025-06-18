package com.bank.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.bank.enums.UserStatus;
import com.bank.models.Account;

public class AccountMapper {
	public static Account fromResultSet(List<Map<String,Object>> rows) {
		 if (rows == null || rows.isEmpty()) return null;
		 
		 Map<String, Object> row = rows.get(0); 

		 Account account = new Account();
		 account.setAccountId(((Number) row.get("accountId")).longValue());
		 account.setUserId(((Number) row.get("userId")).longValue());
		 account.setBranchId(((Number) row.get("branchId")).longValue());
		 Object balanceObj = row.get("balance");
		 account.setBalance(balanceObj != null ? new BigDecimal(balanceObj.toString()) : BigDecimal.ZERO);
		 account.setStatus(UserStatus.valueOf((String) row.get("status")));
		 account.setCreatedAt(((Number) row.get("createdAt")).longValue());
		 account.setModifiedAt(((Number) row.get("modifiedAt")).longValue());
		 account.setModifiedBy(((Number) row.get("modifiedBy")).longValue());
		 return account;
		 
	}
	public static List<Account> mapToAccounts(List<Map<String, Object>> rows) {
	    List<Account> accounts = new ArrayList<>();
	    if (rows == null) return accounts;

	    for (Map<String, Object> row : rows) {
	        Account account = new Account();
	        account.setAccountId(((Number) row.get("accountId")).longValue());
	        account.setUserId(((Number) row.get("userId")).longValue());
	        account.setBranchId(((Number) row.get("branchId")).longValue());

	        Object balanceObj = row.get("balance");
	        account.setBalance(balanceObj != null ? new BigDecimal(balanceObj.toString()) : BigDecimal.ZERO);
	        account.setStatus(UserStatus.valueOf((String) row.get("status")));
	        account.setCreatedAt(((Number) row.get("createdAt")).longValue());
	        account.setModifiedAt(((Number) row.get("modifiedAt")).longValue());
	        account.setModifiedBy(((Number) row.get("modifiedBy")).longValue());
	        accounts.add(account);
	    }

	    return accounts;
	}

	
}
