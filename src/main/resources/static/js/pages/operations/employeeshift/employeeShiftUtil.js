"use strict"

/**
 * シフト区分表示
 */
export function getShiftMark(shiftType){
    switch(Number(shiftType)){
        case 1:
            return "⚪︎";
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

/**
 * 指定月の第1週（日曜日）を取得する
 * 月初が週の途中なら前月の日曜日から開始する
 */
export function getFirstWeekStart(monthValue){
    const parsed = parseMonth(monthValue);
    if(!parsed){
        return null;
    }

    const {year, month} = parsed;
    const date = new Date(year, month - 1, 1);
    date.setDate(date.getDate() - date.getDay());
    return date;
}

/**
 * 週の表示範囲を取得する（日曜日〜土曜日）
 * weekIndex: 0=第1週、1=第2週...
 */
export function getWeekRange(monthValue, weekIndex = 0){
    const start = getFirstWeekStart(monthValue);
    if(!start){
        return null;
    }

    start.setDate(start.getDate() + weekIndex * 7);

    const end = new Date(start);
    end.setDate(end.getDate() + 6);

    return {
        fromDate: createDateString(
            start.getFullYear(), start.getMonth() + 1, start.getDate()
        ),
        toDate: createDateString(
            end.getFullYear(), end.getMonth() + 1, end.getDate()
        )
    };
}

/**
 * 開始日から終了日までの日付一覧を取得する
 * 終了日を含む
 */
export function getDateList(fromDate, toDate){
    const dates = [];
    const current = new Date(`${fromDate}T00:00:00`);
    const end = new Date(`${toDate}T00:00:00`);

    while(current <= end){
        dates.push(createDateString(
            current.getFullYear(),
            current.getMonth() + 1,
            current.getDate()
        ));
        current.setDate(current.getDate() + 1);
    }

    return dates;
}