package com.example.board.controller;

import com.example.board.config.AesUtil;
import com.example.board.dto.AccountUserDetails;
import com.example.board.dto.ResponseResult;
import com.example.board.entity.AccountEntity;
import com.example.board.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AesUtil aesUtil;

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {

        String errorReason = (String) session.getAttribute("errorReason");
        if ("badCredentials".equals(errorReason)) {
            model.addAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
        } else if ("block".equals(errorReason)) {
            model.addAttribute("error", "차단된 계정입니다.");
        }
        session.removeAttribute("errorReason");

        return "account/login";
}

    @GetMapping("/register")
    public String registerForm() {
        return "account/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String accountId, @RequestParam String password, @RequestParam String passwordConfirm,
                           @RequestParam String nickName, @RequestParam String name, @RequestParam String phone, Model model,
                           RedirectAttributes ra) {

        if (accountId.isBlank() || containsWhitespace(accountId) || password.isBlank() || containsWhitespace(password)
                || passwordConfirm.isBlank() || containsWhitespace(passwordConfirm) || nickName.isBlank() || containsWhitespace(nickName)
                || name.isBlank() || containsWhitespace(name) || phone.isBlank() || containsWhitespace(phone)) {
            model.addAttribute("error", "공백은 입력할 수 없습니다.");
            return "account/register";
        }
        if (15 < accountId.length()) {
            model.addAttribute("error", "아이디는 15자까지 가능합니다.");
            return "account/register";
        }
        if (15 < password.length()) {
            model.addAttribute("error", "비밀번호는 15자까지 가능합니다.");
            return "account/register";
        }
        if (10 < nickName.length()) {
            model.addAttribute("error", "닉네임은 10글자까지 가능합니다.");
            return "account/register";
        }
        if (5 < name.length()) {
            model.addAttribute("error", "이름은 5글자까지 가능합니다.");
            return "account/register";
        }
        if (!(phone.length() == 11)) {
            model.addAttribute("error", "전화번호 11글자만 가능합니다.");
            return "account/register";
        }
        if (!password.equals(passwordConfirm)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "account/register";
        }
        if (!isDigitsOnly(phone)) {
            model.addAttribute("error", "전화번호는 숫자로만 입력하세요.");
            return "account/register";
        }
        try {
            if (accountService.existId(accountId)) {
                model.addAttribute("error", "이미 사용 중인 아이디입니다.");
                return "account/register";
            }
            if (accountService.existNickName(nickName)) {
                model.addAttribute("error", "이미 사용 중인 닉네임입니다.");
                return "account/register";
            }
            if (accountService.existName(name)) {
                model.addAttribute("error", "이미 가입되어 있는 이름입니다.");
                return "account/register";
            }
            if (accountService.existPhone(phone)) {
                model.addAttribute("error", "이미 가입되어 있는 번호입니다.");
                return "account/register";
            }
        } catch (Exception e) {
            log.error("/account/register 1", e);
            model.addAttribute("error", "중복확인에 실패했습니다.");
            return "account/register";
        }

        try {
            accountService.register(accountId, password, nickName, name, phone);
            ra.addFlashAttribute("message", "회원 가입에 성공했습니다.");
        } catch (Exception e) {
            log.error("/account/register 2", e);
            model.addAttribute("error", "회원 가입에 실패했습니다.");
            return "account/register";
        }
        return "redirect:/account/login";
    }

    @GetMapping("/check-id")
    @ResponseBody
    public ResponseResult checkId(@RequestParam String accountId) {

        if (accountId.isBlank() || containsWhitespace(accountId)) {
            return new ResponseResult(false, "아이디에 공백이 존재할 수 없습니다.");
        }
        if (15 < accountId.length()) {
            return new ResponseResult(false, "아이디는 15글자까지 가능합니다.");
        }
        try {
            if (accountService.existId(accountId)) {
                return new ResponseResult(false, "사용 중인 아이디입니다.");
            }
            return new ResponseResult(true, "사용 가능한 아이디입니다.");
        } catch (Exception e) {
            log.error("/account/check-id", e);
            return new ResponseResult(false, "중복확인에 실패했습니다.");
        }
    }

    @GetMapping("/check-nickname")
    @ResponseBody
    public ResponseResult checkNickName(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @RequestParam String nickName) {

        if (nickName.isBlank() || containsWhitespace(nickName)) {
            return new ResponseResult(false, "닉네임에 공백이 존재할 수 없습니다.");
        }
        if (10 < nickName.length()) {
            return new ResponseResult(false, "닉네임은 10글자까지 가능합니다.");
        }
        try {
            if (accountUserDetails == null) {
                if (accountService.existNickName(nickName)) {
                    return new ResponseResult(false, "사용 중인 닉네임입니다.");
                }
                return new ResponseResult(true, "사용 가능한 닉네임입니다.");
            } else {
                if (accountUserDetails.getNickName().equals(nickName)) {
                    return new ResponseResult(false, "현재 닉네임과 같습니다.");
                }
                if (accountService.existNickName(nickName)) {
                    return new ResponseResult(false, "사용 중인 닉네임입니다.");
                }
                return new ResponseResult(true, "사용 가능한 닉네임입니다.");
            }
        } catch (Exception e) {
            log.error("/account/checkNickName", e);
            return new ResponseResult(false, "중복확인에 실패했습니다.");
        }
    }

    @GetMapping("/find-id")
    public String findIdForm() {
        return "account/find_id";
    }

    @PostMapping("/find-id")
    public String findId(@RequestParam String name, @RequestParam String phone, Model model) {

        if (name.isBlank() || containsWhitespace(name) || phone.isBlank() || containsWhitespace(phone)) {
            model.addAttribute("message", "입력 값에 공백이 존재할 수 없습니다.");
            return "account/find_id";
        }
        if (5 < name.length()) {
            model.addAttribute("message", "이름은 5글자까지 가능합니다.");
            return "account/find_id";
        }
        if (!(phone.length() == 11)) {
            model.addAttribute("message", "전화번호 11글자만 가능합니다.");
            return "account/find_id";
        }
        try {
            model.addAttribute("foundId", accountService.findId(name, phone));
            model.addAttribute("searched", true);
            return "account/find_id";
        } catch (Exception e) {
            log.error("/account/findId", e);
            model.addAttribute("message", "아이디 찾기에 실패했습니다.");
            return "account/find_id";
        }
    }

    @GetMapping("/find-password")
    public String findPasswordForm() {
        return "account/find_password";
    }

    @PostMapping("/find-password")
    public String findPassword(@RequestParam String accountId, @RequestParam String name, @RequestParam String phone, Model model) {

        if (accountId.isBlank() || containsWhitespace(accountId) || name.isBlank() || containsWhitespace(name) || phone.isBlank() || containsWhitespace(phone)) {
            model.addAttribute("message", "입력 값에 공백이 존재할 수 없습니다.");
            return "account/find_password";
        }
        if (15 < accountId.length()) {
            model.addAttribute("message", "아이디는 15자까지 가능합니다.");
            return "account/find_password";
        }
        if (5 < name.length()) {
            model.addAttribute("message", "이름은 5글자까지 가능합니다.");
            return "account/find_password";
        }
        if (!(11 == phone.length())) {
            model.addAttribute("message", "전화번호 11글자만 가능합니다.");
            return "account/find_password";
        }
        try {
            model.addAttribute("tempPassword", accountService.resetPassword(accountId, name, phone));
            model.addAttribute("searched", true);
            return "account/find_password";
        } catch (Exception e) {
            log.error("/account/find-password", e);
            model.addAttribute("message", "비밀번호 찾기에 실패했습니다.");
            return "account/find_password";
        }
    }

    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal AccountUserDetails accountUserDetails, Model model, RedirectAttributes ra) {

        if (accountUserDetails == null) {
            return "redirect:/account/login";
        }
        try {
            AccountEntity accountEntity = accountService.getByAccountId(accountUserDetails.getUsername());

            Map<String, String> map = new HashMap<>();
            map.put("accountId", accountEntity.getAccountId());
            map.put("nickName", accountEntity.getNickName());
            map.put("name", accountEntity.getName());
            map.put("phone", accountEntity.getPhone());

            model.addAttribute("account", map);
            return "account/mypage";
        } catch (Exception e) {
            log.error("/account/myPage", e);
            ra.addFlashAttribute("message", "내 정보를 가져오지 못 했습니다.");
            return "redirect:/";
        }
    }

    @PostMapping("/mypage/update")
    public String updateMyInfo(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @RequestParam String field, @RequestParam String value,
                                @RequestParam(required = false) String currentPassword, @RequestParam(required = false) String newPasswordConfirm,
                               RedirectAttributes ra) {

        if (accountUserDetails == null) {
            return "redirect:/account/login";
        }
        if (field.isBlank() || containsWhitespace(field)) {
            ra.addFlashAttribute("message", "값을 확인해주세요.");
            return "redirect:/account/mypage";
        }
        if (!("password".equals(field) || "nickName".equals(field) || "name".equals(field) || "phone".equals(field))) {
            ra.addFlashAttribute("message", "값을 확인해주세요.");
            return "redirect:/account/mypage";
        }
        if (value.isBlank() || containsWhitespace(value)) {
            ra.addFlashAttribute("message", "입력 값에 공백이 존재할 수 없습니다.");
            return "redirect:/account/mypage";
        }

        switch (field) {
            case "password":
                try {
                    if (currentPassword == null || currentPassword.isBlank() || containsWhitespace(currentPassword)) {
                        ra.addFlashAttribute("message", "현재 비밀번호 입력 값에 공백이 존재합니다.");
                        break;
                    }
                    if (!accountService.verifyPassword(accountUserDetails.getUsername(), currentPassword)) {
                        ra.addFlashAttribute("message", "현재 비밀번호가 일치하지 않습니다.");
                        break;
                    }
                    if (!value.equals(newPasswordConfirm)) {
                        ra.addFlashAttribute("message", "새 비밀번호 입력 값이 일치하지 않습니다.");
                        break;
                    } else {
                        int passwordResult = accountService.updateInfo(accountUserDetails.getUsername(), field, value);
                        if (passwordResult == 1)
                            ra.addFlashAttribute("message", "비밀번호 수정완료");
                    }
                } catch (Exception e) {
                    log.error("/account/mypage/update password", e);
                    ra.addFlashAttribute("message", "비밀번호 변경에 실패했습니다.");
                }
                break;
            case "nickName":
                try {
                    if (accountUserDetails.getNickName().equals(value)) {
                        ra.addFlashAttribute("message", "현재 닉네임과 같습니다.");
                        break;
                    }
                    if (accountService.existNickName(value)) {
                        ra.addFlashAttribute("message", "이미 사용 중인 닉네임입니다.");
                        break;
                    } else {
                        int result = accountService.updateInfo(accountUserDetails.getUsername(), field, value);
                        if (result == 1) {
                            accountUserDetails.setNickName(value);
                            ra.addFlashAttribute("message", "닉네임 수정완료");
                        }
                    }
                } catch (Exception e) {
                    log.error("/account/mypage/update nickName", e);
                    ra.addFlashAttribute("message", "닉네임 변경에 실패했습니다.");
                }
                break;
            case "name":
                try {
                    if (5 < value.length()) {
                        ra.addFlashAttribute("message", "이름은 5글자까지 가능합니다.");
                        break;
                    }
                    if (accountUserDetails.getHsName().equals(aesUtil.createHashValue(value))) {
                        ra.addFlashAttribute("message", "현재 이름과 같습니다.");
                        break;
                    }
                    if (accountService.existName(value)) {
                        ra.addFlashAttribute("message", "가입되어 있는 이름입니다.");
                        break;
                    } else {
                        int result = accountService.updateInfo(accountUserDetails.getUsername(), field, value);
                        if (result == 1) {
                            accountUserDetails.setHsName(aesUtil.createHashValue(value));
                            ra.addFlashAttribute("message", "이름 수정완료");
                        }
                    }
                } catch (Exception e) {
                    log.error("/account/mypage/update name", e);
                    ra.addFlashAttribute("message", "이름 변경에 실패했습니다.");
                }
                break;
            case "phone":
                try {
                    if (!(11 == value.length())) {
                        ra.addFlashAttribute("message", "전화번호 11글자만 가능합니다.");
                        break;
                    }
                    if (accountUserDetails.getHsPhone().equals(aesUtil.createHashValue(value))) {
                        ra.addFlashAttribute("message", "현재 번호와 같습니다.");
                        break;
                    }
                    if (accountService.existPhone(value)) {
                        ra.addFlashAttribute("message", "가입되어 있는 번호입니다.");
                        break;
                    } else {
                        int result = accountService.updateInfo(accountUserDetails.getUsername(), field, value);
                        if (result == 1) {
                            accountUserDetails.setHsPhone(aesUtil.createHashValue(value));
                            ra.addFlashAttribute("message", "전화번호 수정완료");
                        }
                    }
                } catch (Exception e) {
                    log.error("/account/mypage/update phone", e);
                    ra.addFlashAttribute("message", "번호 변경에 실패했습니다.");
                }
                break;
        }
        return "redirect:/account/mypage";
    }

    /** 탈퇴 1단계 */
    @PostMapping("/withdraw/verify-password")
    @ResponseBody
    public ResponseResult verifyPassword(@AuthenticationPrincipal AccountUserDetails accountUserDetails, @RequestParam String password) {

        if (accountUserDetails == null) {
            return new ResponseResult(false, null);
        }
        if (accountService.verifyPassword(accountUserDetails.getUsername(), password)) {
            accountUserDetails.setWithdrawCheck(1);
            return new ResponseResult(true, null);
        }
        return new ResponseResult(false, null);
    }

    /** 탈퇴 2단계 */
    @PostMapping("/withdraw")
    public String withdraw(@AuthenticationPrincipal AccountUserDetails accountUserDetails, HttpServletRequest request, HttpServletResponse response,
                           RedirectAttributes ra) {

        if (accountUserDetails == null) {
            return "redirect:/account/login";
        }
        try {
            if (accountUserDetails.getWithdrawCheck() == 1) {
                accountService.withdraw(accountUserDetails.getUsername());

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null) {
                    new SecurityContextLogoutHandler().logout(request, response, authentication);
                }
                ra.addFlashAttribute("message", "탈퇴에 성공했습니다.");
            }
        } catch (Exception e) {
            log.error("/account/withdraw", e);
            ra.addFlashAttribute("message", "탈퇴에 실패했습니다.");
        }

        return "redirect:/";
    }

    @PostMapping("/withdraw/cancel")
    @ResponseBody
    public ResponseResult verifyCancel(@AuthenticationPrincipal AccountUserDetails accountUserDetails) {

        if (accountUserDetails == null) {
            return new ResponseResult(false, null);
        }
        if (accountUserDetails.getWithdrawCheck() == 1)
            accountUserDetails.setWithdrawCheck(0);

        return new ResponseResult(true, null);
    }

    private boolean containsWhitespace(String value) {
        return value != null && value.chars().anyMatch(Character::isWhitespace);
    }

    private boolean isDigitsOnly(String value) {
        return value != null && value.matches("\\d+");
    }
}