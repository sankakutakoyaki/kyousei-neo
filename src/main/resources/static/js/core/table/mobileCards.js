"use strict";

let detailSequence = 0;
const mobileMedia = window.matchMedia("(max-width: 560px)");

function updateAccessibility(row) {
    if (mobileMedia.matches) {
        row.setAttribute("tabindex", "0");
        row.setAttribute("role", "button");
        row.setAttribute("aria-expanded", String(row.classList.contains("mobile-card-expanded")));
        row.setAttribute("aria-controls", row.dataset.cardDetails);
    } else {
        for (const name of ["tabindex", "role", "aria-expanded", "aria-controls"]) row.removeAttribute(name);
    }
}

mobileMedia.addEventListener("change", () => {
    document.querySelectorAll(".mobile-collapsible-card").forEach(updateAccessibility);
});

/** 一覧の既存セルを利用し、スマホではカード全体で開閉する。 */
export function attachMobileCard(table, row) {
    const page = table.closest("main[data-page]")?.dataset.page || "";
    const isOrder = page.endsWith("/orderPage.js") && table.id === "table-01";
    const isRecycle = page.endsWith("/recyclePage.js") && /^table-0[1-5]$/.test(table.id);
    if (!isOrder && !isRecycle) return;

    row.classList.add("mobile-collapsible-card");
    const details = [];
    const markDetail = (element, className) => {
        if (!element) return;
        element.classList.add(className);
        element.id = `mobile-card-detail-${++detailSequence}`;
        details.push(element.id);
    };
    for (const cell of row.querySelectorAll("td[data-field]")) {
        const field = cell.dataset.field;
        if (isOrder) {
            if (field === "title") {
                markDetail(cell.querySelector("span:first-of-type"), "mobile-card-secondary");
            } else if (field !== "date") {
                markDetail(cell, "mobile-card-detail");
            }
        } else if (field === "maker") {
            markDetail(cell.querySelector("span:last-of-type"), "mobile-card-secondary");
        } else if (field !== "recycleNumber" && !(table.id === "table-05" && field === "date")) {
            markDetail(cell, "mobile-card-detail");
        }
    }

    row.dataset.cardDetails = details.join(" ");
    updateAccessibility(row);
    const toggle = () => {
        row.classList.toggle("mobile-card-expanded", !row.classList.contains("mobile-card-expanded"));
        updateAccessibility(row);
    };
    row.addEventListener("click", event => {
        if (!mobileMedia.matches) return;
        event.stopPropagation();
        // 入力やリンクなどの固有操作は維持する。
        if (event.target.closest("button, a, input, select, textarea, [contenteditable]")) return;
        toggle();
    });
    row.addEventListener("keydown", event => {
        if (!mobileMedia.matches || event.target !== row || !["Enter", " "].includes(event.key)) return;
        event.preventDefault();
        event.stopPropagation();
        toggle();
    });
    // スマホの連続タップを既存の行編集に伝播させない。PCの動作は維持する。
    row.addEventListener("dblclick", event => {
        if (mobileMedia.matches) event.stopPropagation();
    });
}
