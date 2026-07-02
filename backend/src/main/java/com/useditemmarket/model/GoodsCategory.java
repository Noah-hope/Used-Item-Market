package com.useditemmarket.model;

public enum GoodsCategory {
    TEXTBOOK("教材"),
    DIGITAL("数码"),
    DORMITORY("宿舍用品"),
    SPORTS("运动器材"),
    DAILY("日用品"),
    OTHER("其他");

    private final String label;

    GoodsCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if ("书".equals(trimmed) || "书籍".equals(trimmed) || "学习用品".equals(trimmed)) {
            return TEXTBOOK.label;
        }
        if ("床上用品".equals(trimmed) || "宿舍".equals(trimmed)) {
            return DORMITORY.label;
        }
        for (GoodsCategory category : values()) {
            if (category.name().equalsIgnoreCase(trimmed) || category.label.equals(trimmed)) {
                return category.label;
            }
        }
        return trimmed;
    }
}
