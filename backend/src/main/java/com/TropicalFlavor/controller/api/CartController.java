package com.TropicalFlavor.controller.api;

import com.TropicalFlavor.dto.CartAddRequest;
import com.TropicalFlavor.model.AuthContext;
import com.TropicalFlavor.response.ApiResponse;
import com.TropicalFlavor.service.api.CartService;
import com.TropicalFlavor.vo.CartItemVo;
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
@RequestMapping("/api/cart")
public class CartController {
    @Resource
    private CartService cartService;

    @GetMapping
    public ApiResponse<List<CartItemVo>> list() {
        return ApiResponse.success(cartService.listCart(AuthContext.get().getUid()));
    }

    @PostMapping("/items")
    public ApiResponse<List<CartItemVo>> add(@RequestBody CartAddRequest request) {
        return ApiResponse.success("加入购物车成功",
                cartService.addItem(AuthContext.get().getUid(), request.getGid(), request.getQuantity()));
    }

    @PutMapping("/items/{gid}")
    public ApiResponse<List<CartItemVo>> update(@PathVariable String gid, @RequestBody CartAddRequest request) {
        return ApiResponse.success("更新购物车成功",
                cartService.updateItem(AuthContext.get().getUid(), gid, request.getQuantity()));
    }

    @DeleteMapping("/items/{gid}")
    public ApiResponse<List<CartItemVo>> remove(@PathVariable String gid) {
        return ApiResponse.success("移除成功", cartService.removeItem(AuthContext.get().getUid(), gid));
    }
}
