package com.customer.servlet;

public class User {
    private int id;
    private String firstname;
    private String surname;
    private String username;
    private String password;
    private String dob;
    private String gender;
    private String address;
    private String email;
    private String phone;

    public User(int id, String firstname, String surname, String username, String password, String dob, String gender, String address, String email, String phone) {
        this.id = id;
        this.firstname = firstname;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }

    public int getId() { return id; }
    public String getFirstname() { return firstname; }
    public String getSurname() { return surname; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDob() { return dob; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    public void setId(int id) { this.id = id; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setDob(String dob) { this.dob = dob; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAddress(String address) { this.address = address; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
//    @Override
//    public String toString() {
//        return "User{id=" + id + ", name='" + firstname + "', email='" + email + "'}";
//    }
}
