package com.TropicalFlavor.service.api;

import com.TropicalFlavor.vo.GoodsVo;

import java.util.List;

public interface FavoriteService {
    List<GoodsVo> list(String uid);

    void add(String uid, String gid);

    void remove(String uid, String gid);
}
