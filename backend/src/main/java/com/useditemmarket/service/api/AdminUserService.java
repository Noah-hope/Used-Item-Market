package com.useditemmarket.service.api;

import com.useditemmarket.dto.UserProfileUpdateRequest;
import com.useditemmarket.vo.OrderVo;
import com.useditemmarket.vo.UserVo;

import java.util.List;

public interface AdminUserService {
    List<UserVo> listUsers();

    UserVo updateUser(String targetUid, UserProfileUpdateRequest request);

    UserVo resetPassword(String targetUid);

    UserVo disableUser(String targetUid);

    List<OrderVo> listOrders();
}
