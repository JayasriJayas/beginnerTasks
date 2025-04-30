package com.bank.dao;


import com.bank.models.User;
import java.util.List;

public interface UserDAO {
     
    int addUser(User user);
    User authenticateUser(String username, String password);
    
//    boolean updateUser(User user);
//   
//    User getUserById(int userId);
//    
//    User getUserByUsername(String username);
//    
//    User getUserByEmail(String email);
//    
   
//    
//    boolean changePassword(int userId, String newPassword);
//
//    List<User> getUsersByBranch(int branchId);
//    
//    List<User> getUsersByRole(int roleId);
//    
//    boolean updateLastLogin(int userId);
//  
//    boolean deactivateUser(int userId);
    
}
