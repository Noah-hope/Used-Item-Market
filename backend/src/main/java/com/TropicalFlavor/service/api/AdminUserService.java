package com.TropicalFlavor.service.api;

import com.TropicalFlavor.dto.UserProfileUpdateRequest;
import com.TropicalFlavor.vo.OrderVo;
import com.TropicalFlavor.vo.UserVo;

import java.util.List;

public interface AdminUserService {
    List<UserVo> listUsers();

    UserVo updateUser(String targetUid, UserProfileUpdateRequest request);

    UserVo resetPassword(String targetUid);

    UserVo disableUser(String targetUid);

    List<OrderVo> listOrders();
}
