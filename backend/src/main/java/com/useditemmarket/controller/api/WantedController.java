package com.useditemmarket.controller.api;

import com.useditemmarket.dto.WantedCreateRequest;
import com.useditemmarket.model.AuthContext;
import com.useditemmarket.response.ApiResponse;
import com.useditemmarket.service.api.WantedService;
import com.useditemmarket.vo.WantedVo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/wanted")
public class WantedController {
    @Resource
    private WantedService wantedService;

    @GetMapping("/open")
    public ApiResponse<List<WantedVo>> listOpen() {
        return ApiResponse.success(wantedService.listOpen());
    }

    @GetMapping("/mine")
    public ApiResponse<List<WantedVo>> listMine() {
        return ApiResponse.success(wantedService.listMine(AuthContext.get().getUid()));
    }

    @PostMapping
    public ApiResponse<WantedVo> create(@RequestBody WantedCreateRequest request) {
        return ApiResponse.success("求购已发布", wantedService.create(AuthContext.get().getUid(), request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<WantedVo> delete(@PathVariable Long id) {
        return ApiResponse.success("求购已删除", wantedService.delete(AuthContext.get().getUid(), id));
    }
}
