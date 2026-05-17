"use strict"

// import { getToday } from "../../../util/time.js";
import { getBaseDate } from "../../../util/date.js";
import { calculateRange } from "../../../util/dateRange.js";
import { formatDate } from "../../../util/date.js";
// import { parseDate } from "../../../util/date.js";

export function handleDateMove(event, el){
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

// export function handleDateArrowMove(event, el){
//     if(event.type !== "keydown") return;
//     if(el.dataset.type !== "date") return;

//     let diff = 0;
//     if(event.key === "ArrowUp") diff = 1;
//     if(event.key === "ArrowDown") diff = -1;
//     if(diff === 0) return;

//     event.preventDefault();

//     const current = el.valueAsDate ?? new Date();
//     current.setDate(current.getDate() + diff);
//     el.valueAsDate = current;

//     adjustPair(el);
// }
export function handleDateArrowMove(event, el){
    if(event.type !== "keydown") return;

    if(el.dataset.type !== "date") return;

    // Shiftなしはブラウザ標準動作
    if(!event.shiftKey) return;

    let current = el.value ? new Date(el.value): new Date();

    // Shift + 左右 → 日移動
    if(event.key === "ArrowRight"){
        current.setDate(current.getDate() + 1);
    }
    else if(event.key === "ArrowLeft"){
        current.setDate(current.getDate() - 1);
    }
    // Shift + 上下 → 月移動
    else if(event.key === "ArrowUp"){
        current.setMonth(current.getMonth() + 1);
    }
    else if(event.key === "ArrowDown"){
        current.setMonth(current.getMonth() - 1);
    }
    else{
        return;
    }

    event.preventDefault();

    el.value = current.toISOString().slice(0, 10);
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