package com.useditemmarket.model;

public enum OrderStatus {
    PENDING_CONTACT(false, false, "待沟通"),
    PENDING_PICKUP(true, false, "待自提"),
    COMPLETED(true, true, "已完成");

    private final boolean sent;
    private final boolean got;
    private final String label;

    OrderStatus(boolean sent, boolean got, String label) {
        this.sent = sent;
        this.got = got;
        this.label = label;
    }

    public boolean isSent() {
        return sent;
    }

    public boolean isGot() {
        return got;
    }

    public String getLabel() {
        return label;
    }

    public static OrderStatus fromFlags(boolean sent, boolean got) {
        if (!sent && !got) {
            return PENDING_CONTACT;
        }
        if (sent && !got) {
            return PENDING_PICKUP;
        }
        return COMPLETED;
    }

    public static OrderStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return PENDING_CONTACT;
        }
        try {
            return OrderStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return PENDING_CONTACT;
        }
    }
}
