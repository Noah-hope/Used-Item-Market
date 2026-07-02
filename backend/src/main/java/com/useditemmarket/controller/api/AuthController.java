package com.useditemmarket.controller.api;

import com.useditemmarket.dto.LoginRequest;
import com.useditemmarket.dto.RegisterRequest;
import com.useditemmarket.model.AuthContext;
import com.useditemmarket.model.AuthUser;
import com.useditemmarket.response.ApiResponse;
import com.useditemmarket.service.api.AuthService;
import com.useditemmarket.vo.LoginResponse;
import com.useditemmarket.vo.UserVo;
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
