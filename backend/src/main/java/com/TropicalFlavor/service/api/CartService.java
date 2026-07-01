package com.TropicalFlavor.service.api;

import com.TropicalFlavor.vo.CartItemVo;

import java.util.List;

public interface CartService {
    List<CartItemVo> listCart(String uid);

    List<CartItemVo> addItem(String uid, String gid, Double quantity);

    List<CartItemVo> updateItem(String uid, String gid, Double quantity);

    List<CartItemVo> removeItem(String uid, String gid);
}
