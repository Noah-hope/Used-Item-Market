package com.TropicalFlavor.service.api;

import com.TropicalFlavor.dto.LoginRequest;
import com.TropicalFlavor.dto.RegisterRequest;
import com.TropicalFlavor.vo.LoginResponse;
import com.TropicalFlavor.vo.UserVo;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    UserVo register(RegisterRequest request);

    UserVo currentUser(String uid);
}
