package com.bank.test;

public class PasswordHashTest {
    public static void main(String[] args) {
        String password = "superadmin#123";  // Change this to any password you want
        String hashed = com.bank.util.PasswordUtil.hashPassword(password);
        System.out.println("Original password: " + password);
        System.out.println("Hashed password  : " + hashed);
    }
}

