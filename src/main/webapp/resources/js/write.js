(function () {
    "use strict";

    var TITLE_MAX = 35;
    var BODY_MAX = 100;

    function validateBoardForm(form) {
        var title = form.title.value.trim();
        var body = form.body.value.trim();

        if (!title) {
            alert("제목을 입력하세요.");
            form.title.focus();
            return false;
        }
        if (title.length > TITLE_MAX) {
            alert("제목은 " + TITLE_MAX + "자 이내로 입력하세요.");
            form.title.focus();
            return false;
        }
        if (!body) {
            alert("내용을 입력하세요.");
            form.body.focus();
            return false;
        }
        if (body.length > BODY_MAX) {
            alert("내용은 " + BODY_MAX + "자 이내로 입력하세요.");
            form.body.focus();
            return false;
        }

        return true;
    }

    document.addEventListener("DOMContentLoaded", function () {
        var boardForm = document.getElementById("board-form");
        if (boardForm) {
            boardForm.addEventListener("submit", function (e) {
                if (!validateBoardForm(boardForm)) {
                    e.preventDefault();
                }
            });
        }
    });
})();