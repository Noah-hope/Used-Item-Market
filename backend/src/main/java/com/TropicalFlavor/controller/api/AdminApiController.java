package com.TropicalFlavor.controller.api;

import com.TropicalFlavor.dto.UserProfileUpdateRequest;
import com.TropicalFlavor.dto.AdminGoodsReviewRequest;
import com.TropicalFlavor.dto.CategorySaveRequest;
import com.TropicalFlavor.error.BaseException;
import com.TropicalFlavor.model.AuthContext;
import com.TropicalFlavor.response.ApiResponse;
import com.TropicalFlavor.service.api.AdminOpsService;
import com.TropicalFlavor.service.api.AdminUserService;
import com.TropicalFlavor.vo.CategoryVo;
import com.TropicalFlavor.vo.DashboardVo;
import com.TropicalFlavor.vo.GoodsVo;
import com.TropicalFlavor.vo.OrderVo;
import com.TropicalFlavor.vo.UserVo;
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
@RequestMapping("/api/admin")
public class AdminApiController {
    @Resource
    private AdminUserService adminUserService;
    @Resource
    private AdminOpsService adminOpsService;

    @GetMapping("/users")
    public ApiResponse<List<UserVo>> users() {
        requireAdmin();
        return ApiResponse.success(adminUserService.listUsers());
    }

    @PutMapping("/users/{uid}")
    public ApiResponse<UserVo> updateUser(@PathVariable("uid") String uid, @RequestBody UserProfileUpdateRequest request) {
        requireAdmin();
        return ApiResponse.success("更新成功", adminUserService.updateUser(uid, request));
    }

    @PostMapping("/users/{uid}/reset-password")
    public ApiResponse<UserVo> resetPassword(@PathVariable("uid") String uid) {
        requireAdmin();
        return ApiResponse.success("重置密码成功", adminUserService.resetPassword(uid));
    }

    @PostMapping("/users/{uid}/disable")
    public ApiResponse<UserVo> disable(@PathVariable("uid") String uid) {
        requireAdmin();
        return ApiResponse.success("停用成功", adminUserService.disableUser(uid));
    }

    @GetMapping("/orders")
    public ApiResponse<List<OrderVo>> orders() {
        requireAdmin();
        return ApiResponse.success(adminUserService.listOrders());
    }

    @GetMapping("/goods/pending")
    public ApiResponse<List<GoodsVo>> pendingGoods() {
        requireAdmin();
        return ApiResponse.success(adminOpsService.listPendingGoods());
    }

    @GetMapping("/goods")
    public ApiResponse<List<GoodsVo>> allGoods() {
        requireAdmin();
        return ApiResponse.success(adminOpsService.listAllGoods());
    }

    @PostMapping("/goods/{gid}/review")
    public ApiResponse<GoodsVo> reviewGoods(@PathVariable String gid, @RequestBody AdminGoodsReviewRequest request) {
        requireAdmin();
        return ApiResponse.success("审核已处理", adminOpsService.reviewGoods(gid, request));
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryVo>> categories() {
        requireAdmin();
        return ApiResponse.success(adminOpsService.listCategories());
    }

    @PostMapping("/categories")
    public ApiResponse<CategoryVo> saveCategory(@RequestBody CategorySaveRequest request) {
        requireAdmin();
        return ApiResponse.success("分类已保存", adminOpsService.saveCategory(request));
    }

    @GetMapping("/dashboard")
    public ApiResponse<DashboardVo> dashboard() {
        requireAdmin();
        return ApiResponse.success(adminOpsService.dashboard());
    }

    private void requireAdmin() {
        if (AuthContext.get() == null || !AuthContext.get().isAdmin()) {
            throw new BaseException(403, "无管理员权限");
        }
    }
}
