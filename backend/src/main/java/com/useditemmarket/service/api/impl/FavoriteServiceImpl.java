package com.useditemmarket.service.api.impl;

import com.useditemmarket.exception.BaseException;
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
        requireGoods(gid);
        String sellerUid = salesDao.WhoseGoods(gid);
        if (isAdminUid(sellerUid)) {
            throw new BaseException(400, "该商品不可收藏");
        }
        favoriteRepository.addFavorite(uid, gid);
    }

    @Override
    public void remove(String uid, String gid) {
        requireNormalUser(uid);
        favoriteRepository.removeFavorite(uid, gid);
    }
}
