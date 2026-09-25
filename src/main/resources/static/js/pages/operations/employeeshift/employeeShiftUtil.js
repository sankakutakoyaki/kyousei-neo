"use strict"

/**
 * シフト区分表示
 */
export function getShiftMark(shiftType){
    switch(Number(shiftType)){
        case 1:
            return "出";
        case 2:
            return "休";
        case 3:
            return "有";
        default:
            return "--";
    }
}

/**
 * 指定月の日数
 */
export function getLastDay(year, month){
    return new Date(year, month, 0).getDate();
}

/**
 * yyyy-MM から年月取得
 */
export function parseMonth(value){
    if(!value){
        return null;
    }

    const [year, month] = value.split("-").map(Number);
    return {year, month};
}

/**
 * 月初～翌月初
 */
export function getMonthRange(value){
    const parsed = parseMonth(value);
    if(!parsed){
        return null;
    }

    const {year, month} = parsed;
    const fromDate = `${year}-${String(month).padStart(2, "0")}-01`;
    const next = new Date(year, month, 1);
    const toDate = `${next.getFullYear()}-${String(next.getMonth() + 1).padStart(2, "0")}-01`;
    return {fromDate, toDate};
}


/**
 * yyyy-MM-dd
 */
export function createDateString(year, month, day){
    return (
        `${year}-` +
        `${String(month).padStart(2, "0")}-` +
        `${String(day).padStart(2, "0")}`
    );
}

/**
 * 曜日表示
 */
export function getWeekName(day){
    return [
        "日",
        "月",
        "火",
        "水",
        "木",
        "金",
        "土"
    ][day];
}