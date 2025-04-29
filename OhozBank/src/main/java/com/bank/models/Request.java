package com.bank.models;
import java.time.LocalDate;

public class Request {
    private int id;
    private String username;
    private String password;
    private String email;
    private String address;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String aadharNo;
    private String panNo;
    private String maritalStatus;
    private String occupation;
    private double annualIncome;
    private int branchId;
    private String branchName;
    private LocalDate requestDate;
    private String status; 
    private String rejectionReason;
    private Integer processedBy; 
    private LocalDate processedDate;

    public Request() {
        this.requestDate = LocalDate.now();
        this.status = "PENDING";
    }
    public User toUser() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setGender(this.gender);
        user.setDob(this.dob);
        user.setAddress(this.address);
        user.setMaritalStatus(this.maritalStatus);
        user.setAadharNo(this.aadharNo);
        user.setPanNo(this.panNo);
        user.setBranchId(this.branchId);
        user.setBranchName(this.branchName);
        user.setOccupation(this.occupation);
        user.setAnnualIncome(this.annualIncome);
        user.setRoleId(3); // Default role ID for customers
        user.setCreatedDate(LocalDate.now());
        user.setActive(true);
        return user;
    }


    

}
