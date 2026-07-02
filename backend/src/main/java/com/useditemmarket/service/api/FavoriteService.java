package com.useditemmarket.service.api;

import com.useditemmarket.vo.GoodsVo;

import java.util.List;

public interface FavoriteService {
    List<GoodsVo> list(String uid);

    void add(String uid, String gid);

    void remove(String uid, String gid);
}
