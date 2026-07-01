package com.TropicalFlavor.controller.api;

import com.TropicalFlavor.dto.WantedCreateRequest;
import com.TropicalFlavor.model.AuthContext;
import com.TropicalFlavor.response.ApiResponse;
import com.TropicalFlavor.service.api.WantedService;
import com.TropicalFlavor.vo.WantedVo;
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

    @PostMapping("/{id}/close")
    public ApiResponse<WantedVo> close(@PathVariable Long id) {
        return ApiResponse.success("求购已关闭", wantedService.close(AuthContext.get().getUid(), id));
    }
}
