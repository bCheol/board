package com.example.board.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_info_temp")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountTempEntity {

    @Id
    @Column(name = "account_id")
    private String accountId;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

}