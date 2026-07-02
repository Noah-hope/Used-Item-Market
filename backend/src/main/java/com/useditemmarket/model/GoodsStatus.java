package com.useditemmarket.model;

public enum GoodsStatus {
    PENDING_REVIEW("待审核"),
    ACTIVE("上架中"),
    REJECTED("已驳回"),
    OFF_SHELF("已下架"),
    BANNED("违规下架");

    private final String label;

    GoodsStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static GoodsStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return PENDING_REVIEW;
        }
        try {
            return GoodsStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return PENDING_REVIEW;
        }
    }
}
