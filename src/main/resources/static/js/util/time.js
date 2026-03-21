"use strict"

export function formatDate(date = new Date(), format = "yyyy-MM-dd"){

    const d = (date instanceof Date) ? date : new Date(date);
    if (isNaN(d)) return "";

    const pad = (n, len = 2) => String(n).padStart(len, "0");

    const map = {
        yyyy: d.getFullYear(),
        MM: pad(d.getMonth() + 1),
        dd: pad(d.getDate()),
        HH: pad(d.getHours()),
        mm: pad(d.getMinutes()),
        ss: pad(d.getSeconds()),
        SSS: pad(d.getMilliseconds(), 3)
    };

    return format.replace(/yyyy|MM|dd|HH|mm|ss|SSS/g, k => map[k]);
}

export function formatTime(value){
    return formatDate(value, "HH:mm");
}