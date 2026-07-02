package com.useditemmarket.model;

public enum DeliveryMode {
    SELF_PICKUP("自提"),
    CAMPUS_DELIVERY("送货到校"),
    BOTH("自提/送货均可");

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
        try {
            return DeliveryMode.valueOf(value.trim().toUpperCase());
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
