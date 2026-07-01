package com.TropicalFlavor.vo;

import com.TropicalFlavor.model.UserStatus;
import com.TropicalFlavor.po.MarketUser;

public class UserVo {
    private String uid;
    private String username;
    private String email;
    private String phoneNumber;
    private String studentNo;
    private String realName;
    private boolean campusVerified;
    private String avatar;
    private String bio;
    private boolean admin;
    private UserStatus status;

    public static UserVo from(MarketUser user) {
        UserVo vo = new UserVo();
        vo.uid = user.getUID();
        vo.username = user.getUname();
        vo.email = user.getEmail();
        vo.phoneNumber = user.getPhoneNum();
        vo.studentNo = user.getStudentNo();
        vo.realName = user.getRealName();
        vo.campusVerified = user.getCampusVerified() == null || user.getCampusVerified() == 1;
        vo.avatar = user.getAvatar();
        vo.bio = user.getBio();
        vo.admin = user.getUID() != null && user.getUID().startsWith("ADMI");
        vo.status = UserStatus.fromCode(user.getStatus());
        return vo;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public String getRealName() {
        return realName;
    }

    public boolean isCampusVerified() {
        return campusVerified;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getBio() {
        return bio;
    }

    public boolean isAdmin() {
        return admin;
    }

    public UserStatus getStatus() {
        return status;
    }
}
