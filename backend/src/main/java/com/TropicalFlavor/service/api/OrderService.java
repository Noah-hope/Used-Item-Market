package com.TropicalFlavor.service.api;

import com.TropicalFlavor.dto.OrderCreateRequest;
import com.TropicalFlavor.model.OrderStatus;
import com.TropicalFlavor.vo.OrderVo;

import java.util.List;

public interface OrderService {
    OrderVo createOrder(String uid, OrderCreateRequest request);

    List<OrderVo> listPurchases(String uid, OrderStatus status);

    List<OrderVo> listSales(String uid, OrderStatus status);

    OrderVo ship(String uid, String pid);

    OrderVo receive(String uid, String pid);
}
