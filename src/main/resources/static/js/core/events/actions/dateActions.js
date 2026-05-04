"use strict"

import { getToday } from "../../../util/time.js";
import { getBaseDate } from "../../../util/date.js";
import { calculateRange } from "../../../util/dateRange.js";
import { formatDate } from "../../../util/date.js";
import { parseDate } from "../../../util/date.js";

export function handleDateMove(el){
    const type = el.dataset.type;
    const startId = el.dataset.start;
    const endId = el.dataset.end;
    const startEl = document.getElementById(startId);
    const endEl   = document.getElementById(endId);

    if(!startEl || !endEl) return;

    const base = getBaseDate(startEl);
    const range = calculateRange(type, base);

    if(!range) return;

    startEl.value = formatDate(range.start);
    endEl.value   = formatDate(range.end);
}

export function handleDateArrowMove(el, event){
    if(event.type !== "keydown") return;
    if(el.dataset.type !== "date") return;

    let diff = 0;
    if(event.key === "ArrowUp") diff = 1;
    if(event.key === "ArrowDown") diff = -1;
    if(diff === 0) return;

    event.preventDefault();

    const current = el.valueAsDate ?? new Date();
    current.setDate(current.getDate() + diff);
    el.valueAsDate = current;

    adjustPair(el);
}

export function autoFormatDate(el){
    if(el.dataset.type !== "date") return;

    let v = el.value.replace(/\D/g, ""); // 数字だけ
    if(v.length >= 4){
        v = v.slice(0,4) + "-" + v.slice(4);
    }
    if(v.length >= 7){
        v = v.slice(0,7) + "-" + v.slice(7,8);
    }

    el.value = v;
}

function adjustPair(el){
    const pairId = el.dataset.pair;
    if(!pairId) return;

    const other = document.getElementById(pairId);
    if(!other) return;

    const current = el.valueAsDate;
    const otherDate = other.valueAsDate;
    if(!current || !otherDate) return;

    if(el.dataset.role === "start" && current > otherDate){
        other.valueAsDate = current;
    }
    if(el.dataset.role === "end" && current < otherDate){
        other.valueAsDate = current;
    }
}



// export function handleDateMove(el){
//     const type = el.dataset.type;
//     const startId = el.dataset.start;
//     const endId = el.dataset.end;

//     const startEl = document.getElementById(startId);
//     const endEl   = document.getElementById(endId);
//     if(!startEl || !endEl) return;

//     const now = new Date();
//     const today = new Date(
//         now.getFullYear(),
//         now.getMonth(),
//         now.getDate()
//     );

//     const start = getBaseDate(startEl);
//     const end   = getBaseDate(endEl);

//     let from, to;

//     switch(type){
//         case "prev-day":
//             from = addDays(start, -1);
//             to   = addDays(end, -1);
//             break;

//         case "next-day":
//             from = addDays(start, 1);
//             to   = addDays(end, 1);
//             break;

//         case "today":
//             from = to = today;
//             break;

//         case "this-month":
//             from = new Date(today.getFullYear(), today.getMonth(), 1);
//             to   = new Date(today.getFullYear(), today.getMonth() + 1, 0);
//             break;

//         case "prev-month":
//             from = new Date(start.getFullYear(), start.getMonth() - 1, 1);
//             to   = new Date(start.getFullYear(), start.getMonth(), 0);
//             break;

//         case "next-month":
//             from = new Date(end.getFullYear(), end.getMonth() + 1, 1);
//             to   = new Date(end.getFullYear(), end.getMonth() + 2, 0);
//             break;
//     }

//     startEl.value = formatDate(from);
//     endEl.value   = formatDate(to);
// }

// function addDays(date, n){
//     const d = new Date(
//         date.getFullYear(),
//         date.getMonth(),
//         date.getDate()
//     ); // ★ 一旦リセット
//     d.setDate(d.getDate() + n);
//     return d;
// }

// function formatDate(d){
//     return d.toLocaleDateString("sv-SE");
// }

// function getBaseDate(el){
//     const v = el.value;
//     if(!v){
//         const now = new Date();
//         return new Date(
//             now.getFullYear(),
//             now.getMonth(),
//             now.getDate()
//         ); // ★ 時刻リセット
//     }
//     const [y, m, d] = v.split("-").map(Number);
//     return new Date(y, m - 1, d); // ★ ローカル日付
// }