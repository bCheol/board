package com.example.board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num")
    private Integer num;

    @Column(name = "title")
    private String title;

    @Column(name = "body")
    private String body;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "create_tm")
    private LocalDateTime createTm;

    @Column(name = "update_tm")
    private LocalDateTime updateTm;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "block")
    private Integer block;

    @ManyToOne
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private AccountEntity accountEntity;

    @Transient
    private String tempWriter;

    public BoardEntity(String title, String body, String accountId) {
        this.title = title;
        this.body = body;
        this.accountId = accountId;
    }

    public String getWriterNickName() {
        return accountEntity != null ? accountEntity.getNickName() : tempWriter;
    }
}