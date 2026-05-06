"use strict"

export function formatDate(d){
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, "0");
    const dd = String(d.getDate()).padStart(2, "0");
    return `${yyyy}-${mm}-${dd}`;
}

export function getBaseDate(el){
    const v = el.value;
    if(!v){
        const now = new Date();
        return new Date(
            now.getFullYear(),
            now.getMonth(),
            now.getDate()
        );
    }
    const [y, m, d] = v.split("-").map(Number);
    return new Date(y, m - 1, d);
}

export function parseDate(v){
    if(!/^\d{4}-\d{2}-\d{2}$/.test(v)) return null;
    const [y,m,d] = v.split("-").map(Number);
    return new Date(y, m - 1, d);
}

export function toExclusiveDate(value){
    if(!value){
        return value;
    }
    const d = parseDate(value);
    if(!d){
        return value;
    }
    d.setDate(d.getDate() + 1);
    return formatDate(d);
}
