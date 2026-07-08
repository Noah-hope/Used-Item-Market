package com.useditemmarket.service.api.impl;

import com.useditemmarket.exception.BaseException;
import com.useditemmarket.po.MarketGoods;
import com.useditemmarket.repository.FavoriteRepository;
import com.useditemmarket.service.api.FavoriteService;
import com.useditemmarket.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class FavoriteServiceImpl extends AbstractApiSupport implements FavoriteService {
    @Resource
    private FavoriteRepository favoriteRepository;

    @Override
    public List<GoodsVo> list(String uid) {
        requireNormalUser(uid);
        return favoriteRepository.listFavorites(uid);
    }

    @Override
    public void add(String uid, String gid) {
        requireNormalUser(uid);
        MarketGoods goods = requireGoods(gid);
        String sellerUid = salesDao.WhoseGoods(gid);
        if (isAdminUid(sellerUid)) {
            throw new BaseException(400, "该商品不可收藏");
        }
        requireGoodsActive(goods, "该商品当前不可收藏");
        favoriteRepository.addFavorite(uid, gid);
    }

    @Override
    public void remove(String uid, String gid) {
        requireNormalUser(uid);
        favoriteRepository.removeFavorite(uid, gid);
    }
}
