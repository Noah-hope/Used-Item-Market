package com.TropicalFlavor.controller.api;

import com.TropicalFlavor.dto.GoodsCreateRequest;
import com.TropicalFlavor.dto.GoodsUpdateRequest;
import com.TropicalFlavor.model.AuthContext;
import com.TropicalFlavor.response.ApiResponse;
import com.TropicalFlavor.service.api.SellerGoodsService;
import com.TropicalFlavor.vo.GoodsVo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/seller/goods")
public class SellerGoodsController {
    @Resource
    private SellerGoodsService sellerGoodsService;

    @GetMapping
    public ApiResponse<List<GoodsVo>> listMine() {
        return ApiResponse.success(sellerGoodsService.listMine(AuthContext.get().getUid()));
    }

    @PostMapping
    public ApiResponse<GoodsVo> create(@RequestBody GoodsCreateRequest request) {
        return ApiResponse.success("发布成功", sellerGoodsService.create(AuthContext.get().getUid(), request));
    }

    @PutMapping("/{gid}")
    public ApiResponse<GoodsVo> update(@PathVariable String gid, @RequestBody GoodsUpdateRequest request) {
        return ApiResponse.success("更新成功", sellerGoodsService.update(AuthContext.get().getUid(), gid, request));
    }

    @DeleteMapping("/{gid}")
    public ApiResponse<Void> delete(@PathVariable String gid) {
        sellerGoodsService.delete(AuthContext.get().getUid(), gid);
        return ApiResponse.success("下架成功", null);
    }
}
