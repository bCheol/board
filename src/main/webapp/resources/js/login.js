(function () {
    "use strict";

    document.addEventListener("DOMContentLoaded", function () {
        initLoginForm();
        clearIdOnLoginError();
    });

    function clearIdOnLoginError() {
        var errorBox = document.querySelector(".auth-box .alert-error");
        var accountIdInput = document.getElementById("accountId");
        if (!errorBox || !accountIdInput) {
            return;
        }
        accountIdInput.value = "";
        var passwordInput = document.getElementById("password");
        if (passwordInput) {
            passwordInput.value = "";
        }
        accountIdInput.focus();
    }

    function initLoginForm() {
        var loginForm = document.getElementById("login-form");
        var errorSpan = document.getElementById("login-error");
        if (!loginForm || !errorSpan) {
            return;
        }

        function showError(text) {
            errorSpan.textContent = text;
        }

        loginForm.addEventListener("submit", function (e) {
            var accountId = loginForm.accountId.value;
            var password = loginForm.password.value;

            if (!accountId) {
                showError("아이디를 입력하세요.");
                e.preventDefault();
                return;
            }
            if (/\s/.test(accountId)) {
                showError("아이디에 공백이 존재할 수 없습니다.");
                e.preventDefault();
                return;
            }
            if (!password) {
                showError("비밀번호를 입력하세요.");
                e.preventDefault();
                return;
            }
            if (/\s/.test(password)) {
                showError("비밀번호에 공백이 존재할 수 없습니다.");
                e.preventDefault();
                return;
            }
            showError("");
        });
    }
})();