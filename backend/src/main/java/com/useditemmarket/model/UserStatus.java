package com.useditemmarket.model;

public enum UserStatus {
    ACTIVE(0),
    PASSWORD_RESET_REQUIRED(1),
    DISABLED(2);

    private final int code;

    UserStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static UserStatus fromCode(Integer code) {
        if (code == null) {
            return ACTIVE;
        }
        for (UserStatus value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return ACTIVE;
    }
}
