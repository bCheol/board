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
        initDeleteConfirm();
        initReportModal();
    });

    function initDeleteConfirm() {
        var deleteForm = document.getElementById("delete-form");
        if (deleteForm) {
            deleteForm.addEventListener("submit", function (e) {
                if (!confirm("정말 삭제하시겠습니까?")) {
                    e.preventDefault();
                }
            });
        }
    }

    function initReportModal() {
        var openBtn = document.getElementById("report-open-btn");
        var overlay = document.getElementById("report-modal-overlay");
        if (!openBtn || !overlay) {
            return;
        }

        var reasonInput = document.getElementById("report-reason");
        var errorEl = document.getElementById("report-error");
        var cancelBtn = document.getElementById("report-cancel-btn");
        var submitBtn = document.getElementById("report-submit-btn");
        var boardNum = openBtn.dataset.boardNum;


        function closeModal() {
            overlay.classList.remove("active");
            reasonInput.value = "";
            errorEl.textContent = "";
        }

        openBtn.addEventListener("click", function () {
            reasonInput.value = "";
            errorEl.textContent = "";
            overlay.classList.add("active");
            reasonInput.focus();
        });

        cancelBtn.addEventListener("click", closeModal);

        submitBtn.addEventListener("click", function () {
            var reason = reasonInput.value;
            if (!reason.trim()) {
                errorEl.textContent = "신고 이유를 입력하세요.";
                reasonInput.focus();
                return;
            }

            submitBtn.disabled = true;
            fetch("/board/report", {
                method: "POST",
                headers: Object.assign(
                    { "Content-Type": "application/x-www-form-urlencoded" },
                    csrfHeader()
                ),
                body: "boardNum=" + encodeURIComponent(boardNum) +
                      "&reason=" + encodeURIComponent(reason)
            })
                .then(function (res) { return res.json(); })
                .then(function (data) {
                    if (data.result) {
                        alert("신고가 접수되었습니다.");
                        closeModal();
                    } else {
                        errorEl.textContent = data.message || "신고 처리 중 오류가 발생했습니다.";
                    }
                })
                .catch(function () {
                    errorEl.textContent = "신고 처리 중 오류가 발생했습니다.";
                })
                .then(function () {
                    submitBtn.disabled = false;
                });
        });
    }
})();
