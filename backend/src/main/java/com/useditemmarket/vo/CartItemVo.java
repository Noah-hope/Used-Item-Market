package com.useditemmarket.vo;

import com.useditemmarket.po.MarketGoods;

public class CartItemVo {
    private String gid;
    private String name;
    private String category;
    private Double price;
    private Double quantity;
    private Double stock;
    private String image;
    private String sellerUid;
    private String sellerName;
    private String status;

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
        vo.stock = goods.getNumber();
        vo.image = goods.getImage();
        vo.sellerUid = sellerUid;
        vo.status = goods.getStatus();
        return vo;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getStock() {
        return stock;
    }

    public void setStock(Double stock) {
        this.stock = stock;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSellerUid() {
        return sellerUid;
    }

    public void setSellerUid(String sellerUid) {
        this.sellerUid = sellerUid;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
