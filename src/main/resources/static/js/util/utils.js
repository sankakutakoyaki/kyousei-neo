"use strict"

export function getElement(el) {
    if(el instanceof HTMLElement) return el;
    return document.getElementById(el);
}