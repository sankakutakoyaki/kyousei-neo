"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { registerController } from "../../../application/controllerRegistry.js";
import { createMasterPage } from "../../../core/page/createMasterPage.js";
import { TimeworksRepository } from "../../repositories/management/TimeworksRepository.js";
import { createTimeworksListColumns } from "./columns.js";
import { formatDate, getToday } from "../../../util/time.js";
import { openMsgDialog } from "../../../core/ui/dialog/dialogCore.js";

export async function init() {
    await initCommon();
    await initPageCache("/api/timeworks/init/cache");

    const timeworks = timeworksPage();
    registerController("timeworks", timeworks);
    timeworks.init();
    initClock();
    initFilters(timeworks);
    initStampActions(timeworks);
    await Promise.all([timeworks.dataTable.refresh(), refreshStampStatus()]);
}

export const timeworksPage = () =>
    createMasterPage({
        key: "timeworks",
        tableId: "table-01",
        footerId: "footer-01",
        idKey: "timeworkId",
        repository: TimeworksRepository,
        columns: createTimeworksListColumns(),
        checkable: false,
        components: {combo: true},
        buildParams: () => ({
            workDate: document.getElementById("timeworks-work-date")?.value || getToday(),
            officeId: document.getElementById("timeworks-office-filter")?.value || null
        }),
        onDoubleClick: () => {},
        model: {
            pageSize: 50
        }
    });

function initClock() {
    const date = document.getElementById("stamp-date");
    const time = document.getElementById("stamp-time");
    const render = () => {
        const now = new Date();
        if (date) date.textContent = formatDate(now, "yyyy年MM月dd日");
        if (time) time.textContent = formatDate(now, "HH:mm:ss");
    };
    render();
    const timerId = window.setInterval(() => {
        if (!date?.isConnected || !time?.isConnected) {
            window.clearInterval(timerId);
            return;
        }
        render();
    }, 1000);
}

function initFilters(controller) {
    const workDate = document.getElementById("timeworks-work-date");
    if (workDate && !workDate.value) workDate.value = getToday();
    document.getElementById("timeworks-search")?.addEventListener("click", () => controller.dataTable.refresh());
}

function initStampActions(controller) {
    document.querySelectorAll("[data-stamp-type]").forEach(button => {
        button.addEventListener("click", async () => {
            setStampButtonsDisabled(true);
            try {
                const res = await TimeworksRepository.stamp(button.dataset.stampType);
                renderStampStatus(res.data);
                await controller.dataTable.refresh();
                openMsgDialog({message: res.message, color: "blue"});
            } catch (error) {
                openMsgDialog({message: error.message || "打刻に失敗しました。", color: "red"});
                await refreshStampStatus();
            }
        });
    });
}

async function refreshStampStatus() {
    renderStampStatus(await TimeworksRepository.findToday());
}

function renderStampStatus(status) {
    setText("stamp-user-name", status?.fullName);
    setText("stamp-office-name", status?.officeName);
    setText("stamp-status", status?.statusLabel || "確認できません");
    setText("stamp-start-result", formatStampTime(status?.startTime));
    setText("stamp-end-result", formatStampTime(status?.endTime));
    const start = document.querySelector('[data-stamp-type="START"]');
    const end = document.querySelector('[data-stamp-type="END"]');
    if (start) start.disabled = !status?.canStart;
    if (end) end.disabled = !status?.canEnd;
}

function setStampButtonsDisabled(disabled) {
    document.querySelectorAll("[data-stamp-type]").forEach(button => button.disabled = disabled);
}

function setText(id, value) {
    const element = document.getElementById(id);
    if (element) element.textContent = value || "-----";
}

function formatStampTime(value) {
    return value ? formatDate(value, "HH:mm") : "-----";
}
