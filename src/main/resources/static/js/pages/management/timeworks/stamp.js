"use strict";

import { mobileQuery } from "../../../core/access/mobileReadOnly.js";
import { TimeworksRepository } from "../../../repositories/management/TimeworksRepository.js";
import { formatDate } from "../../../util/time.js";
import { openMsgDialog } from "../../../core/ui/dialog/dialogCore.js";
import { initManagement } from "./management.js";

let selectedEmployee = null;
const mobileMedia = window.matchMedia(mobileQuery);
let rolePersonalMode = false;
let isPersonalMode = mobileMedia.matches;
let responsiveChangeHandler = null;

export function initStamp(controller) {
    rolePersonalMode = APP.cache.page.personalMode === true;
    initClock(controller);
    initResponsiveStampMode(controller);
}

export async function handleEmployeeResolved(controller, group) {
    selectedEmployee = group._resolvedData ?? null;

    if (!selectedEmployee) {
        setStampButtonsDisabled(true);
        return;
    }

    focusStampButton(selectedEmployee);
}

export async function handleStamp(controller, button) {
    if (!selectedEmployee) return;

    const stampType = button.dataset.stampType;
    setStampButtonsDisabled(true);

    try {
        if (isPersonalMode) {
            await TimeworksRepository.stampSelf(stampType);
        } else {
            await TimeworksRepository.stamp(selectedEmployee.employeeId, stampType);
        }

        await controller.dataTable?.refresh();

        if (isPersonalMode) await loadSelfStatus();
        else resetStampForm();

    } catch (error) {
        openMsgDialog({
            message: error.message || "打刻に失敗しました。",
            color: "red"
        });

        if (isPersonalMode) await loadSelfStatus();
        else resetStampForm();
    }
}

function initClock(controller) {
    const date = document.getElementById("stamp-date");
    const time = document.getElementById("stamp-time");
    let currentDateKey = formatDate(new Date(), "yyyy-MM-dd");

    const render = () => {
        const now = new Date();
        if (date) date.textContent = formatDate(now, "yyyy年MM月dd日");
        if (time) time.textContent = formatDate(now, "HH:mm:ss");
        return now;
    };

    render();

    const timerId = window.setInterval(async () => {
        if (!date?.isConnected || !time?.isConnected) {
            window.clearInterval(timerId);
            return;
        }

        const now = render();
        const nextDateKey = formatDate(now, "yyyy-MM-dd");
        if (nextDateKey === currentDateKey) return;

        currentDateKey = nextDateKey;

        if (isPersonalMode) await loadSelfStatus();
        else resetStampForm();

        try {
            await controller.dataTable?.refresh();
        } catch (error) {
            openMsgDialog({
                message: error.message || "当日の一覧を更新できませんでした。",
                color: "red"
            });
        }
    }, 1000);
}

function initResponsiveStampMode(controller) {
    const applyMode = async event => {
        isPersonalMode = rolePersonalMode || event.matches;

        document.querySelector("main[data-page$='timeworksPage.js']")
            ?.classList.toggle("personal-timeworks-mode", isPersonalMode);

        if (isPersonalMode) {
            await loadSelfStatus();
        } else {
            ensureDesktopPage(controller);
            resetStampForm();
        }
    };

    if (responsiveChangeHandler) {
        mobileMedia.removeEventListener("change", responsiveChangeHandler);
    }

    responsiveChangeHandler = applyMode;
    mobileMedia.addEventListener("change", responsiveChangeHandler);
    applyMode(mobileMedia);
}

function ensureDesktopPage(controller) {
    if (!controller.dataTable) controller.init();
    initManagement();
}

async function loadSelfStatus() {
    renderStampRecords(null, "読み込み中…");
    setStampButtonsDisabled(true);
    selectedEmployee = null;

    try {
        selectedEmployee = await TimeworksRepository.findSelf();
        renderStampRecords(selectedEmployee);
        focusStampButton(selectedEmployee);
    } catch (error) {
        renderStampRecords(null, "取得できませんでした");
        openMsgDialog({
            message: error.message || "本人の勤務状態を取得できませんでした。",
            color: "red"
        });
    }
}

function renderStampRecords(status, emptyText = "未打刻") {
    for (const [id, value] of [
        ["stamp-start-record", status?.startTime],
        ["stamp-end-record", status?.endTime]
    ]) {
        const element = document.getElementById(id);
        if (!element) continue;

        element.textContent = value
            ? `${value.slice(0, 10).replaceAll("-", "/")} ${value.slice(11, 16)}`
            : emptyText;
    }
}

function focusStampButton(status) {
    const start = document.querySelector('[data-stamp-type="START"]');
    const end = document.querySelector('[data-stamp-type="END"]');

    if (start) start.disabled = !status?.canStart;
    if (end) end.disabled = !status?.canEnd;

    if (status?.canStart) {
        start?.focus();
    } else if (status?.canEnd) {
        end?.focus();
    } else {
        if (isPersonalMode) return;

        openMsgDialog({
            message: "本日の出勤・退勤打刻は完了しています。",
            color: "blue"
        });

        resetStampForm();
    }
}

function setStampButtonsDisabled(disabled) {
    document.querySelectorAll("[data-stamp-type]")
        .forEach(button => button.disabled = disabled);
}

function setInputValue(id, value) {
    const element = document.getElementById(id);
    if (element) element.value = value || "";
}

function resetStampForm() {
    selectedEmployee = null;

    const group = document.querySelector(".stamp-employee[data-resolve]");
    const identifier = document.getElementById("stamp-identifier");

    if (group) group._resolvedData = null;

    if (identifier) {
        identifier.value = "";
        delete identifier.dataset.lastId;
    }

    setInputValue("stamp-user-name", "");
    setStampButtonsDisabled(true);

    window.requestAnimationFrame(() => identifier?.focus());
}