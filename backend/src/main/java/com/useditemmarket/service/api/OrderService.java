package com.useditemmarket.service.api;

import com.useditemmarket.dto.OrderCreateRequest;
import com.useditemmarket.model.OrderStatus;
import com.useditemmarket.vo.OrderVo;

import java.util.List;

public interface OrderService {
    OrderVo createOrder(String uid, OrderCreateRequest request);

    List<OrderVo> listPurchases(String uid, OrderStatus status);

    List<OrderVo> listSales(String uid, OrderStatus status);

    OrderVo ship(String uid, String pid);

    OrderVo receive(String uid, String pid);

    void delete(String uid, String pid);
}
