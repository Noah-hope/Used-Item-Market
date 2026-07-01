package com.TropicalFlavor.vo;

import com.TropicalFlavor.po.MarketGoods;

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

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Double getPrice() {
        return price;
    }

    public Double getStock() {
        return stock;
    }

    public String getImage() {
        return image;
    }

    public String getComment() {
        return comment;
    }

    public String getSellerUid() {
        return sellerUid;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getStatus() {
        return status;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getReviewNote() {
        return reviewNote;
    }

    public String getPublishedAt() {
        return publishedAt;
    }
}
