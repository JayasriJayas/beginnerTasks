package com.bank.enums;

public enum UserRole {
    SUPER_ADMIN(1),
    ADMIN(2),
    USER(3),
    PUBLIC(4);

    
    private final int id;

    UserRole(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static UserRole fromId(int id) {
        for (UserRole role : values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role id: " + id);
    }
}
