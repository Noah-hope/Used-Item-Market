package com.useditemmarket.model;

public enum OrderStatus {
    PENDING_PICKUP("待自提"),
    PENDING_SHIPMENT("待发货"),
    PENDING_RECEIPT("待收货"),
    COMPLETED("已完成");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static OrderStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return PENDING_PICKUP;
        }
        try {
            return OrderStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return PENDING_PICKUP;
        }
    }
}
