"use strict"

/**
 * 期間指定ボックスを変更する
 * @param {*} str 分岐用文字列
 * @returns 開始日と終了日を変更する
 */
function execSpecifyPeriod(str, startId, endId) {
    const date = new Date();

    const startdate = document.getElementById(startId);
    if (startdate == null) return;
    const start = new Date(startdate.value);

    const enddate = document.getElementById(endId);
    if (enddate == null) return;
    const end = new Date(enddate.value);

    switch (str) {
        case "last-month":
            startdate.value = new Date(date.getFullYear(), date.getMonth() - 1, 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(date.getFullYear(), date.getMonth(), 0).toLocaleDateString('sv-SE');
            break;
        case "this-month":
            startdate.value = new Date(date.getFullYear(), date.getMonth(), 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(date.getFullYear(), date.getMonth() + 1, 0).toLocaleDateString('sv-SE');
            break;
        case "prev-day":
            startdate.value = new Date(start.getFullYear(), start.getMonth(), start.getDate() - 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(end.getFullYear(), end.getMonth(), end.getDate() - 1).toLocaleDateString('sv-SE');
            break;
        case "next-day":
            startdate.value = new Date(start.getFullYear(), start.getMonth(), start.getDate() + 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(end.getFullYear(), end.getMonth(), end.getDate() + 1).toLocaleDateString('sv-SE');
            break;
        case "yesterday":
            startdate.value = new Date(date.getFullYear(), date.getMonth(), date.getDate() - 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(date.getFullYear(), date.getMonth(), date.getDate() - 1).toLocaleDateString('sv-SE');
            break;
        case "prev-week":
            startdate.value = new Date(start.getFullYear(), start.getMonth(), start.getDate() - 7).toLocaleDateString('sv-SE');
            enddate.value = new Date(start.getFullYear(), start.getMonth(), start.getDate() - 1).toLocaleDateString('sv-SE');
            break;
        case "next-week":
            startdate.value = new Date(start.getFullYear(), start.getMonth(), start.getDate() + 7).toLocaleDateString('sv-SE');
            enddate.value = new Date(start.getFullYear(), start.getMonth(), start.getDate() + 13).toLocaleDateString('sv-SE');
            break;
        case "this-week":
            startdate.value = new Date(date.getFullYear(), date.getMonth(), date.getDate()).toLocaleDateString('sv-SE');
            enddate.value = new Date(date.getFullYear(), date.getMonth(), date.getDate() + 6).toLocaleDateString('sv-SE');
            break;
        case "today":
            startdate.value = date.toLocaleDateString('sv-SE');
            enddate.value = date.toLocaleDateString('sv-SE');
            break;
        case "prev-month":
            startdate.value = new Date(start.getFullYear(), start.getMonth() - 1, 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(start.getFullYear(), start.getMonth(), 0).toLocaleDateString('sv-SE');
            break;
        case "next-month":
            startdate.value = new Date(end.getFullYear(), end.getMonth() + 1, 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(end.getFullYear(), end.getMonth() + 2, 0).toLocaleDateString('sv-SE');
            break;
        case "half-month":
            startdate.value = new Date(date.getFullYear(), date.getMonth() - 1, 16).toLocaleDateString('sv-SE');
            enddate.value = new Date(date.getFullYear(), date.getMonth(), 15).toLocaleDateString('sv-SE');
            break;
        case "end-month":
            startdate.value = new Date(date.getFullYear(), date.getMonth() - 1, 1).toLocaleDateString('sv-SE');
            enddate.value = new Date(date.getFullYear(), date.getMonth(), 0).toLocaleDateString('sv-SE');
            break;
        case "prev-month-move":
            startdate.value = new Date(start.getFullYear(), start.getMonth() - 1, 16).toLocaleDateString('sv-SE');
            enddate.value = new Date(start.getFullYear(), start.getMonth(), 15).toLocaleDateString('sv-SE');
            break;
        case "next-month-move":
            startdate.value = new Date(start.getFullYear(), start.getMonth() + 1, 16).toLocaleDateString('sv-SE');
            enddate.value = new Date(start.getFullYear(), start.getMonth() + 2, 15).toLocaleDateString('sv-SE');
            break;
        default:
            break;
    }
}