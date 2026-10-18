package com.example.board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class ReportBoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num")
    private Integer num;

    @Column(name = "board_num")
    private Integer boardNum;

    @Column(name = "from_id")
    private String fromId;

    @Column(name = "reason")
    private String reason;

    @Column(name = "result")
    private String result;

    @Column(name = "admin_id")
    private String adminId;

    @Column(name = "response_tm")
    private LocalDateTime responseTm;

    @Column(name = "request_tm")
    private LocalDateTime requestTm;

    @ManyToOne
    @JoinColumn(name = "board_num", referencedColumnName = "num", insertable = false, updatable = false, nullable = true)
    BoardEntity boardEntity;

    public ReportBoardEntity(Integer boardNum, String fromId, String reason) {
        this.boardNum = boardNum;
        this.fromId = fromId;
        this.reason = reason;
    }

    public void process(String result, String adminId) {
        this.result = result;
        this.adminId = adminId;
        this.responseTm = LocalDateTime.now();
    }

    public String getBoardTitle() {
        return (boardEntity == null) ? null : boardEntity.getTitle();
    }

    public String getBoardBody() {
        return (boardEntity == null) ? null : boardEntity.getBody();
    }

    public String getBoardAccountId() {
        return (boardEntity == null) ? null : boardEntity.getAccountId();
    }
}