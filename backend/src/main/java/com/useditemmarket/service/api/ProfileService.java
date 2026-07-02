package com.useditemmarket.service.api;

import com.useditemmarket.dto.PasswordUpdateRequest;
import com.useditemmarket.dto.UserProfileUpdateRequest;
import com.useditemmarket.vo.UserVo;

public interface ProfileService {
    UserVo getProfile(String uid);

    UserVo updateProfile(String uid, UserProfileUpdateRequest request);

    void updatePassword(String uid, PasswordUpdateRequest request);
}
