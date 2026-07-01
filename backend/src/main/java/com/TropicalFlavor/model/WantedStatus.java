package com.TropicalFlavor.model;

public enum WantedStatus {
    OPEN("进行中"),
    CLOSED("已关闭");

    private final String label;

    WantedStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static WantedStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return OPEN;
        }
        try {
            return WantedStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return OPEN;
        }
    }
}
