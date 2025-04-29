package com.bank.models;

public class Branch {
    private long branchId;
    private String branchName;
    private String branchCode;
    private String location;
    private String phone;
    private int adminId;
    private boolean active;
    
    public Branch() {
        this.active = true;
    }




    public long getId() {
        return branchId;
    }

    public void setId(int id) {
        this.branchId = id;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getLocation() {
        return location;
    }

    public void setAddress(String location) {
        this.location = location;
    }
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int managerId) {
        this.adminId = adminId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

//    @Override
//    public String toString() {
//        return "Branch{" +
//                "id=" + id +
//                ", branchName='" + branchName + '\'' +
//                ", branchCode='" + branchCode + '\'' +
//                ", city='" + city + '\'' +
//                '}';
//    }
}

