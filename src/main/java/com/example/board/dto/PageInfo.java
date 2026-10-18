package com.example.board.dto;

import lombok.Getter;

@Getter
public class PageInfo {

    private static final int PAGE_BLOCK_SIZE = 10;
    private final int page;
    private final int size;
    private final long totalCount;
    private final int totalPages;
    private final int startPage;
    private final int endPage;
    private final boolean hasPrevBlock;
    private final boolean hasNextBlock;

    public PageInfo(int page, int size, long totalCount) {
        this.page = Math.max(page, 1);
        this.size = size <= 0 ? 15 : size;
        this.totalCount = totalCount;
        this.totalPages = (int) Math.max(1, Math.ceil((double) totalCount / this.size));

        int block = (this.page - 1) / PAGE_BLOCK_SIZE;
        this.startPage = block * PAGE_BLOCK_SIZE + 1;
        this.endPage = Math.min(this.startPage + PAGE_BLOCK_SIZE - 1, this.totalPages);
        this.hasPrevBlock = this.startPage > 1;
        this.hasNextBlock = this.endPage < this.totalPages;
    }
}