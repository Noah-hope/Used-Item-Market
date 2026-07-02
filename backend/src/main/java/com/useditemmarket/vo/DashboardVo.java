package com.useditemmarket.vo;

public class DashboardVo {
    private Long goodsPublished;
    private Long goodsActive;
    private Long goodsPending;
    private Long totalOrders;
    private Long completedOrders;
    private Long disabledUsers;
    private Long wantedOpen;

    public Long getGoodsPublished() {
        return goodsPublished;
    }

    public void setGoodsPublished(Long goodsPublished) {
        this.goodsPublished = goodsPublished;
    }

    public Long getGoodsActive() {
        return goodsActive;
    }

    public void setGoodsActive(Long goodsActive) {
        this.goodsActive = goodsActive;
    }

    public Long getGoodsPending() {
        return goodsPending;
    }

    public void setGoodsPending(Long goodsPending) {
        this.goodsPending = goodsPending;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(Long completedOrders) {
        this.completedOrders = completedOrders;
    }

    public Long getDisabledUsers() {
        return disabledUsers;
    }

    public void setDisabledUsers(Long disabledUsers) {
        this.disabledUsers = disabledUsers;
    }

    public Long getWantedOpen() {
        return wantedOpen;
    }

    public void setWantedOpen(Long wantedOpen) {
        this.wantedOpen = wantedOpen;
    }
}
