package com.useditemmarket.service.api.impl;

import com.useditemmarket.dao.GoodsDao;
import com.useditemmarket.dao.SalesDao;
import com.useditemmarket.dao.UserDao;
import com.useditemmarket.dao.UtilsDao;
import com.useditemmarket.exception.BaseException;
import com.useditemmarket.model.GoodsStatus;
import com.useditemmarket.po.MarketGoods;
import com.useditemmarket.po.MarketUser;
import com.useditemmarket.util.StringUtils;

import javax.annotation.Resource;

abstract class AbstractApiSupport {
    @Resource
    protected UserDao userDao;
    @Resource
    protected GoodsDao goodsDao;
    @Resource
    protected SalesDao salesDao;
    @Resource
    protected UtilsDao utilsDao;

    protected MarketUser requireUser(String uid) {
        MarketUser user = userDao.SelectUser(uid);
        if (user == null) {
            throw new BaseException(404, "用户不存在");
        }
        return user;
    }

    protected MarketUser requireNormalUser(String uid) {
        MarketUser user = requireUser(uid);
        if (isAdminUid(user.getUID())) {
            throw new BaseException(403, "管理员账号仅用于后台审核和管理");
        }
        return user;
    }

    protected MarketGoods requireGoods(String gid) {
        MarketGoods goods = goodsDao.SelectGoods(gid);
        if (goods == null) {
            throw new BaseException(404, "商品不存在");
        }
        return goods;
    }

    protected void requireOwner(String uid, String gid) {
        String ownerUid = salesDao.WhoseGoods(gid);
        if (ownerUid == null || !ownerUid.equals(uid)) {
            throw new BaseException(403, "无权操作该商品");
        }
        if (isAdminUid(ownerUid)) {
            throw new BaseException(403, "管理员账号不能作为卖家操作商品");
        }
    }

    protected String nextGid() {
        try {
            return String.valueOf(utilsDao.selectMaxGID() + 1);
        } catch (Exception ex) {
            return "1000000001";
        }
    }

    protected String nextPid() {
        try {
            return String.valueOf(utilsDao.selectMaxPID() + 1);
        } catch (Exception ex) {
            return "1000000001";
        }
    }

    protected void requireNotBlank(String value, String message) {
        if (StringUtils.isNullOrEmpty(value)) {
            throw new BaseException(400, message);
        }
    }

    protected void requireCampusVerified(MarketUser user) {
        if (user.getCampusVerified() != null && user.getCampusVerified() == 0) {
            throw new BaseException(403, "请先完成校园认证");
        }
    }

    protected void requireGoodsActive(MarketGoods goods, String message) {
        if (goods == null || GoodsStatus.fromValue(goods.getStatus()) != GoodsStatus.ACTIVE) {
            throw new BaseException(400, message);
        }
        if (goods.getNumber() == null || goods.getNumber() <= 0) {
            throw new BaseException(400, message);
        }
    }

    protected boolean isAdminUid(String uid) {
        return uid != null && uid.startsWith("ADMI");
    }
}
