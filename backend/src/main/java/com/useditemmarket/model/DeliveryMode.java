package com.useditemmarket.model;

public enum DeliveryMode {
    SELF_PICKUP("买家自提"),
    CAMPUS_DELIVERY("商家送货到指定地址");

    private final String label;

    DeliveryMode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static DeliveryMode fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return SELF_PICKUP;
        }
        String normalized = value.trim().toUpperCase();
        if ("BOTH".equals(normalized)) {
            return SELF_PICKUP;
        }
        try {
            return DeliveryMode.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            for (DeliveryMode mode : values()) {
                if (mode.label.equals(value)) {
                    return mode;
                }
            }
            return SELF_PICKUP;
        }
    }
}
