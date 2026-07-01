package com.TropicalFlavor.service.api;

import com.TropicalFlavor.dto.GoodsCreateRequest;
import com.TropicalFlavor.dto.GoodsUpdateRequest;
import com.TropicalFlavor.vo.GoodsVo;

import java.util.List;

public interface SellerGoodsService {
    List<GoodsVo> listMine(String uid);

    GoodsVo create(String uid, GoodsCreateRequest request);

    GoodsVo update(String uid, String gid, GoodsUpdateRequest request);

    void delete(String uid, String gid);
}
