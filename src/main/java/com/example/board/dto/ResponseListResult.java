package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ResponseListResult<T> {

    private List<T> dataList;
    private PageInfo pageInfo;
}