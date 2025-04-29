package com.bank.test;

import com.bank.dao.UserDAO;
import com.bank.dao.impl.UserDAOImpl;
import com.bank.models.User;

import java.time.LocalDate;

public class UserInsertTest {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAOImpl();
        User user = new User();

        // Fill in realistic dummy values
        user.setUsername("testuser23");
        user.setPassword("test@123"); // PasswordUtil will hash this
        user.setEmail("testuser123@example.com");
        user.setPhone("9876543210");
        user.setGender("Female");
        user.setDob(LocalDate.of(1998, 5, 15));
        user.setAddress("123 Test Street");
        user.setMaritalStatus("Single");
        user.setAadharNo("123412341234");
        user.setPanNo("ABCDE1234F");
        user.setBranchId(1);
        user.setOccupation("Developer");
        user.setAnnualIncome(500000);
        user.setRoleId(2); // e.g., 1 for admin, 2 for customer
        user.setCreatedDate(LocalDate.now());
        user.setActive(true);

        int userId = userDAO.addUser(user);

        if (userId != -1) {
            System.out.println("User inserted successfully with ID: " + userId);
        } else {
            System.out.println("Failed to insert user.");
        }
    }
    
}
