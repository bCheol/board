package com.example.board.service;

import com.example.board.dto.PageInfo;
import com.example.board.dto.ReportSearchCondition;
import com.example.board.dto.ResponseListResult;
import com.example.board.entity.AccountEntity;
import com.example.board.entity.BoardEntity;
import com.example.board.entity.ReportBoardEntity;
import com.example.board.repository.AccountRepository;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.ReportBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportBoardRepository reportBoardRepository;
    private final BoardRepository boardRepository;
    private final AccountRepository accountRepository;

    String RESULT_REJECT = "반려 (이상 없음)";

    public ResponseListResult<ReportBoardEntity> listReports(ReportSearchCondition cond) {

        Pageable pageable = PageRequest.of(cond.getPage() - 1, cond.getSize(), Sort.by(Sort.Direction.DESC, "num"));

        Page<ReportBoardEntity> page;
        if ("pending".equals(cond.getStatus())) {
            page = reportBoardRepository.findByResultIsNull(pageable);
        } else if ("done".equals(cond.getStatus())) {
            page = reportBoardRepository.findByResultIsNotNull(pageable);
        } else {
            page = reportBoardRepository.findAll(pageable);
        }

        PageInfo pageInfo = new PageInfo(cond.getPage(), cond.getSize(), page.getTotalElements());
        return new ResponseListResult<ReportBoardEntity>(page.getContent(), pageInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public int report(int boardNum, String fromId, String reason) {

        BoardEntity boardEntity = boardRepository.findById(boardNum).orElse(null);

        if (boardEntity == null)
            return 0;

        if (boardEntity.getAccountId() != null && boardEntity.getAccountId().equals(fromId)) {
            return -1;
        }
        if (reportBoardRepository.findByBoardNumAndFromId(boardNum, fromId) == null) {
            ReportBoardEntity newReport = new ReportBoardEntity(boardNum, fromId, reason);
            newReport.setRequestTm(LocalDateTime.now());
            reportBoardRepository.save(newReport);
            return 1;
        } else {
            return 2;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public String processReport(int num, String choiceResult, String adminId) {

        ReportBoardEntity reportBoardEntity = reportBoardRepository.findById(num).orElse(null);
        if (reportBoardEntity == null)
            return "존재하지 않는 신고입니다.";

        BoardEntity boardEntity = boardRepository.findById(reportBoardEntity.getBoardNum()).orElse(null);
        if (boardEntity == null)
            return "존재하지 않는 게시글입니다.";

        List<ReportBoardEntity> reportBoardList = reportBoardRepository.findByBoardNum(reportBoardEntity.getBoardNum());
        if (reportBoardList.size() == 1) {
            if (reportBoardEntity.getResult() == null) {
                if (!RESULT_REJECT.equals(choiceResult)) {
                    boardEntity.setBlock(1);
                    changeAccountWarning("increase", boardEntity.getAccountId());
                }
                reportBoardEntity.process(choiceResult, adminId);
            } else {
                if (reportBoardEntity.getResult().equals(choiceResult)) {
                    return "처리 결과가 같습니다.";
                } else {
                    if (RESULT_REJECT.equals(choiceResult)) {
                        boardEntity.setBlock(0);
                        changeAccountWarning("decrease", boardEntity.getAccountId());
                    } else {
                        boardEntity.setBlock(1);
                        changeAccountWarning("increase", boardEntity.getAccountId());
                    }
                    reportBoardEntity.process(choiceResult, adminId);
                }
            }

        } else {
            long nullCount = reportBoardList.stream().filter(s -> s.getResult() == null).count();
            long notNullCount = reportBoardList.stream().filter(s -> s.getResult() != null).count();

            if (reportBoardList.size() == nullCount) {
                if (RESULT_REJECT.equals(choiceResult)) {
                    boardEntity.setBlock(0);
                } else {
                    boardEntity.setBlock(1);
                    changeAccountWarning("increase", boardEntity.getAccountId());
                }
                reportBoardList.stream().forEach(s -> s.process(choiceResult, adminId));
            } else if (reportBoardList.size() == notNullCount) {
                if (reportBoardList.get(0).getResult().equals(choiceResult)) {
                    return "처리 결과가 같습니다.";
                } else {
                    if (RESULT_REJECT.equals(choiceResult)) {
                        boardEntity.setBlock(0);
                        changeAccountWarning("decrease", boardEntity.getAccountId());
                    } else {
                        boardEntity.setBlock(1);
                        changeAccountWarning("increase", boardEntity.getAccountId());
                    }
                    reportBoardList.stream().forEach(s -> s.process(choiceResult, adminId));
                }
            } else {
                if (reportBoardEntity.getResult() == null) {
                    if (reportBoardList.get(0).getResult().equals(choiceResult)) {
                        reportBoardList.stream().filter(s->s.getResult()==null).forEach(s -> s.process(choiceResult, adminId));
                    } else {
                        return "이전 처리 결과와 다릅니다.";
                    }
                } else if (RESULT_REJECT.equals(reportBoardEntity.getResult())) {
                    if (RESULT_REJECT.equals(choiceResult)) {
                        return "처리 결과가 같습니다.";
                    } else {
                        boardEntity.setBlock(1);
                        changeAccountWarning("increase", boardEntity.getAccountId());
                        reportBoardList.stream().forEach(s -> s.process(choiceResult, adminId));
                    }
                } else {
                    if (RESULT_REJECT.equals(choiceResult)) {
                        boardEntity.setBlock(0);
                        changeAccountWarning("decrease", boardEntity.getAccountId());
                        reportBoardList.stream().forEach(s -> s.process(choiceResult, adminId));
                    } else {
                        return "처리 결과가 같습니다.";
                    }
                }
            }
        }
        return null;
    }

    public void changeAccountWarning(String type, String accountId) {

        AccountEntity accountEntity = accountRepository.findByAccountIdAndExist(accountId, 1);
        if (accountEntity == null)
            return;

        if ("decrease".equals(type)) {
            if (accountEntity.getWarningCount() > 0)
                accountEntity.setWarningCount(accountEntity.getWarningCount() - 1);
        } else {
            accountEntity.setWarningCount(accountEntity.getWarningCount() + 1);
        }
    }

}