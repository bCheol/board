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
        initEditModal();
        initWithdraw();
    });

    /** 수정 모달 */
    function initEditModal() {
        var overlay = document.getElementById("edit-modal-overlay");
        var editButtons = document.querySelectorAll(".btn-inline-edit");
        if (!overlay || editButtons.length === 0) {
            return;
        }

        var cancelBtn = document.getElementById("edit-cancel-btn");
        var editForm = document.getElementById("edit-form");
        var fieldInput = document.getElementById("edit-field");
        var valueInput = document.getElementById("edit-value");
        var valueLabel = document.getElementById("edit-value-label");
        var modalTitle = document.getElementById("edit-modal-title");
        var valueErrorSpan = document.getElementById("edit-value-error");

        function showInfoText(text) {
            valueErrorSpan.textContent = text;
        }

        /** 비밀번호 */
        var valueRow = document.getElementById("edit-value-row");
        var passwordRows = document.getElementById("edit-password-rows");
        var currentPasswordInput = document.getElementById("edit-current-password");
        var newPasswordInput = document.getElementById("edit-new-password");
        var newPasswordConfirmInput = document.getElementById("edit-new-password-confirm");
        var passwordErrorSpan = document.getElementById("edit-password-error");

        function showPasswordError(text) {
            passwordErrorSpan.textContent = text;
        }

        /** 닉네임 */
        var nickCheckBtn = document.getElementById("edit-check-nickname-btn");
        var nickChecked = false;

        editButtons.forEach(function (btn) {
            btn.addEventListener("click", function () {
                var field = btn.dataset.field;
                var label = btn.dataset.label;
                var isPassword = field === "password";

                fieldInput.value = field;
                modalTitle.textContent = label + " 수정";
                valueRow.style.display = isPassword ? "none" : "";
                valueInput.required = !isPassword;
                if (!isPassword) {
                    valueLabel.textContent = label;
                    valueInput.type = btn.dataset.type || "text";
                    valueInput.maxLength = btn.dataset.maxlength ? Number(btn.dataset.maxlength) : 255;
                    valueInput.value = "";
                    valueInput.placeholder = btn.dataset.value || "";
                }

                passwordRows.style.display = isPassword ? "" : "none";
                currentPasswordInput.required = isPassword;
                newPasswordInput.required = isPassword;
                newPasswordConfirmInput.required = isPassword;
                if (isPassword) {
                    currentPasswordInput.value = "";
                    newPasswordInput.value = "";
                    newPasswordConfirmInput.value = "";
                    newPasswordInput.maxLength = btn.dataset.maxlength ? Number(btn.dataset.maxlength) : 255;
                    newPasswordConfirmInput.maxLength = newPasswordInput.maxLength;
                    showPasswordError("");
                }
                nickChecked = false;
                showInfoText("");
                nickCheckBtn.style.display = field === "nickName" ? "" : "none";

                overlay.classList.add("active");
                (isPassword ? currentPasswordInput : valueInput).focus();
            });
        });

        if (cancelBtn) {
            cancelBtn.addEventListener("click", function () {
                overlay.classList.remove("active");
            });
        }

        valueInput.addEventListener("input", function () {
            showInfoText("");
            if (fieldInput.value === "nickName") {
                nickChecked = false;
            }
        });

        nickCheckBtn.addEventListener("click", function () {
            var rawNickName = valueInput.value;
            if (!rawNickName) {
                showInfoText("닉네임을 입력하세요.")
                return;
            }
            if (/\s/.test(rawNickName)) {
                showInfoText("닉네임에 공백이 존재할 수 없습니다.")
                return;
            }
            fetch("/account/check-nickname?nickName=" + encodeURIComponent(rawNickName))
                .then(function (res) { return res.json(); })
                .then(function (data) {
                    if (data.result)
                        nickChecked = !!data.result;
                    showInfoText(data.message, nickChecked);
                })
                .catch(function () {
                    nickChecked = false;
                    showInfoText("다시 시도해 주세요.");
                });
        });

        editForm.addEventListener("submit", function (e) {
            var field = fieldInput.value;

            if (field === "password") {
                var currentPassword = currentPasswordInput.value;
                var newPassword = newPasswordInput.value;
                var newPasswordConfirm = newPasswordConfirmInput.value;

                if (!currentPassword) {
                    showPasswordError("현재 비밀번호를 입력하세요.");
                    e.preventDefault();
                    return;
                }
                if (!newPassword) {
                    showPasswordError("새 비밀번호를 입력하세요.");
                    e.preventDefault();
                    return;
                }
                if (/\s/.test(currentPassword) || /\s/.test(newPassword)) {
                    showPasswordError("비밀번호에 공백이 존재할 수 없습니다.");
                    e.preventDefault();
                    return;
                }
                if (newPassword !== newPasswordConfirm) {
                    showPasswordError("새 비밀번호 확인이 일치하지 않습니다.");
                    e.preventDefault();
                    return;
                }
                valueInput.value = newPassword;
                return;
            }

            var value = valueInput.value;
            showInfoText("");

            var spaceCheckLabels = { name: "이름", phone: "전화번호" };

            if (spaceCheckLabels[field] && /\s/.test(value)) {
                showInfoText(spaceCheckLabels[field] + "에 공백이 존재할 수 없습니다.");
                e.preventDefault();
                return;
            }

            if (field === "nickName" && !nickChecked) {
                showInfoText("닉네임 중복확인을 해주세요.");
                e.preventDefault();
                return;
            }

            if (field === "phone" && !/^\d+$/.test(value)) {
                showInfoText("전화번호는 숫자로만 입력하세요.");
                e.preventDefault();
                return;
            }

            if (field === "phone" && !(value.length == 11)) {
                showInfoText("전화번호 입력정보를 확인해주세요.");
                e.preventDefault();
                return;
            }

        });

    }

    /** 회원 탈퇴 */
    function initWithdraw() {
        var openBtn = document.getElementById("withdraw-open-btn");
        var overlay = document.getElementById("withdraw-modal-overlay");
        if (!openBtn || !overlay) {
            return;
        }
        var withdrawForm = document.getElementById("withdraw-form");
        var stepPassword = document.getElementById("withdraw-step-password");
        var stepConfirm = document.getElementById("withdraw-step-confirm");
        var passwordInput = document.getElementById("withdraw-password");
        var errorEl = document.getElementById("withdraw-password-error");

        var verifyBtn = document.getElementById("withdraw-verify-btn");
        var cancelBtn1 = document.getElementById("withdraw-cancel-btn-1");

        var finalBtn = document.getElementById("withdraw-final-btn");
        var cancelBtn2 = document.getElementById("withdraw-cancel-btn-2");


        function showError(text) {
            errorEl.textContent = text;
            errorEl.style.display = "";
        }

        function resetModal() {
            stepPassword.style.display = "";
            stepConfirm.style.display = "none";
            passwordInput.value = "";
            errorEl.style.display = "none";
            errorEl.textContent = "";
        }

        function closeModal() {
            overlay.classList.remove("active");
            resetModal();
        }

        openBtn.addEventListener("click", function () {
            resetModal();
            overlay.classList.add("active");
            passwordInput.focus();
        });

        verifyBtn.addEventListener("click", function () {
            var password = passwordInput.value;
            if (!password) {
                showError("비밀번호를 입력하세요.");
                return;
            }
            fetch("/account/withdraw/verify-password", {
                method: "POST",
                headers: Object.assign(
                    { "Content-Type": "application/x-www-form-urlencoded" },
                    csrfHeader()
                ),
                body: "password=" + encodeURIComponent(password)
                })
                .then(function (res) { return res.json(); })
                .then(function (data) {
                    if (data.result) {
                        stepPassword.style.display = "none";
                        stepConfirm.style.display = "";
                    } else {
                        showError("비밀번호가 일치하지 않습니다.");
                    }
                })
                .catch(function () {
                    showError("다시 시도해 주세요.");
                });
        });

        cancelBtn1.addEventListener("click", closeModal);

        /** 탈퇴 2 */
        finalBtn.addEventListener("click", function () {
            withdrawForm.submit();
        });

        cancelBtn2.addEventListener("click", function () {
           fetch("/account/withdraw/cancel", {
               method: "POST",
               headers: Object.assign(
                   { "Content-Type": "application/x-www-form-urlencoded" },
                   csrfHeader()
               )
               })
               .then(function (res) { return res.json(); })
               .then(function (data) {
                   if (data.result) {
                       closeModal();
                   }
               })
               .catch(function () {
                   showError("다시 시도해 주세요.");
               });
       });
    }
})();