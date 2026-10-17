(function () {
    "use strict";

    document.addEventListener("DOMContentLoaded", function () {
        initFindIdForm();
    });

    function initFindIdForm() {
        var form = document.getElementById("find-id-form");
        var errorSpan = document.getElementById("find-id-error");
        if (!form || !errorSpan) {
            return;
        }

        form.addEventListener("submit", function (e) {
            var name = form.name.value;
            var phone = form.phone.value;

            if (!name) {
                errorSpan.textContent = "이름을 입력하세요.";
                e.preventDefault();
                return;
            }
            if (/\s/.test(name)) {
                errorSpan.textContent = "이름에 공백이 존재할 수 없습니다.";
                e.preventDefault();
                return;
            }
            if (!phone) {
                errorSpan.textContent = "전화번호를 입력하세요.";
                e.preventDefault();
                return;
            }
            if (/\s/.test(phone)) {
                errorSpan.textContent = "전화번호에 공백이 존재할 수 없습니다.";
                e.preventDefault();
                return;
            }
            if (!/^\d+$/.test(phone)) {
                errorSpan.textContent = "전화번호는 숫자로만 입력하세요.";
                e.preventDefault();
                return;
            }
            if (!(phone.length == 11)) {
                errorSpan.textContent = "전화번호 입력정보를 확인해주세요.";
                e.preventDefault();
                return;
            }
            errorSpan.textContent = "";
        });
    }
})();