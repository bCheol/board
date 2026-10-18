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
        initProcessModal();
        initReasonModal();
        initBoardModal();
    });

    /** 게시글 자세히 모달 */
    function initBoardModal() {
        var overlay = document.getElementById("report-board-modal-overlay");
        var triggers = document.querySelectorAll(".btn-board-detail");
        if (!overlay || triggers.length === 0) {
            return;
        }

        var titleEl = document.getElementById("report-board-title");
        var writerEl = document.getElementById("report-board-writer");
        var bodyEl = document.getElementById("report-board-body");
        var closeBtn = document.getElementById("report-board-close-btn");

        triggers.forEach(function (btn) {
            btn.addEventListener("click", function () {
                titleEl.textContent = btn.dataset.boardTitle || "";
                writerEl.textContent = btn.dataset.boardWriter || "";
                bodyEl.textContent = btn.dataset.boardBody || "";
                overlay.classList.add("active");
            });
        });

        closeBtn.addEventListener("click", function () {
            overlay.classList.remove("active");
        });
    }

    /** 신고 이유 모달 */
    function initReasonModal() {
        var overlay = document.getElementById("report-reason-modal-overlay");
        var reasonCells = document.querySelectorAll(".reason-cell");
        if (!overlay || reasonCells.length === 0) {
            return;
        }

        var body = document.getElementById("report-reason-full");
        var closeBtn = document.getElementById("report-reason-close-btn");

        reasonCells.forEach(function (cell) {
            if (cell.scrollWidth <= cell.clientWidth) {
                return;
            }
            cell.classList.add("reason-clip");
            cell.addEventListener("click", function () {
                body.textContent = cell.textContent;
                overlay.classList.add("active");
            });
        });

        closeBtn.addEventListener("click", function () {
            overlay.classList.remove("active");
        });
    }

    /** 신고 처리 모달 */
    function initProcessModal() {
        var overlay = document.getElementById("report-process-modal-overlay");
        var actionButtons = document.querySelectorAll(".btn-report-process");
        if (!overlay || actionButtons.length === 0) {
            return;
        }

        var contextPath = overlay.dataset.contextPath || "";
        var form = document.getElementById("report-process-form");
        var targetEl = document.getElementById("report-process-target");
        var resultSelect = document.getElementById("report-process-result");
        var errorEl = document.getElementById("report-process-error");
        var cancelBtn = document.getElementById("report-process-cancel-btn");

        function showError(text) {
            errorEl.textContent = text;
        }

        function openModal(num, title, currentResult) {
            form.action = contextPath + "/admin/reports/" + num + "/process";
            targetEl.textContent = num + "번 게시글 신고 처리 하기";
            resultSelect.value = currentResult || "";
            showError("");
            overlay.classList.add("active");
            resultSelect.focus();
        }

        function closeModal() {
            overlay.classList.remove("active");
        }

        actionButtons.forEach(function (btn) {
            btn.addEventListener("click", function () {
                openModal(btn.dataset.num, btn.dataset.title || "", btn.dataset.result || "");
            });
        });

        cancelBtn.addEventListener("click", closeModal);

        form.addEventListener("submit", function (e) {
            e.preventDefault();

            var result = resultSelect.value;
            if (!result.trim()) {
                showError("처리 결과를 선택하세요.");
                return;
            }

            fetch(form.action, {
                method: "POST",
                headers: Object.assign(
                    { "Content-Type": "application/x-www-form-urlencoded" },
                    csrfHeader()
                ),
                body: "result=" + encodeURIComponent(result)
            })
                .then(function (res) { return res.json(); })
                .then(function (data) {
                    if (data.result) {
                        alert(data.message);
                        closeModal();
                        window.location.reload();
                    } else {
                        showError(data.message);
                    }
                })
                .catch(function () {
                    showError("처리 중 오류가 발생했습니다.");
                });
        });
    }
})();