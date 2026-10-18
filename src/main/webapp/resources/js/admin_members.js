(function () {
    "use strict";

    function csrfHeader() {
        var token = document.querySelector('meta[name="_csrf"]').content;
        var headerName = document.querySelector('meta[name="_csrf_header"]').content;
        var headers = {};
        headers[headerName] = token;
        return headers;
    }

    document.addEventListener("DOMContentLoaded", function () {
        initBlockModal();
        initRoleModal();
    });

    var ACTION_TEXT = {
        block: { title: "회원 차단", confirm: "회원을 차단하시겠습니까?", submit: "차단" },
        unblock: { title: "회원 차단 해제", confirm: "회원의 차단을 해제하시겠습니까?", submit: "해제" }
    };

    /** 회원 차단/해제 모달 */
    function initBlockModal() {
        var overlay = document.getElementById("block-modal-overlay");
        var actionButtons = document.querySelectorAll(".btn-block-action");
        if (!overlay || actionButtons.length === 0) {
            return;
        }

        var contextPath = overlay.dataset.contextPath || "";
        var form = document.getElementById("block-form");
        var modalTitle = document.getElementById("block-modal-title");
        var accountIdInput = document.getElementById("block-account-id");
        var messageEl = document.getElementById("block-message");
        var passwordInput = document.getElementById("block-password");
        var errorEl = document.getElementById("block-password-error");
        var cancelBtn = document.getElementById("block-cancel-btn");
        var submitBtn = document.getElementById("block-submit-btn");

        function showError(text) {
            errorEl.textContent = text;
        }

        function openModal(accountId, action) {
            var text = ACTION_TEXT[action];
            form.action = contextPath + "/admin/members/" + action;
            modalTitle.textContent = text.title;
            submitBtn.textContent = text.submit;
            accountIdInput.value = accountId;
            messageEl.textContent = "\'" + accountId + "\' " + text.confirm;
            passwordInput.value = "";
            showError("");
            overlay.classList.add("active");
            passwordInput.focus();
        }

        function closeModal() {
            overlay.classList.remove("active");
        }

        actionButtons.forEach(function (btn) {
            btn.addEventListener("click", function () {
                openModal(btn.dataset.accountId, btn.dataset.action);
            });
        });

        cancelBtn.addEventListener("click", closeModal);

        form.addEventListener("submit", function (e) {
            e.preventDefault();

            var password = passwordInput.value;
            if (!password) {
                showError("비밀번호를 입력하세요.");
                return;
            }
            if (/\s/.test(password)) {
                showError("비밀번호에 공백이 존재할 수 없습니다.");
                return;
            }

            fetch(form.action, {
                method: "POST",
                headers: Object.assign(
                    { "Content-Type": "application/x-www-form-urlencoded" },
                    csrfHeader()
                ),
                body: "accountId=" + encodeURIComponent(accountIdInput.value) +
                    "&password=" + encodeURIComponent(password)
            })
                .then(function (res) { return res.json(); })
                .then(function (data) {
                    if (data.result) {
                        alert(data.message);
                        closeModal();
                        window.location.reload();
                    } else {
                        passwordInput.value = "";
                        showError(data.message);
                        passwordInput.focus();
                    }
                })
                .catch(function () {
                    alert("처리 중 오류가 발생했습니다.");
                });
        });
    }

    /** 권한 변경 모달 */
    function initRoleModal() {
        var overlay = document.getElementById("role-modal-overlay");
        var actionButtons = document.querySelectorAll(".btn-role-action");
        if (!overlay || actionButtons.length === 0) {
            return;
        }

        var contextPath = overlay.dataset.contextPath || "";
        var form = document.getElementById("role-form");
        var accountIdInput = document.getElementById("role-account-id");
        var messageEl = document.getElementById("role-message");
        var roleSelect = document.getElementById("role-select");
        var passwordInput = document.getElementById("role-password");
        var errorEl = document.getElementById("role-password-error");
        var cancelBtn = document.getElementById("role-cancel-btn");

        function showError(text) {
            errorEl.textContent = text;
        }

        function openModal(accountId, currentRole) {
            form.action = contextPath + "/admin/members/role";
            accountIdInput.value = accountId;
            messageEl.textContent = "\'" + accountId + "\' 회원의 권한을 변경합니다.";
            roleSelect.value = currentRole || "USER";
            passwordInput.value = "";
            showError("");
            overlay.classList.add("active");
            roleSelect.focus();
        }

        function closeModal() {
            overlay.classList.remove("active");
        }

        actionButtons.forEach(function (btn) {
            btn.addEventListener("click", function () {
                openModal(btn.dataset.accountId, btn.dataset.currentRole);
            });
        });

        cancelBtn.addEventListener("click", closeModal);

        form.addEventListener("submit", function (e) {
            e.preventDefault();

            var password = passwordInput.value;
            if (!password) {
                showError("비밀번호를 입력하세요.");
                return;
            }
            if (/\s/.test(password)) {
                showError("비밀번호에 공백이 존재할 수 없습니다.");
                return;
            }

            fetch(form.action, {
                method: "POST",
                headers: Object.assign(
                    { "Content-Type": "application/x-www-form-urlencoded" },
                    csrfHeader()
                ),
                body: "accountId=" + encodeURIComponent(accountIdInput.value) +
                    "&roleType=" + encodeURIComponent(roleSelect.value) +
                    "&password=" + encodeURIComponent(password)
                })
                .then(function (res) { return res.json(); })
                .then(function (data) {
                    if (data.result) {
                        alert(data.message);
                        closeModal();
                        window.location.reload();
                    } else {
                        passwordInput.value = "";
                        showError(data.message);
                        passwordInput.focus();
                    }
                })
                .catch(function () {
                    alert("처리 중 오류가 발생했습니다.");
                });
        });
    }
})();