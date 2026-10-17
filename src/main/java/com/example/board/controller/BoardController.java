package com.example.board.controller;

import com.example.board.dto.AccountUserDetails;
import com.example.board.dto.BoardSearchCondition;
import com.example.board.dto.ResponseListResult;
import com.example.board.entity.BoardEntity;
import com.example.board.service.BoardService;
import com.example.board.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.concurrent.ExecutorService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final ExecutorService threadPool;

    private final CacheManager cacheManager;
    private final SchedulerService schedulerService;

    @GetMapping({"", "/"})
    public String list(@ModelAttribute BoardSearchCondition cond, Model model) {

        cond.setCache(schedulerService.runningScheduler());

        ResponseListResult<BoardEntity> result = boardService.list(cond);

        model.addAttribute("boards", result.getDataList());
        model.addAttribute("pageInfo", result.getPageInfo());
        model.addAttribute("cond", cond);

        return "board/list";
    }

    @GetMapping("/board/detail/{boardNum}")
    public String detail(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @PathVariable int boardNum, Model model, @RequestParam(defaultValue = "0") int page, RedirectAttributes ra) {

        BoardEntity board = boardService.getById(boardNum);
        if (board == null) {
            ra.addFlashAttribute("message", "존재하지 않는 게시글입니다.");
            return "redirect:/";
        }

        if (board.getBlock() == 1) {
            boolean isAdmin = accountUserDetails != null && accountUserDetails.getRole().contains("ADMIN");
            if (!isAdmin) {
                model.addAttribute("blocked", true);
                return "board/detail";
            }
            model.addAttribute("board", board);
            model.addAttribute("blockedNotice", true);
            return "board/detail";
        }

        board.setViewCount(board.getViewCount() + 1);
        model.addAttribute("board", board);

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                boardService.increaseViewCount(boardNum);
                if (schedulerService.runningScheduler() && (1 <= page && page <= 3))
                    cacheModify("viewCount", page, boardNum, null);
            }
        });
        return "board/detail";
    }

    @GetMapping("/board/write")
    public String writeForm(@AuthenticationPrincipal AccountUserDetails accountUserDetails, Model model) {

        if (accountUserDetails == null) {
            return "redirect:/account/login";
        }
        model.addAttribute("mode", "create");
        return "board/write";
    }

    @PostMapping("/board/write")
    public String write(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @RequestParam Map<String, String> param, RedirectAttributes ra) {

        if (accountUserDetails == null) {
            return "redirect:/account/login";
        }

        String title = param.get("title");
        String body = param.get("body");
        if (title == null || title.trim().equals("") || body == null || body.trim().equals("")) {
            ra.addFlashAttribute("message", "입력 값을 확인해주세요.");
            return "redirect:/board/write";
        }
        if (35 < title.length()) {
            ra.addFlashAttribute("message", "제목은 35자까지 가능합니다.");
            return "redirect:/board/write";
        }
        if (100 < body.length()) {
            ra.addFlashAttribute("message", "내용은 100자까지 가능합니다.");
            return "redirect:/board/write";
        }

        BoardEntity boardEntity = new BoardEntity(title, body, accountUserDetails.getUsername());

        try {
            boardService.write(boardEntity);
            if (schedulerService.runningScheduler())
                schedulerService.cacheUpdate();
        } catch (Exception e) {
            log.error("/board/write", e);
            ra.addFlashAttribute("message", "등록에 실패했습니다.");
            ra.addFlashAttribute("board", boardEntity);
            return "redirect:/board/write";
        }

        ra.addFlashAttribute("message", "게시글이 등록되었습니다.");
        return "redirect:/board/detail/" + boardEntity.getNum();
    }

    @GetMapping("/board/edit/{boardNum}")
    public String editForm(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @PathVariable int boardNum, Model model, RedirectAttributes ra) {

        if (accountUserDetails == null) {
            return "redirect:/account/login";
        }

        BoardEntity boardEntity = boardService.getById(boardNum);

        if (boardEntity == null) {
            ra.addFlashAttribute("message", "존재하지 않는 게시글입니다.");
            return "redirect:/";
        }
        if (!accountUserDetails.getUsername().equals(boardEntity.getAccountId())) {
            ra.addFlashAttribute("message", "본인이 작성한 글만 수정할 수 있습니다.");
            return "redirect:/board/detail/" + boardNum;
        }

        if (model.getAttribute("title") != null) {
            boardEntity.setTitle((String) model.getAttribute("title"));
            boardEntity.setBody((String) model.getAttribute("body"));
        }

        model.addAttribute("board", boardEntity);
        model.addAttribute("mode", "edit");
        return "board/write";
    }

    @PostMapping("/board/edit/{boardNum}/update")
    public String update(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @PathVariable int boardNum, @RequestParam Map<String, String> param, RedirectAttributes ra) {

        if (accountUserDetails == null) {
            return "redirect:/account/login";
        }
        String title = param.get("title");
        String body = param.get("body");
        if (title == null || title.trim().isBlank() || body == null || body.trim().isBlank()) {
            ra.addFlashAttribute("message", "입력 값을 확인해주세요.");
            return "redirect:/board/edit/"+boardNum;
        }
        if (35 < title.length()) {
            ra.addFlashAttribute("message", "제목은 35자까지 가능합니다.");
            return "redirect:/board/edit/"+boardNum;
        }
        if (100 < body.length()) {
            ra.addFlashAttribute("message", "내용은 100자까지 가능합니다.");
            return "redirect:/board/edit/"+boardNum;
        }

        int success = 0;
        try {
            success = boardService.update(boardNum, accountUserDetails.getUsername(), title, body);
        } catch (Exception e) {
            log.error("/board/edit/{boardNum}/update", e);
            ra.addFlashAttribute("message", "수정에 실패했습니다.");
            ra.addFlashAttribute("title", title);
            ra.addFlashAttribute("body", body);
            return "redirect:/board/edit/" + boardNum;
        }

        switch (success) {
            case 1:
                ra.addFlashAttribute("message", "게시글이 수정되었습니다.");
                int page = 0;
                if (param.get("page") != null)
                    page = Integer.parseInt(param.get("page"));
                if (schedulerService.runningScheduler() && (1 <= page && page <= 3))
                    cacheModify(null, page, boardNum, title);
            break;
            case 0:
                ra.addFlashAttribute("message", "글이 존재하지 않습니다.");
            break;
            case -1:
                ra.addFlashAttribute("message", "본인이 작성한 글만 수정할 수 있습니다.");
            break;
        }

        ra.addAttribute("page", param.get("page"));
        ra.addAttribute("searchType", param.get("searchType"));
        ra.addAttribute("keyword", param.get("keyword"));
        ra.addAttribute("sort", param.get("sort"));

        return "redirect:/board/detail/" + boardNum;
    }

    @PostMapping("/board/delete/{boardNum}")
    public String delete(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @PathVariable int boardNum, @RequestParam(defaultValue = "0") int page, RedirectAttributes ra) {

        if (accountUserDetails == null) {
            return "redirect:/account/login";
        }

        int success = 0;
        try {
            success = boardService.delete(boardNum, accountUserDetails.getUsername());
        } catch (Exception e) {
            log.error("/board/delete/{boardNum}", e);
            ra.addFlashAttribute("message", "삭제에 실패했습니다.");
            return "redirect:/board/detail/" + boardNum;
        }

        switch (success) {
            case 1:
                ra.addFlashAttribute("message", "게시글이 삭제되었습니다.");
                if (schedulerService.runningScheduler() && (1 <= page && page <= 3))
                    schedulerService.cacheUpdate();
                return "redirect:/";
            case 0:
                ra.addFlashAttribute("message", "글이 존재하지 않습니다.");
                break;
            case -1:
                ra.addFlashAttribute("message", "차단된 게시물은 삭제할 수 없습니다.");
                break;
            case -2:
                ra.addFlashAttribute("message", "본인이 작성한 글만 삭제할 수 있습니다.");
                break;
        }
        return "redirect:/board/detail/" + boardNum;
    }

    public void cacheModify(String type, int page, int boardNum, String title) {

        Cache cache = cacheManager.getCache("ltBoardList");
        ResponseListResult<BoardEntity> value = cache.get(page, ResponseListResult.class);
        if (value == null)
            return;
        if ("viewCount".equals(type)) {
            value.getDataList().stream().filter(board -> boardNum == board.getNum()).forEach(board -> board.setViewCount(board.getViewCount() + 1));
        } else {
            value.getDataList().stream().filter(board -> boardNum == board.getNum()).forEach(board -> board.setTitle(title));
        }
    }

}