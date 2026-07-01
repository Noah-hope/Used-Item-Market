package com.TropicalFlavor.controller.api;

import com.TropicalFlavor.dto.PasswordUpdateRequest;
import com.TropicalFlavor.dto.UserProfileUpdateRequest;
import com.TropicalFlavor.model.AuthContext;
import com.TropicalFlavor.response.ApiResponse;
import com.TropicalFlavor.service.api.ProfileService;
import com.TropicalFlavor.vo.UserVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Resource
    private ProfileService profileService;

    @GetMapping
    public ApiResponse<UserVo> getProfile() {
        return ApiResponse.success(profileService.getProfile(AuthContext.get().getUid()));
    }

    @PutMapping
    public ApiResponse<UserVo> updateProfile(@RequestBody UserProfileUpdateRequest request) {
        return ApiResponse.success("更新成功", profileService.updateProfile(AuthContext.get().getUid(), request));
    }

    @PutMapping("/password")
    public ApiResponse<Void> updatePassword(@RequestBody PasswordUpdateRequest request) {
        profileService.updatePassword(AuthContext.get().getUid(), request);
        return ApiResponse.success("修改密码成功", null);
    }
}
