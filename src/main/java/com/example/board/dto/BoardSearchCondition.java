package com.example.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardSearchCondition {

    private String searchType;
    private String keyword;
    private String sort;
    private int page;
    private int size = 15;
    private boolean cache;

    public String getSearchType() {
        if ("title".equals(searchType))
            return this.searchType;
        if ("body".equals(searchType))
            return this.searchType;

        return "all";
    }

    public String getSort() {
        if ("view".equals(sort))
            return sort;

        return "latest";
    }

    public int getPage() {
        return Math.max(page, 1);
    }
    public int getSize() {
        return size;
    }
}