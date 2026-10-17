let isWorking = false;
window.addEventListener('beforeunload', (event) => {
    if (isWorking)
        event.preventDefault();
});
window.addEventListener('pagehide', () => {
    if (isWorking)
        stopRequest(4);
});

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
        manageModal();
    });

    function manageModal() {
        var overlay = document.getElementById("manage-modal-overlay");
        var actionButtons = document.querySelectorAll(".btn-manage-action");
        if (!overlay || actionButtons.length === 0) {
            return;
        }

        var contextPath = overlay.dataset.contextPath || "";
        var form = document.getElementById("manage-form");
        var modalTitle = document.getElementById("manage-modal-title");
        var manageTypeInput = document.getElementById("manage-type");
        var messageEl = document.getElementById("manage-message");
        var passwordInput = document.getElementById("manage-password");
        var infoEl = document.getElementById("manage-info");
        var errorEl = document.getElementById("manage-password-error");
        var cancelBtn = document.getElementById("manage-cancel-btn");
        var submitBtn = document.getElementById("manage-submit-btn");

        var divEncryptKey = document.getElementById("div-encryptKey");
        var encryptKeyInput = document.getElementById("manage-encryptKey");

        function showInfo(text) {
            infoEl.textContent = text;
        }

        function showError(text) {
            errorEl.textContent = text;
        }

        function openModal(type, action) {
            form.action = contextPath + "/admin/setting";
            manageTypeInput.value = type;
            messageEl.textContent = action;
            passwordInput.value = "";
            encryptKeyInput.value = "";
            showInfo("");
            showError("");
            overlay.classList.add("active");
            passwordInput.focus();

            if ("temp_change"== type) {
                divEncryptKey.style.display = "block";
            } else {
                divEncryptKey.style.display = "none";
            }
        }

        function closeModal() {
            overlay.classList.remove("active");
        }

        let controller = null;

        actionButtons.forEach(function (btn) {
            btn.addEventListener("click", function () {
                openModal(btn.dataset.type, btn.dataset.action);
            });
        });

        cancelBtn.addEventListener("click", function () {
            if (controller != null) {
                if ("copy" == manageTypeInput.value) {
                    stopRequest(0);
                } else if ("temp_change" == manageTypeInput.value) {
                    stopRequest(1);
                } else if ("org_change" == manageTypeInput.value) {
                    stopRequest(2);
                } else if ("delete_temp" == manageTypeInput.value) {
                    stopRequest(3);
                }
                controller?.abort();
            }
            closeModal();
        });

        form.addEventListener("submit", async (e) => {
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

            if ("temp_change" == manageTypeInput.value) {
                var encryptKey = encryptKeyInput.value;
                if (!encryptKey) {
                    showError("암호키를 입력하세요.");
                    return;
                }
                if (/\s/.test(encryptKey)) {
                    showError("암호키에 공백이 존재할 수 없습니다.");
                    return;
                }
            }
            var type = manageTypeInput.value;

            controller = new AbortController();

            const submitBtn = form.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            isWorking = true;
            showInfo("작업 진행 중 입니다.");
            showError("");

            try {
                const res = await fetch(form.action, {
                    method: "POST",
                    headers: Object.assign(
                        { "Content-Type": "application/x-www-form-urlencoded" },
                        csrfHeader()
                    ),
                    body: "type=" + encodeURIComponent(type) + "&password=" + encodeURIComponent(password) + "&encryptKey=" + encodeURIComponent(encryptKey),
                    signal: controller.signal
                });
                if (!res.ok) {
                    isWorking = false;
                    throw new Error();
                }

                const data = await res.json();
                isWorking = false;
                closeModal();
                alert(data.message)

            } catch (e) {
                showError("요청에 실패했습니다.");
            } finally {
                submitBtn.disabled = false;
                controller = null;
            }
        });
    }
})();

function stopRequest(num) {
    fetch("/admin/setting/stop/" + num, {
        method: "POST"
    })
    .catch(function () {
        alert("처리 중 오류가 발생했습니다.");
    });
}