package com.example.board.dto;

public interface AdminMemberListView {

    String getAccountId();
    String getRole();
    String getNickName();
    Integer getExist();
    Integer getWarningCount();
    Integer getBlock();
}