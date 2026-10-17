package com.example.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "account_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class AccountEntity {

    @Id
    @Column(name = "account_id")
    private String accountId;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "exist")
    private Integer exist;

    @Column(name = "warning_count")
    private Integer warningCount;

    @Column(name = "block")
    private Integer block;

    public AccountEntity(String accountId, String name, String phone) {
        this.accountId = accountId;
        this.name = name;
        this.phone = phone;
    }
}