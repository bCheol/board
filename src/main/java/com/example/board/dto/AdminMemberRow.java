package com.example.board.dto;

import lombok.Getter;

@Getter
public class AdminMemberRow {

    private final String accountId;
    private final String nickName;
    private final Integer exist;
    private final Integer warningCount;
    private final Integer block;
    private final String role;
    private final String roleType;

    public AdminMemberRow(AdminMemberListView view, String roleType) {
        this.accountId = view.getAccountId();
        this.nickName = view.getNickName();
        this.exist = view.getExist();
        this.warningCount = view.getWarningCount();
        this.block = view.getBlock();
        this.role = view.getRole();
        this.roleType = roleType;
    }
}