"use strict"

export function handleDateMove(el){
console.log("date-move", new Error().stack);
    const type = el.dataset.type;
    const startId = el.dataset.start;
    const endId = el.dataset.end;

    const startEl = document.getElementById(startId);
    const endEl   = document.getElementById(endId);
    if(!startEl || !endEl) return;

    const now = new Date();
    const today = new Date(
        now.getFullYear(),
        now.getMonth(),
        now.getDate()
    );

    const start = getBaseDate(startEl);
    const end   = getBaseDate(endEl);

    let from, to;

    switch(type){
        case "prev-day":
            from = addDays(start, -1);
            to   = addDays(end, -1);
            break;

        case "next-day":
            from = addDays(start, 1);
            to   = addDays(end, 1);
            break;

        case "today":
            from = to = today;
            break;

        case "this-month":
            from = new Date(today.getFullYear(), today.getMonth(), 1);
            to   = new Date(today.getFullYear(), today.getMonth() + 1, 0);
            break;

        case "prev-month":
            from = new Date(start.getFullYear(), start.getMonth() - 1, 1);
            to   = new Date(start.getFullYear(), start.getMonth(), 0);
            break;

        case "next-month":
            from = new Date(end.getFullYear(), end.getMonth() + 1, 1);
            to   = new Date(end.getFullYear(), end.getMonth() + 2, 0);
            break;
    }

    startEl.value = formatDate(from);
    endEl.value   = formatDate(to);
}

function addDays(date, n){
    const d = new Date(
        date.getFullYear(),
        date.getMonth(),
        date.getDate()
    ); // ★ 一旦リセット
    d.setDate(d.getDate() + n);
    return d;
}

function formatDate(d){
    return d.toLocaleDateString("sv-SE");
}

function getBaseDate(el){
    const v = el.value;
    if(!v){
        const now = new Date();
        return new Date(
            now.getFullYear(),
            now.getMonth(),
            now.getDate()
        ); // ★ 時刻リセット
    }
    const [y, m, d] = v.split("-").map(Number);
    return new Date(y, m - 1, d); // ★ ローカル日付
}

// function formatDate(d){
//     const y = d.getFullYear();
//     const m = String(d.getMonth() + 1).padStart(2, "0");
//     const day = String(d.getDate()).padStart(2, "0");
//     return `${y}-${m}-${day}`;
// }