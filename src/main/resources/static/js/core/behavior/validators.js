"use strict"

export const validators = {
    email: (v) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v),
    phone: (v) => /^\d{10,11}$/.test(v.replaceAll("-", "")),
    recycle: (v) => /^\d{13}$/.test(v.replaceAll("-", ""))
};
