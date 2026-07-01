package com.TropicalFlavor.service.api.impl;

import com.TropicalFlavor.dto.PasswordUpdateRequest;
import com.TropicalFlavor.dto.UserProfileUpdateRequest;
import com.TropicalFlavor.error.BaseException;
import com.TropicalFlavor.po.MarketUser;
import com.TropicalFlavor.service.api.ProfileService;
import com.TropicalFlavor.util.Md5Util;
import com.TropicalFlavor.vo.UserVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileServiceImpl extends AbstractApiSupport implements ProfileService {
    @Override
    public UserVo getProfile(String uid) {
        return UserVo.from(requireUser(uid));
    }

    @Override
    public UserVo updateProfile(String uid, UserProfileUpdateRequest request) {
        MarketUser user = requireUser(uid);
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            user.setUname(request.getUsername().trim());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            user.setEmail(request.getEmail().trim());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            user.setPhoneNum(request.getPhoneNumber().trim());
        }
        if (request.getRealName() != null && !request.getRealName().trim().isEmpty()) {
            user.setRealName(request.getRealName().trim());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar().trim().isEmpty() ? null : request.getAvatar().trim());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio().trim().isEmpty() ? null : request.getBio().trim());
        }
        if (!userDao.ChangeInfo(user)) {
            throw new BaseException(500, "更新个人信息失败");
        }
        return UserVo.from(requireUser(uid));
    }

    @Override
    public void updatePassword(String uid, PasswordUpdateRequest request) {
        requireNotBlank(request.getPassword(), "密码不能为空");
        requireNotBlank(request.getConfirmPassword(), "确认密码不能为空");
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BaseException(400, "两次输入的密码不一致");
        }
        MarketUser user = requireUser(uid);
        user.setPassword(Md5Util.getMD5(request.getPassword()));
        user.setStatus(0);
        if (!userDao.ChangeInfo(user)) {
            throw new BaseException(500, "修改密码失败");
        }
    }
}
