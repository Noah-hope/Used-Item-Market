package com.useditemmarket.controller.api;

import com.useditemmarket.dto.OrderCreateRequest;
import com.useditemmarket.model.AuthContext;
import com.useditemmarket.model.OrderStatus;
import com.useditemmarket.response.ApiResponse;
import com.useditemmarket.service.api.OrderService;
import com.useditemmarket.vo.OrderVo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Resource
    private OrderService orderService;

    @PostMapping
    public ApiResponse<OrderVo> create(@RequestBody OrderCreateRequest request) {
        return ApiResponse.success("下单成功", orderService.createOrder(AuthContext.get().getUid(), request));
    }

    @GetMapping("/purchases")
    public ApiResponse<List<OrderVo>> purchases(@RequestParam(defaultValue = "PENDING_PICKUP") String status) {
        return ApiResponse.success(orderService.listPurchases(AuthContext.get().getUid(), OrderStatus.valueOf(status)));
    }

    @GetMapping("/sales")
    public ApiResponse<List<OrderVo>> sales(@RequestParam(defaultValue = "PENDING_SHIPMENT") String status) {
        return ApiResponse.success(orderService.listSales(AuthContext.get().getUid(), OrderStatus.valueOf(status)));
    }

    @PostMapping("/{pid}/ship")
    public ApiResponse<OrderVo> ship(@PathVariable String pid) {
        return ApiResponse.success("发货成功", orderService.ship(AuthContext.get().getUid(), pid));
    }

    @PostMapping("/{pid}/receive")
    public ApiResponse<OrderVo> receive(@PathVariable String pid) {
        return ApiResponse.success("确认收货成功", orderService.receive(AuthContext.get().getUid(), pid));
    }

    @DeleteMapping("/{pid}")
    public ApiResponse<Void> delete(@PathVariable String pid) {
        orderService.delete(AuthContext.get().getUid(), pid);
        return ApiResponse.success("订单已删除", null);
    }
}
