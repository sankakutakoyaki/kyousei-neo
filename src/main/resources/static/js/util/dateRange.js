"use strict"

export function calculateRange(type, base) {
    const today = getToday(); // ★ 常に現在日（時刻なし）
    const handlers = {
        // ===== 絶対 =====
        "today": () => ({ start: today, end: today }),
        "this-week": () => getWeekRange(today, 1),
        "this-month": () => getMonthRange(today),

        // ===== 相対 =====
        "prev-day": () => ({
            start: addDays(base, -1),
            end: addDays(base, -1)
        }),
        "next-day": () => ({
            start: addDays(base, 1),
            end: addDays(base, 1)
        }),
        "prev-week": () => {
            const d = addDays(base, -7);
            return getWeekRange(d, 1);
        },
        "next-week": () => {
            const d = addDays(base, 7);
            return getWeekRange(d, 1);
        },
        "prev-month": () => {
            const d = new Date(base);
            d.setMonth(d.getMonth() - 1);
            return getMonthRange(d);
        },
        "next-month": () => {
            const d = new Date(base);
            d.setMonth(d.getMonth() + 1);
            return getMonthRange(d);
        }
    };
    const fn = handlers[type];
    return fn ? fn() : null;
}


// ===== utils =====
function getToday(){
    const d = new Date();
    return new Date(d.getFullYear(), d.getMonth(), d.getDate());
}

function addDays(date, n){
    const d = new Date(date);
    d.setDate(d.getDate() + n);
    return d;
}

/**
 * 
 * @param {*} base 
 * @param {*} startOfWeek  0：日曜始まり　1：月曜始まり
 * @returns 
 */
function getWeekRange(base, startOfWeek = 0){
    const day = base.getDay();
    const diff = (day - startOfWeek + 7) % 7;
    const start = addDays(base, -diff);
    const end   = addDays(start, 6);
    return { start, end };
}

function getMonthRange(base){
    return {
        start: new Date(base.getFullYear(), base.getMonth(), 1),
        end: new Date(base.getFullYear(), base.getMonth() + 1, 0)
    };
}