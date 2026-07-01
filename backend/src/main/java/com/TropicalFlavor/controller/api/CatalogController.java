package com.TropicalFlavor.controller.api;

import com.TropicalFlavor.dto.GoodsQuery;
import com.TropicalFlavor.response.ApiResponse;
import com.TropicalFlavor.response.PageResponse;
import com.TropicalFlavor.service.api.CatalogService;
import com.TropicalFlavor.vo.CategoryVo;
import com.TropicalFlavor.vo.GoodsVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {
    @Resource
    private CatalogService catalogService;

    @GetMapping("/goods")
    public ApiResponse<PageResponse<GoodsVo>> list(GoodsQuery query) {
        return ApiResponse.success(catalogService.queryGoods(query));
    }

    @GetMapping("/goods/{gid}")
    public ApiResponse<GoodsVo> detail(@PathVariable String gid) {
        return ApiResponse.success(catalogService.getGoodsDetail(gid));
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryVo>> categories() {
        return ApiResponse.success(catalogService.listCategories());
    }
}
