package com.useditemmarket.model;

public class AuthUser {
    private final String uid;
    private final boolean admin;

    public AuthUser(String uid, boolean admin) {
        this.uid = uid;
        this.admin = admin;
    }

    public String getUid() {
        return uid;
    }

    public boolean isAdmin() {
        return admin;
    }
}
