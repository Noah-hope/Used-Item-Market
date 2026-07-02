package com.useditemmarket.service.api;

import com.useditemmarket.dto.LoginRequest;
import com.useditemmarket.dto.RegisterRequest;
import com.useditemmarket.vo.LoginResponse;
import com.useditemmarket.vo.UserVo;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    UserVo register(RegisterRequest request);

    UserVo currentUser(String uid);
}
