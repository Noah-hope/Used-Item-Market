package com.TropicalFlavor.service.api.impl;

import com.TropicalFlavor.repository.PlatformRepository;
import com.TropicalFlavor.service.api.FavoriteService;
import com.TropicalFlavor.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class FavoriteServiceImpl extends AbstractApiSupport implements FavoriteService {
    @Resource
    private PlatformRepository platformRepository;

    @Override
    public List<GoodsVo> list(String uid) {
        requireUser(uid);
        return platformRepository.listFavorites(uid);
    }

    @Override
    public void add(String uid, String gid) {
        requireUser(uid);
        requireGoods(gid);
        platformRepository.addFavorite(uid, gid);
    }

    @Override
    public void remove(String uid, String gid) {
        requireUser(uid);
        platformRepository.removeFavorite(uid, gid);
    }
}
