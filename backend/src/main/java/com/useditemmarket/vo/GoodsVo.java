package com.useditemmarket.vo;

import com.useditemmarket.po.MarketGoods;

public class GoodsVo {
    private String gid;
    private String name;
    private String category;
    private Double price;
    private Double stock;
    private String image;
    private String comment;
    private String sellerUid;
    private String sellerName;
    private String status;
    private String deliveryMode;
    private String pickupLocation;
    private String reviewNote;
    private String publishedAt;

    public static GoodsVo from(MarketGoods goods, String sellerUid) {
        return from(goods, sellerUid, null);
    }

    public static GoodsVo from(MarketGoods goods, String sellerUid, String sellerName) {
        GoodsVo vo = new GoodsVo();
        vo.gid = goods.getGID();
        vo.name = goods.getName();
        vo.category = goods.getKind();
        vo.price = goods.getPrice();
        vo.stock = goods.getNumber();
        vo.image = goods.getImage();
        vo.comment = goods.getComment();
        vo.sellerUid = sellerUid;
        vo.sellerName = sellerName;
        vo.status = goods.getStatus();
        vo.deliveryMode = goods.getDeliveryMode();
        vo.pickupLocation = goods.getPickupLocation();
        vo.reviewNote = goods.getReviewNote();
        vo.publishedAt = goods.getPublishedAt();
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getReviewNote() {
        return reviewNote;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}
