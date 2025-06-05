package com.bank.dao;

import java.sql.SQLException;
import exception.QueryException;
import com.bank.models.Customer;

public interface CustomerDAO {
	Customer getCustomerByUserId(long userId)throws SQLException,QueryException;
	boolean updateEditableFields(Customer customer) throws SQLException;

}
