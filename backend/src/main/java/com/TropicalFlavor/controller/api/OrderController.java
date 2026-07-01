package com.TropicalFlavor.controller.api;

import com.TropicalFlavor.dto.OrderCreateRequest;
import com.TropicalFlavor.model.AuthContext;
import com.TropicalFlavor.model.OrderStatus;
import com.TropicalFlavor.response.ApiResponse;
import com.TropicalFlavor.service.api.OrderService;
import com.TropicalFlavor.vo.OrderVo;
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
    public ApiResponse<List<OrderVo>> purchases(@RequestParam(defaultValue = "PENDING_CONTACT") String status) {
        return ApiResponse.success(orderService.listPurchases(AuthContext.get().getUid(), OrderStatus.valueOf(status)));
    }

    @GetMapping("/sales")
    public ApiResponse<List<OrderVo>> sales(@RequestParam(defaultValue = "PENDING_CONTACT") String status) {
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
}
