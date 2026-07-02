package com.useditemmarket.vo;

public class LoginResponse {
    private String accessToken;
    private UserVo user;

    public LoginResponse(String accessToken, UserVo user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserVo getUser() {
        return user;
    }

    public void setUser(UserVo user) {
        this.user = user;
    }
}
