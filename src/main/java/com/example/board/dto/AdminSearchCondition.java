package com.example.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminSearchCondition {

    private String roleType;
    private String existYn;
    private String blockYn;
    private String keyword;
    private int page;
    private int size = 15;

    public String getRoleType() {
        if ("admin".equals(roleType))
            return this.roleType;
        if ("user".equals(roleType))
            return this.roleType;

        return "";
    }

    public String getExistYn() {
        if ("1".equals(existYn))
            return existYn;
        if ("0".equals(existYn))
            return existYn;

        return "";
    }

    public String getBlockYn() {
        if ("1".equals(blockYn))
            return blockYn;
        if ("0".equals(blockYn))
            return blockYn;

        return "";
    }

    public int getPage() {
        return Math.max(page, 1);
    }
    public int getSize() {
        return size;
    }
}