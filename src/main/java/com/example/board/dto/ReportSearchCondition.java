package com.example.board.dto;

import lombok.Setter;

@Setter
public class ReportSearchCondition {

    private String status;
    private int page;
    private int size = 15;

    public String getStatus() {
        if ("pending".equals(status))
            return status;
        if ("done".equals(status))
            return status;

        return "";
    }

    public int getPage() {
        return Math.max(page, 1);
    }

    public int getSize() {
        return size;
    }
}