package com.useditemmarket.model;

public class PageQuery {
    private int page = 1;
    private int pageSize = 12;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page < 1 ? 1 : page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize < 1) {
            this.pageSize = 12;
        } else if (pageSize > 100) {
            this.pageSize = 100;
        } else {
            this.pageSize = pageSize;
        }
    }

    public int getOffset() {
        return (page - 1) * pageSize;
    }
}
