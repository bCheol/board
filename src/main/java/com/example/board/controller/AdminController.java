package com.example.board.controller;

import com.example.board.dto.*;
import com.example.board.entity.ReportBoardEntity;
import com.example.board.service.AccountService;
import com.example.board.service.AdminService;
import com.example.board.service.ReportService;
import com.example.board.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AccountService accountService;
    private final ReportService reportService;
    private final SchedulerService schedulerService;

    /** 글 신고 목록 */
    @GetMapping("/reports")
    public String reports(@ModelAttribute ReportSearchCondition cond, Model model) {

        ResponseListResult<ReportBoardEntity> result = reportService.listReports(cond);
        model.addAttribute("reports", result.getDataList());
        model.addAttribute("pageInfo", result.getPageInfo());
        model.addAttribute("cond", cond);
        return "admin/reports";
    }

    /** 글 신고 목록에서 관리 */
    @PostMapping("/reports/{num}/process")
    @ResponseBody
    public ResponseResult processReport(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @PathVariable int num, @RequestParam String result) {

        if (result == null || result.trim().isEmpty()) {
            return new ResponseResult(false, "처리 결과를 선택하세요.");
        }
        if (!("반려 (이상 없음)".equals(result) || "게시글 비공개".equals(result))) {
            return new ResponseResult(false, "처리 결과가 잘못됐습니다.");
        }

        String serviceResult = reportService.processReport(num, result, accountUserDetails.getUsername());
        if (serviceResult == null) {
            if (schedulerService.runningScheduler())
                schedulerService.cacheUpdate();
            return new ResponseResult(true, "신고를 처리했습니다.");
        } else {
            return new ResponseResult(false, serviceResult);
        }
    }

    /** 회원 목록 */
    @GetMapping("/members")
    public String members(@ModelAttribute AdminSearchCondition cond, Model model) {

        ResponseListResult<AdminMemberRow> result = adminService.listMembers(cond);
        model.addAttribute("members", result.getDataList());
        model.addAttribute("pageInfo", result.getPageInfo());
        model.addAttribute("cond", cond);
        return "admin/members";
    }

    /** 회원 차단  accountId 차단할 ID, password 관리자 PW */
    @PostMapping("/members/block")
    @ResponseBody
    public ResponseResult block(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @RequestParam String accountId, @RequestParam String password) {

        if (accountUserDetails.getUsername().equals(accountId)) {
            return new ResponseResult(false, "본인 계정은 차단할 수 없습니다.");
        }
        if (!accountService.verifyPassword(accountUserDetails.getUsername(), password)) {
            return new ResponseResult(false, "비밀번호가 일치하지 않습니다.");
        } else {
            if (adminService.changeBlock(accountId, 1) == 1) {
                return new ResponseResult(true, "차단되었습니다.");
            } else {
                return new ResponseResult(false, "존재하지 않는 회원입니다.");
            }
        }
    }

    /** 회원 해제  accountId 차단할 ID, password 관리자 PW */
    @PostMapping("/members/unblock")
    @ResponseBody
    public ResponseResult unblock(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @RequestParam String accountId, @RequestParam String password) {

        if (accountUserDetails.getUsername().equals(accountId)) {
            return new ResponseResult(false, "본인 계정은 차단 해제할 수 없습니다.");
        }
        if (!accountService.verifyPassword(accountUserDetails.getUsername(), password)) {
            return new ResponseResult(false, "비밀번호가 일치하지 않습니다.");
        } else {
            if (adminService.changeBlock(accountId, 0) == 1) {
                return new ResponseResult(true, "차단 해제되었습니다.");
            } else {
                return new ResponseResult(false, "존재하지 않는 회원입니다.");
            }
        }
    }

    /** 권한 변경  accountId 차단할 ID, roleType 변경할 role, password 관리자 PW */
    @PostMapping("/members/role")
    @ResponseBody
    public ResponseResult changeRole(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @RequestParam String accountId, @RequestParam String roleType, @RequestParam String password) {

        if (accountUserDetails.getUsername().equals(accountId)) {
            return new ResponseResult(false, "본인 계정은 변경할 수 없습니다.");
        }
        if (!accountService.verifyPassword(accountUserDetails.getUsername(), password)) {
            return new ResponseResult(false, "비밀번호가 일치하지 않습니다.");
        } else {
            if ("USER".equals(roleType) || "LOWER_ADMIN".equals(roleType) ||"UPPER_ADMIN".equals(roleType)) {
                int result = adminService.changeMemberRole(accountId, roleType);
                if (result == 1) {
                    return new ResponseResult(true, "권한을 변경했습니다.");
                } else if (result == -1) {
                    return new ResponseResult(true, "차단된 회원은 권한을 변경할 수 없습니다.");
                } else {
                    return new ResponseResult(true, "존재하지 않는 회원입니다.");
                }
            } else {
                return new ResponseResult(false, "권한 정보를 찾을 수 없습니다.");
            }
        }
    }

    /** 스케줄러 관리 */
    @GetMapping("/scheduler")
    public String scheduler(Model model) {

        Map<String, Object> map = new HashMap<>();
        map.put("num", "1");
        map.put("name", "캐시 스케줄러");
        map.put("cycle", "60s");
        map.put("state", schedulerService.runningScheduler());
        model.addAttribute("schedulerMap", map);

        return "admin/scheduler";
    }

    @PostMapping("/scheduler/start")
    @ResponseBody
    public ResponseResult startScheduler(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @RequestParam Map<String, String> param) {

        if (!accountService.verifyPassword(accountUserDetails.getUsername(), param.get("password"))) {
            return new ResponseResult(false, "비밀번호가 일치하지 않습니다.");
        }
        try {
            schedulerService.startScheduler();
            return new ResponseResult(true, "스케줄러를 실행했습니다.");
        } catch (RuntimeException e) {
            return new ResponseResult(false, "다시 시도해 주세요.");
        }
    }

    @PostMapping("/scheduler/stop")
    @ResponseBody
    public ResponseResult stopScheduler(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @RequestParam Map<String, String> param) {

        if (!accountService.verifyPassword(accountUserDetails.getUsername(), param.get("password"))) {
            return new ResponseResult(false, "비밀번호가 일치하지 않습니다.");
        }
        try {
            schedulerService.stopScheduler();
            return new ResponseResult(true, "스케줄러를 중단했습니다.");
        } catch (RuntimeException e) {
            return new ResponseResult(false, "다시 시도해 주세요.");
        }
    }

    /** 설정 관리 */
    @GetMapping("/setting")
    public String setting(Model model) {

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Map.of("num", "4", "name", "임시 테이블 비우기", "type", "delete_temp"));
        list.add(Map.of("num", "3", "name", "변경 값으로 운영 테이블 갱신", "type", "org_change"));
        list.add(Map.of("num", "2", "name", "임시 테이블 값 변경", "type", "temp_change"));
        list.add(Map.of("num", "1", "name", "임시 테이블로 복사", "type", "copy"));

        model.addAttribute("settingList", list);
        return "admin/setting";
    }

    @PostMapping("/setting")
    @ResponseBody
    public ResponseResult startSetting(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @RequestParam Map<String, String> param) {

        if (!accountService.verifyPassword(accountUserDetails.getUsername(), param.get("password"))) {
            return new ResponseResult(false, "비밀번호가 일치하지 않습니다.");
        }
        String type = param.get("type");
        if (type == null) {
            return new ResponseResult(false, "type error");
        }

        switch (type) {
            case "copy":
                try {
                    adminService.copyTable();
                    return new ResponseResult(true, "복사에 성공했습니다");
                } catch (RuntimeException e) {
                    return new ResponseResult(false, "복사에 실패했습니다");
                }
            case "temp_change":
                try {
                    adminService.tempTableChange(param.get("encryptKey"));
                    return new ResponseResult(true, "임시 테이블 값 변경 성공했습니다");
                } catch (Exception e) {
                    return new ResponseResult(false, "임시 테이블 값 변경 실패했습니다");
                }
            case "org_change":
                try {
                    adminService.orgTableChange();
                    return new ResponseResult(true, "운영 테이블 값 변경 성공했습니다");
                } catch (Exception e) {
                    return new ResponseResult(false, "운영 테이블 값 변경 실패했습니다");
                }
            case "delete_temp":
                try {
                    adminService.deleteTempTable();
                    return new ResponseResult(true, "임시 테이블 비우기 성공했습니다");
                } catch (Exception e) {
                    return new ResponseResult(false, "임시 테이블 비우기 실패했습니다");
                }
        }

        return new ResponseResult(false, null);
    }

    @PostMapping("/setting/stop/{num}")
    @ResponseBody
    public void stopSetting(@PathVariable int num) {

        AdminService.cancel[num] = true;
    }

}