package com.TropicalFlavor.service.api;

import com.TropicalFlavor.dto.PasswordUpdateRequest;
import com.TropicalFlavor.dto.UserProfileUpdateRequest;
import com.TropicalFlavor.vo.UserVo;

public interface ProfileService {
    UserVo getProfile(String uid);

    UserVo updateProfile(String uid, UserProfileUpdateRequest request);

    void updatePassword(String uid, PasswordUpdateRequest request);
}
