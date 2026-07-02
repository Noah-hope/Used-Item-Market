package com.useditemmarket.service.api;

import com.useditemmarket.dto.AdminGoodsReviewRequest;
import com.useditemmarket.dto.CategorySaveRequest;
import com.useditemmarket.vo.CategoryVo;
import com.useditemmarket.vo.DashboardVo;
import com.useditemmarket.vo.GoodsVo;

import java.util.List;

public interface AdminOpsService {
    List<GoodsVo> listPendingGoods();

    List<GoodsVo> listAllGoods();

    GoodsVo reviewGoods(String gid, AdminGoodsReviewRequest request);

    List<CategoryVo> listCategories();

    CategoryVo saveCategory(CategorySaveRequest request);

    DashboardVo dashboard();
}
