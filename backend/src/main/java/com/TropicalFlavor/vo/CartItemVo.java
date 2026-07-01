package com.TropicalFlavor.vo;

import com.TropicalFlavor.po.MarketGoods;

public class CartItemVo {
    private String gid;
    private String name;
    private String category;
    private Double price;
    private Double quantity;
    private String image;
    private String sellerUid;

    public static CartItemVo from(MarketGoods goods) {
        return from(goods, null);
    }

    public static CartItemVo from(MarketGoods goods, String sellerUid) {
        CartItemVo vo = new CartItemVo();
        vo.gid = goods.getGID();
        vo.name = goods.getName();
        vo.category = goods.getKind();
        vo.price = goods.getPrice();
        vo.quantity = goods.getNumber();
        vo.image = goods.getImage();
        vo.sellerUid = sellerUid;
        return vo;
    }

    public String getGid() {
        return gid;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Double getPrice() {
        return price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public String getImage() {
        return image;
    }

    public String getSellerUid() {
        return sellerUid;
    }
}
