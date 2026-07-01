package com.TropicalFlavor.controller.api;

import com.TropicalFlavor.dto.LoginRequest;
import com.TropicalFlavor.dto.RegisterRequest;
import com.TropicalFlavor.model.AuthContext;
import com.TropicalFlavor.model.AuthUser;
import com.TropicalFlavor.response.ApiResponse;
import com.TropicalFlavor.service.api.AuthService;
import com.TropicalFlavor.vo.LoginResponse;
import com.TropicalFlavor.vo.UserVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<UserVo> register(@RequestBody RegisterRequest request) {
        return ApiResponse.success("注册成功", authService.register(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserVo> me() {
        AuthUser authUser = AuthContext.get();
        return ApiResponse.success(authService.currentUser(authUser.getUid()));
    }
}
