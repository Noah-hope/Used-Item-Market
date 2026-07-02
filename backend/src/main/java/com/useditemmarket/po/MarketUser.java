package com.useditemmarket.po;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class MarketUser {
    //用户ID（前5位若是管理员为Admin，若是普通用户为NUser）
    private String UID;
    //用户名
    private String Uname;
    //邮箱
    private String Email;
    //电话
    private String PhoneNum;
    //密码
    private String Password;

    private Integer Status;

    private String StudentNo;

    private String RealName;

    private Integer CampusVerified;

    private String Avatar;

    private String Bio;

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getStudentNo() {
        return StudentNo;
    }

    public void setStudentNo(String studentNo) {
        StudentNo = studentNo;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public Integer getCampusVerified() {
        return CampusVerified;
    }

    public void setCampusVerified(Integer campusVerified) {
        CampusVerified = campusVerified;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUname() {
        return Uname;
    }



    public void setUname(String uname) {
        Uname = uname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        PhoneNum = phoneNum;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public MarketUser(){};

    public MarketUser(String uname, String email, String phoneNum, String password, String SID)
    {
        this.UID = "NORM"+SID;
        Uname = uname;
        Email = email;
        PhoneNum = phoneNum;
        Password = password;
    }

    public MarketUser(String UID, String uname, String email, String phoneNum, String password, Integer status) {
        this.UID = UID;
        Uname = uname;
        Email = email;
        PhoneNum = phoneNum;
        Password = password;
        Status = status;
    }

    @Override
    public String toString()
    {
        return "MarketUser{" +
                "UID='" + UID + '\'' +
                ", Uname='" + Uname + '\'' +
                ", Email='" + Email + '\'' +
                ", PhoneNum='" + PhoneNum + '\'' +
                ", Password='" + Password + '\'' +
                '}';
    }
}
