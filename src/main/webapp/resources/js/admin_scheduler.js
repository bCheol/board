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
        schedulerModal();
    });

    var ACTION_TEXT = {
        start: { title: "스케줄러 실행", confirm: "실행하시겠습니까?", submit: "실행" },
        stop: { title: "스케줄러 중단", confirm: "중단하시겠습니까?", submit: "중단" }
    };


    function schedulerModal() {
        var overlay = document.getElementById("scheduler-modal-overlay");
        var actionButtons = document.querySelectorAll(".btn-scheduler-action");
        if (!overlay || actionButtons.length === 0) {
            return;
        }

        var contextPath = overlay.dataset.contextPath || "";
        var form = document.getElementById("scheduler-form");
        var modalTitle = document.getElementById("scheduler-modal-title");
        var schedulerIdInput = document.getElementById("scheduler-id");
        var messageEl = document.getElementById("scheduler-message");
        var passwordInput = document.getElementById("scheduler-password");
        var errorEl = document.getElementById("scheduler-password-error");
        var cancelBtn = document.getElementById("scheduler-cancel-btn");
        var submitBtn = document.getElementById("scheduler-submit-btn");

        function showError(text) {
            errorEl.textContent = text;
        }

        function openModal(schedulerId, action) {
            var text = ACTION_TEXT[action];
            form.action = contextPath + "/admin/scheduler/" + action;
            modalTitle.textContent = text.title;
            submitBtn.textContent = text.submit;
            schedulerIdInput.value = schedulerId;
            messageEl.textContent = "\'" + schedulerId + "\' 번 스케줄러를 " + text.confirm;
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
                openModal(btn.dataset.schedulerId, btn.dataset.action);
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
                body: "num=" + encodeURIComponent(schedulerIdInput.value) + "&password=" + encodeURIComponent(password)
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