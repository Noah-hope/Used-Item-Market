package com.useditemmarket.model;

public enum GoodsSortType {
    PRICE_ASC,
    PRICE_DESC,
    STOCK_ASC,
    STOCK_DESC,
    LATEST;

    public static GoodsSortType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return LATEST;
        }
        try {
            return GoodsSortType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return LATEST;
        }
    }
}
