package com.TropicalFlavor.service.api.impl;

import com.TropicalFlavor.dao.GoodsDao;
import com.TropicalFlavor.dao.SalesDao;
import com.TropicalFlavor.dao.UserDao;
import com.TropicalFlavor.dao.UtilsDao;
import com.TropicalFlavor.error.BaseException;
import com.TropicalFlavor.po.MarketGoods;
import com.TropicalFlavor.po.MarketUser;
import com.TropicalFlavor.repository.PlatformRepository;
import com.TropicalFlavor.tool.StringUtils;

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
    @Resource
    protected PlatformRepository platformRepository;

    protected MarketUser requireUser(String uid) {
        MarketUser user = userDao.SelectUser(uid);
        if (user == null) {
            throw new BaseException(404, "用户不存在");
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
        if (StringUtils.getInstance().isNullOrEmpty(value)) {
            throw new BaseException(400, message);
        }
    }

    protected void requireCampusVerified(MarketUser user) {
        if (user.getCampusVerified() != null && user.getCampusVerified() == 0) {
            throw new BaseException(403, "请先完成校园认证");
        }
    }
}
