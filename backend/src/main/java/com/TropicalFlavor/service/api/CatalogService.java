package com.TropicalFlavor.service.api;

import com.TropicalFlavor.dto.GoodsQuery;
import com.TropicalFlavor.response.PageResponse;
import com.TropicalFlavor.vo.CategoryVo;
import com.TropicalFlavor.vo.GoodsVo;

import java.util.List;

public interface CatalogService {
    PageResponse<GoodsVo> queryGoods(GoodsQuery query);

    GoodsVo getGoodsDetail(String gid);

    List<CategoryVo> listCategories();
}
