package com.useditemmarket.controller.api;

import com.useditemmarket.model.AuthContext;
import com.useditemmarket.response.ApiResponse;
import com.useditemmarket.service.api.FavoriteService;
import com.useditemmarket.vo.GoodsVo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    @Resource
    private FavoriteService favoriteService;

    @GetMapping
    public ApiResponse<List<GoodsVo>> list() {
        return ApiResponse.success(favoriteService.list(AuthContext.get().getUid()));
    }

    @PostMapping("/{gid}")
    public ApiResponse<Void> add(@PathVariable String gid) {
        favoriteService.add(AuthContext.get().getUid(), gid);
        return ApiResponse.success("已收藏", null);
    }

    @DeleteMapping("/{gid}")
    public ApiResponse<Void> remove(@PathVariable String gid) {
        favoriteService.remove(AuthContext.get().getUid(), gid);
        return ApiResponse.success("已取消收藏", null);
    }
}
