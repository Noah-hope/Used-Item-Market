package com.useditemmarket.service.api.impl;

import com.useditemmarket.dto.LoginRequest;
import com.useditemmarket.dto.RegisterRequest;
import com.useditemmarket.exception.BaseException;
import com.useditemmarket.model.UserStatus;
import com.useditemmarket.po.MarketUser;
import com.useditemmarket.security.JwtTokenService;
import com.useditemmarket.service.api.AuthService;
import com.useditemmarket.util.Md5Util;
import com.useditemmarket.util.StringUtils;
import com.useditemmarket.vo.LoginResponse;
import com.useditemmarket.vo.UserVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class AuthServiceImpl extends AbstractApiSupport implements AuthService {
    @Resource
    private JwtTokenService jwtTokenService;

    @Override
    public LoginResponse login(LoginRequest request) {
        requireNotBlank(request.getStudentNo(), "学号不能为空");
        requireNotBlank(request.getPassword(), "密码不能为空");
        boolean admin = Boolean.TRUE.equals(request.getAdmin());
        String uid = userDao.IsTrue(request.getStudentNo(), Md5Util.getMD5(request.getPassword()));
        if (uid == null) {
            throw new BaseException(400, "用户名或密码错误");
        }
        if (admin != uid.startsWith("ADMI")) {
            throw new BaseException(400, "用户角色不匹配");
        }
        MarketUser user = requireUser(uid);
        UserStatus status = UserStatus.fromCode(user.getStatus());
        if (status == UserStatus.DISABLED) {
            throw new BaseException(403, "用户已被停用");
        }
        String token = jwtTokenService.generateToken(uid, admin);
        return new LoginResponse(token, UserVo.from(user));
    }

    @Override
    public UserVo register(RegisterRequest request) {
        requireNotBlank(request.getStudentNo(), "学号不能为空");
        requireNotBlank(request.getUsername(), "用户名不能为空");
        requireNotBlank(request.getPassword(), "密码不能为空");
        requireNotBlank(request.getConfirmPassword(), "确认密码不能为空");
        requireNotBlank(request.getEmail(), "邮箱不能为空");
        requireNotBlank(request.getPhoneNumber(), "手机号不能为空");
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BaseException(400, "两次输入的密码不一致");
        }
        if (!request.getEmail().contains("@")) {
            throw new BaseException(400, "邮箱格式不正确");
        }
        if (!StringUtils.isPhone(request.getPhoneNumber())) {
            throw new BaseException(400, "手机号格式不正确");
        }
        String uid = "NORM" + request.getStudentNo();
        if (userDao.SelectUser(uid) != null) {
            throw new BaseException(400, "该学号已注册");
        }
        MarketUser user = new MarketUser(
                request.getUsername(),
                request.getEmail(),
                request.getPhoneNumber(),
                Md5Util.getMD5(request.getPassword()),
                request.getStudentNo()
        );
        user.setStatus(UserStatus.ACTIVE.getCode());
        user.setStudentNo(request.getStudentNo().trim());
        user.setRealName((request.getRealName() == null || request.getRealName().trim().isEmpty()) ? request.getUsername().trim() : request.getRealName().trim());
        user.setCampusVerified(1);
        if (!userDao.InsertUser(user)) {
            throw new BaseException(500, "注册失败");
        }
        return UserVo.from(requireUser(uid));
    }

    @Override
    public UserVo currentUser(String uid) {
        return UserVo.from(requireUser(uid));
    }
}
