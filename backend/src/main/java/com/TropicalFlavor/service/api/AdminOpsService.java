package com.TropicalFlavor.service.api;

import com.TropicalFlavor.dto.AdminGoodsReviewRequest;
import com.TropicalFlavor.dto.CategorySaveRequest;
import com.TropicalFlavor.vo.CategoryVo;
import com.TropicalFlavor.vo.DashboardVo;
import com.TropicalFlavor.vo.GoodsVo;

import java.util.List;

public interface AdminOpsService {
    List<GoodsVo> listPendingGoods();

    List<GoodsVo> listAllGoods();

    GoodsVo reviewGoods(String gid, AdminGoodsReviewRequest request);

    List<CategoryVo> listCategories();

    CategoryVo saveCategory(CategorySaveRequest request);

    DashboardVo dashboard();
}
