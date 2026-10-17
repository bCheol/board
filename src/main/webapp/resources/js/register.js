(function () {
    "use strict";

    document.addEventListener("DOMContentLoaded", function () {
        initRegisterForm();
    });

    function initRegisterForm() {
        var checkBtn = document.getElementById("check-id-btn");
        var registerForm = document.getElementById("register-form");
        if (!checkBtn || !registerForm) {
            return;
        }

        var resultSpan = document.getElementById("check-id-result");
        var accountIdInput = document.getElementById("accountId");
        var idChecked = false;

        var nickCheckBtn = document.getElementById("check-nickname-btn");
        var nickResultSpan = document.getElementById("check-nickname-result");
        var nickNameInput = document.getElementById("nickName");
        var nickChecked = false;

        checkBtn.addEventListener("click", function () {
            var accountIdValue = accountIdInput.value;
            if (!accountIdValue) {
                showHint(resultSpan, "아이디를 입력하세요.", false);
                return;
            }
            if (/\s/.test(accountIdValue)) {
                showHint(resultSpan, "아이디에 공백이 존재할 수 없습니다.", false);
                return;
            }
            fetch("/account/check-id?accountId=" + encodeURIComponent(accountIdValue))
                .then(function (res) { return res.json(); })
                .then(function (data) {
                    if (data.result)
                        idChecked = !!data.result;
                    showHint(resultSpan, data.message, idChecked);
//                    if (!data.result) {
//                        showHint(resultSpan, data.message != null ? data.message : "이미 사용 중인 아이디입니다.", idChecked);
//                    } else {
//                        idChecked = !!data.result;
//                        showHint(resultSpan, "사용 가능한 아이디입니다.", idChecked);
//                    }
                })
                .catch(function () {
                    idChecked = false;
                    showHint(resultSpan, "확인 중 오류가 발생했습니다.", false);
                });
        });

        accountIdInput.addEventListener("input", function () {
            idChecked = false;
            resultSpan.textContent = "";
        });

        nickCheckBtn.addEventListener("click", function () {
            var nickNameValue = nickNameInput.value;
            if (!nickNameValue) {
                showHint(nickResultSpan, "닉네임을 입력하세요.", false);
                return;
            }
            if (/\s/.test(nickNameValue)) {
                showHint(nickResultSpan, "닉네임에 공백이 존재할 수 없습니다.", false);
                return;
            }
            fetch("/account/check-nickname?nickName=" + encodeURIComponent(nickNameValue))
                .then(function (res) { return res.json(); })
                .then(function (data) {
                    if (data.result)
                        nickChecked = !!data.result;
                    showHint(nickResultSpan, data.message, nickChecked);
                })
                .catch(function () {
                    nickChecked = false;
                    showHint(nickResultSpan, "확인 중 오류가 발생했습니다.", false);
                });
        });

        nickNameInput.addEventListener("input", function () {
            nickChecked = false;
            nickResultSpan.textContent = "";
        });

        var registerErrorSpan = document.getElementById("register-error");

        registerForm.addEventListener("submit", function (e) {
            var accountId = registerForm.accountId.value;
            var nickName = registerForm.nickName.value;
            var password = registerForm.password.value;
            var passwordConfirm = registerForm.passwordConfirm.value;
            var name = registerForm.name.value;
            var phone = registerForm.phone.value;

            if (!accountId) {
                registerErrorSpan.textContent = "아이디를 입력하세요.";
                e.preventDefault();
                return;
            }
            if (!nickName) {
                registerErrorSpan.textContent = "닉네임을 입력하세요.";
                e.preventDefault();
                return;
            }
            if (!password) {
                registerErrorSpan.textContent = "비밀번호를 입력하세요.";
                e.preventDefault();
                return;
            }
            if (/\s/.test(password)) {
                registerErrorSpan.textContent = "비밀번호에 공백이 존재할 수 없습니다.";
                e.preventDefault();
                return;
            }
            if (!passwordConfirm) {
                registerErrorSpan.textContent = "비밀번호 확인을 입력하세요.";
                e.preventDefault();
                return;
            }
            if (!name) {
                registerErrorSpan.textContent = "이름을 입력하세요.";
                e.preventDefault();
                return;
            }
            if (/\s/.test(name)) {
                registerErrorSpan.textContent = "이름에 공백이 존재할 수 없습니다.";
                e.preventDefault();
                return;
            }
            if (!phone) {
                registerErrorSpan.textContent = "전화번호를 입력하세요.";
                e.preventDefault();
                return;
            }
            if (/\s/.test(phone)) {
                registerErrorSpan.textContent = "전화번호에 공백이 존재할 수 없습니다.";
                e.preventDefault();
                return;
            }
            if (!/^\d+$/.test(phone)) {
                registerErrorSpan.textContent = "전화번호는 숫자로만 입력하세요.";
                e.preventDefault();
                return;
            }
            if (!(phone.length == 11)) {
                registerErrorSpan.textContent = "전화번호 입력정보를 확인해주세요.";
                e.preventDefault();
                return;
            }
            if (password !== passwordConfirm) {
                registerErrorSpan.textContent = "비밀번호가 일치하지 않습니다.";
                e.preventDefault();
                return;
            }
            if (!idChecked) {
                registerErrorSpan.textContent = "아이디 중복확인을 해주세요.";
                e.preventDefault();
                return;
            }
            if (!nickChecked) {
                registerErrorSpan.textContent = "닉네임 중복확인을 해주세요.";
                e.preventDefault();
                return;
            }
            registerErrorSpan.textContent = "";
        });

        function showHint(el, text, ok) {
            el.textContent = text;
            el.className = "field-hint " + (ok ? "field-hint-ok" : "field-hint-error");
        }
    }
})();