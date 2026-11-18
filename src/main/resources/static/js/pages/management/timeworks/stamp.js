"use strict";

import { mobileQuery } from "../../../core/access/mobileReadOnly.js";
import { TimeworksRepository } from "../../../repositories/management/TimeworksRepository.js";
import { formatDate } from "../../../util/time.js";
import { openMsgDialog } from "../../../core/ui/dialog/dialogCore.js";


export function createStamp(session, management) {
    let selectedEmployee = null;
    const mobileMedia = window.matchMedia(mobileQuery);
    let rolePersonalMode = false;
    let isPersonalMode = mobileMedia.matches;
    let responsiveChangeHandler = null;
    let clockTimer = null;
    let statusRequest = 0;
    let stamping = false;
    let listTimer = null;
    let refreshingList = false;

    session.onDispose(() => {
        statusRequest++;
        window.clearInterval(clockTimer);
        window.clearInterval(listTimer);
        if(responsiveChangeHandler) mobileMedia.removeEventListener("change", responsiveChangeHandler);
    });

    function initStamp(controller) {
        rolePersonalMode = APP.cache.page.personalMode === true;
        initClock(controller);
        initResponsiveStampMode(controller);
        initListRefresh(controller);
    }

    async function handleEmployeeResolved(controller, group) {
        if(!session.isActive() || isPersonalMode) return;
        statusRequest++;
        selectedEmployee = group._resolvedData ?? null;

        if (!selectedEmployee) {
            setStampButtonsDisabled(true);
            return;
        }

        focusStampButton(selectedEmployee);
    }

    async function handleStamp(controller, button) {
        if (!session.isActive() || !selectedEmployee || stamping || button.disabled) return;
        const employee = selectedEmployee;
        const personalMode = isPersonalMode;
        const request = statusRequest;
        const canApply = () => session.isActive() && personalMode === isPersonalMode
            && request === statusRequest && selectedEmployee === employee;
        stamping = true;

        const stampType = button.dataset.stampType;
        setStampButtonsDisabled(true);

        try {
            if (personalMode) {
                // 本人打刻時のみGPSを取得し、失敗しても打刻を継続する
                const location = await getStampLocation();
                if (!canApply()) return;
                await TimeworksRepository.stampSelf(stampType, location);
            } else {
                await TimeworksRepository.stamp(employee.employeeId, stampType);
            }

            if(!canApply()) return;
            await controller.dataTable?.refresh();
            if(!canApply()) return;

            if (isPersonalMode) await loadSelfStatus();
            else resetStampForm();

        } catch (error) {
            openMsgDialog({
                message: error.message || "打刻に失敗しました。",
                color: "red"
            });

            if(canApply()) {
                if (isPersonalMode) await loadSelfStatus();
                else resetStampForm();
            }
        } finally {stamping = false;}
    }

    /**
     * 本人打刻時の位置情報を取得する。
     * 取得失敗・権限拒否・タイムアウト時はnullを返す。
     */
    async function getStampLocation() {
        if (!navigator.geolocation || !window.isSecureContext) return null;

        const position = await new Promise(resolve => {
            let completed = false;
            const finish = value => {
                if (completed) return;
                completed = true;
                window.clearTimeout(timer);
                resolve(value);
            };

            // 位置情報の取得を最大5秒で終了する
            const timer = window.setTimeout(() => finish(null), 5000);

            try {
                navigator.geolocation.getCurrentPosition(
                    position => finish(position),
                    () => finish(null),
                    {enableHighAccuracy: true, timeout: 5000, maximumAge: 0}
                );
            } catch {
                finish(null);
            }
        });

        if (!position) return null;

        // GPS取得日時を日本時間のLocalDateTime形式に変換する
        const recordedAt = new Date(position.timestamp + 9 * 60 * 60 * 1000)
            .toISOString().slice(0, 23);

        return {
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
            accuracy: position.coords.accuracy,
            recordedAt
        };
    }

    function initClock(controller) {
        const date = session.byId("stamp-date");
        const time = session.byId("stamp-time");
        let currentDateKey = formatDate(new Date(), "yyyy-MM-dd");

        const render = () => {
            const now = new Date();
            if (date) date.textContent = formatDate(now, "yyyy年MM月dd日");
            if (time) time.textContent = formatDate(now, "HH:mm:ss");
            return now;
        };

        render();

        const timerId = clockTimer = window.setInterval(async () => {
            if (!session.isActive() || !date?.isConnected || !time?.isConnected) {
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

    // 打刻一覧を60秒ごとに更新する（画面遷移や多重更新は行わない）
    function initListRefresh(controller) {
        window.clearInterval(listTimer);
        listTimer = window.setInterval(async () => {
            if (!session.isActive() || isPersonalMode || refreshingList) return;
            if (!controller.dataTable) return;

            refreshingList = true;
            try {
                await controller.dataTable.refresh();
            } catch (error) {
                console.warn("勤怠一覧の自動更新に失敗しました", error);
            } finally {
                refreshingList = false;
            }
        }, 60 * 1000);
    }

    function initResponsiveStampMode(controller) {
        const applyMode = async event => {
            if(!session.isActive()) return;
            statusRequest++;
            selectedEmployee = null;
            isPersonalMode = rolePersonalMode || event.matches;

            session.root.classList.toggle("personal-timeworks-mode", isPersonalMode);

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

        responsiveChangeHandler = event => {applyMode(event).catch(error => {
            if(session.isActive()) openMsgDialog({message: error.message, color: "red"});
        });};
        mobileMedia.addEventListener("change", responsiveChangeHandler);
        applyMode(mobileMedia).catch(error => {
            if(session.isActive()) openMsgDialog({message: error.message, color: "red"});
        });
    }

    function ensureDesktopPage(controller) {
        if (!controller.dataTable) controller.init();
        management.initManagement();
    }

    async function loadSelfStatus() {
        if(!session.isActive()) return;
        const request = ++statusRequest;
        renderStampRecords(null, "読み込み中…");
        setStampButtonsDisabled(true);
        selectedEmployee = null;

        try {
            const status = await TimeworksRepository.findSelf();
            if(!session.isActive() || request !== statusRequest || !isPersonalMode) return;
            selectedEmployee = status;
            renderStampRecords(selectedEmployee);
            focusStampButton(selectedEmployee);
        } catch (error) {
            if(!session.isActive() || request !== statusRequest) return;
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
            const element = session.byId(id);
            if (!element) continue;

            element.textContent = value
                ? `${value.slice(0, 10).replaceAll("-", "/")} ${value.slice(11, 16)}`
                : emptyText;
        }
    }

    function focusStampButton(status) {
        const start = session.root.querySelector('[data-stamp-type="START"]');
        const end = session.root.querySelector('[data-stamp-type="END"]');

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
        session.root.querySelectorAll("[data-stamp-type]")
            .forEach(button => button.disabled = disabled);
    }

    function setInputValue(id, value) {
        const element = session.byId(id);
        if (element) element.value = value || "";
    }

    function resetStampForm() {
        statusRequest++;
        selectedEmployee = null;

        const group = session.root.querySelector(".stamp-employee[data-resolve]");
        const identifier = session.byId("stamp-identifier");

        if (group) group._resolvedData = null;

        if (identifier) {
            identifier.value = "";
            delete identifier.dataset.lastId;
        }

        setInputValue("stamp-user-name", "");
        setStampButtonsDisabled(true);

        window.requestAnimationFrame(() => {if(session.isActive()) identifier?.focus();});
    }
    return {initStamp, handleEmployeeResolved, handleStamp};
}
