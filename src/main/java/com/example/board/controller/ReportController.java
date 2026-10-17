package com.example.board.controller;

import com.example.board.dto.AccountUserDetails;
import com.example.board.dto.ResponseResult;
import com.example.board.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /** 게시글 신고 모달에서 호출 */
    @PostMapping("/board/report")
    @ResponseBody
    public ResponseResult report(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @RequestParam int boardNum, @RequestParam String reason) {
        if (accountUserDetails == null) {
            return new ResponseResult(false, "로그인이 필요합니다.");
        }
        if (accountUserDetails.getRole().contains("ADMIN")) {
            return new ResponseResult(false, "관리자는 신고할 수 없습니다.");
        }
        if (reason == null || reason.trim().isEmpty()) {
            return new ResponseResult(false, "신고 이유를 입력하세요.");
        }
        if (reason.length() > 50) {
            return new ResponseResult(false, "신고 이유를 50글자 이내로 입력해 주세요.");
        }

        int result = reportService.report(boardNum, accountUserDetails.getUsername(), reason);
        if (result == 1) {
            return new ResponseResult(true, null);
        } else if (result == 2) {
            return new ResponseResult(false, "신고 내역이 존재합니다.");
        } else if (result == -1) {
            return new ResponseResult(false, "본인이 작성한 글은 신고할 수 없습니다.");
        } else {
            return new ResponseResult(false, "존재하지 않는 게시글입니다.");
        }
    }
}