package com.useditemmarket.dto;

import com.useditemmarket.model.GoodsSortType;
import com.useditemmarket.model.PageQuery;

public class GoodsQuery extends PageQuery {
    private String keyword;
    private String category;
    private GoodsSortType sort = GoodsSortType.LATEST;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public GoodsSortType getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = GoodsSortType.fromValue(sort);
    }
}
