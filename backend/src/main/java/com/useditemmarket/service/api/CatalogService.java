package com.useditemmarket.service.api;

import com.useditemmarket.dto.GoodsQuery;
import com.useditemmarket.response.PageResponse;
import com.useditemmarket.vo.CategoryVo;
import com.useditemmarket.vo.GoodsVo;

import java.util.List;

public interface CatalogService {
    PageResponse<GoodsVo> queryGoods(GoodsQuery query);

    GoodsVo getGoodsDetail(String gid);

    List<CategoryVo> listCategories();
}
