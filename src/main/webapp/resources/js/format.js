(function () {
    "use strict";

    function pad(n) {
        return n < 10 ? "0" + n : String(n);
    }

    function parseDateTime(value) {
        if (!value) {
            return null;
        }
        var date = new Date(value);
        return isNaN(date.getTime()) ? null : date;
    }

    function isToday(date) {
        var now = new Date();
        return date.getFullYear() === now.getFullYear()
            && date.getMonth() === now.getMonth()
            && date.getDate() === now.getDate();
    }

    function formatDateOnly(date) {
        return date.getFullYear() + "-" + pad(date.getMonth() + 1) + "-" + pad(date.getDate());
    }

    function formatTimeOnly(date) {
        return pad(date.getHours()) + ":" + pad(date.getMinutes());
    }

    /** 오늘 날짜면 HH:mm, 오늘이 아니면 yyyy-MM-dd, 값이 없으면 - */
    function formatTodayAware(value) {
        var date = parseDateTime(value);
        if (!date) {
            return "-";
        }
        return isToday(date) ? formatTimeOnly(date) : formatDateOnly(date);
    }

    function formatCreateTm(value) {
        return formatTodayAware(value);
    }

    function formatUpdateTm(value) {
        return formatTodayAware(value);
    }

    function applyFormattedDates() {
        document.querySelectorAll("[data-create-tm]").forEach(function (el) {
            el.textContent = formatCreateTm(el.getAttribute("data-create-tm"));
        });
        document.querySelectorAll("[data-update-tm]").forEach(function (el) {
            el.textContent = formatUpdateTm(el.getAttribute("data-update-tm"));
        });
    }

    document.addEventListener("DOMContentLoaded", applyFormattedDates);

})();