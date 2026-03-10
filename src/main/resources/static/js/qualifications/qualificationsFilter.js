"use strict"

import { initFile, loadQualificationsList } from "./qualificationsFile.js";

let currentCfg = null;

/**
 * 初期化
 */
export function initFilter(CONFIG){

    currentCfg = CONFIG;
    
    initFile(CONFIG.FILE.license);
    loadQualificationsList();

    for (const tab of Object.keys(CONFIG.MODE)) {
        const cfg = CONFIG.MODE[tab];

        // 資格コンボボックス作成
        const fi = document.getElementById(cfg.filterId);
        if (fi) {
            createComboBoxWithTop(fi, cfg.comboList, cfg.comboListTop);
            fi.addEventListener('change', async () => {
                await cfg.codeChange();
            });
        }

        // 検索ボックス作成
        const se = document.getElementById(cfg.searchId);
        if (se)  {
            se.addEventListener('search', async () => {
                await execFilterDisplay(tab);
            }, false);
        }

        makeSortable(cfg.tableId);
    };

    setEnterFocus("form-01");
    initCompanyInputs(CONFIG.COMPANY);

    // 担当者コード入力時の処理
    for (const tab of Object.keys(CONFIG.ID)) {
        const cfg = CONFIG.ID[tab];

        const elm = document.getElementById(cfg.codeId);
        if (elm)  {
            elm.addEventListener('blur', async () => {
                await changeCodeToName(cfg, "/api/employee/get/id");
                updateFilter03State();
            });
        }

        setEnterFocus(cfg.area);
    }

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })

    const closeBtn = document.getElementById("viewer-close");
    if (closeBtn) {
        closeBtn.addEventListener("click", () =>
            closeFormDialog("form-fileviewer")
        );
    }

    document.querySelectorAll(".row-enable").forEach(cb => {
        cb.addEventListener("change", handleRowSelection);
    });

    ["code03", "code04"].forEach(id => {
        const el = document.getElementById(id);
        if (!el) return;

        ["input", "blur"].forEach(evt => {
            el.addEventListener(evt, updateFilter03State);
        });
    });

    // 初期状態も反映
    updateFilter03State();

    // setFileConfig('license', CONFIG);
}

/**
 * 行の選択を処理する
 */
export function handleRowSelection(e) {

    if (!currentCfg) return;

    const cate = e.target.dataset.cate;
    const cfg = currentCfg.CHK[cate];
    const filter = document.getElementById(cfg.filterId);
    if (!filter) return;

    createComboBoxWithTop(filter, cfg.comboList, "");

    const allCheckboxes = document.querySelectorAll(".row-enable");
    if (e.target.checked) {
        allCheckboxes.forEach(other => {
            if (other !== e.target) {
                other.checked = false;
                setCodeEnabled(other.closest(".flex-area"), false);
            }
        });
        setCodeEnabled(e.target.closest(".flex-area"), true);
    } else {
        setCodeEnabled(e.target.closest(".flex-area"), false);
    }

    setFileConfig(cate);
    resetEnterFocus();
    updateFilter03State();
}

/**
 * チェックボックス変更時の処理
 */
export function setCodeEnabled(area, enabled) {

    const codeInput = area.querySelector("input[name='code']");
    if (codeInput) {
        codeInput.disabled = !enabled;
        codeInput.value = null;
    }

    const nameInput = area.querySelector("input[name='name']");
    if (nameInput) {
        nameInput.value = null;
    }

    const nameSelect = area.querySelector("select[name='name']");
    if (nameSelect) {
        nameSelect.disabled = !enabled;
        nameSelect.value = null;
    }

    if (enabled) {
        area.classList.add("select");
    } else {
        if (area.classList.contains("select")) area.classList.remove("select")
    }
}

/**
 * #code03 または #code04 のどちらかが未入力なら、#filter03 を操作不可（disabled）にする
 */
export function updateFilter03State() {

    const code03 = document.getElementById("code03")?.value.trim();
    const name03 = document.getElementById("name03")?.value;
    const code04 = document.getElementById("code04")?.value.trim();
    const name04 = document.getElementById("name04")?.value.trim();
    const filter03 = document.getElementById("filter03");

    if (!filter03) return;

    // どちらか未入力なら無効化
    filter03.disabled = !((code03 && name03 > 0) || (code04 && name04));

    if (filter03.disabled) {
        filter03.selectedIndex = 0;
    }
}