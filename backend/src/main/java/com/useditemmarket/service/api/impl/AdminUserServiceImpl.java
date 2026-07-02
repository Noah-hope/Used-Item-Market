package com.useditemmarket.service.api.impl;

import com.useditemmarket.dao.RecordDao;
import com.useditemmarket.dto.UserProfileUpdateRequest;
import com.useditemmarket.exception.BaseException;
import com.useditemmarket.model.UserStatus;
import com.useditemmarket.po.MarketGoods;
import com.useditemmarket.po.MarketUser;
import com.useditemmarket.service.api.AdminUserService;
import com.useditemmarket.util.Md5Util;
import com.useditemmarket.util.StringUtils;
import com.useditemmarket.vo.OrderVo;
import com.useditemmarket.vo.UserVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminUserServiceImpl extends AbstractApiSupport implements AdminUserService {
    @Resource
    private RecordDao recordDao;

    @Override
    public List<UserVo> listUsers() {
        return userDao.ShowUser().stream()
                .filter(user -> user.getUID() != null && !user.getUID().startsWith("ADMI"))
                .map(UserVo::from)
                .collect(Collectors.toList());
    }

    @Override
    public UserVo updateUser(String targetUid, UserProfileUpdateRequest request) {
        MarketUser user = requireUser(targetUid);
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
            throw new BaseException(500, "更新用户失败");
        }
        return UserVo.from(requireUser(targetUid));
    }

    @Override
    public UserVo resetPassword(String targetUid) {
        MarketUser user = requireUser(targetUid);
        String newPassword = StringUtils.getRandomChar();
        user.setPassword(Md5Util.getMD5(newPassword));
        user.setStatus(UserStatus.PASSWORD_RESET_REQUIRED.getCode());
        if (!userDao.ChangeInfo(user)) {
            throw new BaseException(500, "重置密码失败");
        }
        return UserVo.from(requireUser(targetUid));
    }

    @Override
    public UserVo disableUser(String targetUid) {
        MarketUser user = requireUser(targetUid);
        user.setStatus(UserStatus.DISABLED.getCode());
        if (!userDao.ChangeInfo(user)) {
            throw new BaseException(500, "停用用户失败");
        }
        for (MarketGoods goods : salesDao.ShowGoods(user)) {
            goods.setNumber(0.0);
            goods.setStatus("BANNED");
            goodsDao.ChangeInfo(goods);
        }
        return UserVo.from(requireUser(targetUid));
    }

    @Override
    public List<OrderVo> listOrders() {
        return recordDao.ShowAllRecord().stream()
                .map(OrderVo::fromTrade)
                .collect(Collectors.toList());
    }
}
