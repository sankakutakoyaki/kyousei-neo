"use strict"

// -------------------------------------------------------------------------------------------------------------------------------------- 保存処理
async function execSave(self) {
    let changedList = list02.filter(value => (value.state > 0));
    if (changedList.length == 0) return;

    changedList.forEach(function (item) {
        // let target = list02.filter(value => (value.timeworks_id == item.timeworks_id));
        let start = document.querySelector('#table-02-content tr[data-id="' + item.timeworks_id + '"] input[name="start"]');
        let end = document.querySelector('#table-02-content tr[data-id="' + item.timeworks_id + '"] input[name="end"]');
        let rest = document.querySelector('#table-02-content tr[data-id="' + item.timeworks_id + '"] input[name="rest"]');
        item.comp_start_time = start.value;
        item.comp_end_time = end.value;
        item.rest_time = rest.value;
        item.state = 0;
    });
    const result = await updateTimeworks(changedList, self);
    if (result.success) {
        // 画面更新
        openMsgDialog("msg-dialog", result.message, "blue");
        await saveAfterUpdateDisplay();
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }
}

// -------------------------------------------------------------------------------------------------------------------------------------- 確定処理
async function execConfirm(self) {
    if (list02.some(value => value?.state > 0)) {
        openMsgDialog("msg-dialog", "未保存のデータがあります", "red");
        return;
    }

    if (list02.some(value => value?.comp_start_time === "00:00:00" || value?.comp_end_time === "00:00:00")) {
        openMsgDialog("msg-dialog", "未入力のデータがあります", "red");
        return;
    }

    list02.forEach(function (item) {
        item.state = completeNum;
    });
    const result = await updateTimeworks(list02, self);
    if (result.success) {
        // 画面更新
        openMsgDialog("msg-dialog", result.message, "blue");
        await confirmAfterUpdateDisplay();
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }
}

// -------------------------------------------------------------------------------------------------------------------------------------- 保存・確定後の処理
async function saveAfterUpdateDisplay() {
    const selectedId = document.querySelector("#employee-list-02 li.selected");
    if (selectedId != null) {
        await createEmployeeTimeworksList("02", selectedId.dataset.id);

        const btn = document.getElementById("save-btn02");
        if (btn != null) {
            btn.disabled = true;
            if (btn.classList.contains("ok")){
                btn.classList.remove("ok");
            }
        }
        const btn2 = document.getElementById("confirm-btn02");
        if (btn2 != null) {
            if (list02.length > 0) {
                btn2.disabled = false;
                if (!btn2.classList.contains("ok")){
                    btn2.classList.add("ok");
                }
            } else {
                btn2.disabled = true;
                if (btn2.classList.contains("ok")){
                    btn2.classList.remove("ok");
                }
            }
        }
    }
}

async function confirmAfterUpdateDisplay() {
    const selectedId = document.querySelector("#employee-list-02 li.selected");
    if (selectedId != null) {
        await createEmployeeTimeworksList("02", selectedId.dataset.id);

        const btn2 = document.getElementById("confirm-btn02");
        if (btn2 != null) {
            btn2.disabled = true;
            if (btn2.classList.contains("ok")){
                btn2.classList.remove("ok");
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------------------------------------------- セル変更処理
function cellChange(id, self) {
    self.classList.add("changed");
    const item = list02.find(value => (value.timeworks_id == Number(id)));
    if (item != null) item.state = 1; // 変更のフラグ
    const btn = document.getElementById("save-btn02");
    if (btn != null) {
        btn.disabled = false;
        if (!btn.classList.contains("ok")){
            btn.classList.add("ok");
        }
    }
    const btn2 = document.getElementById("confirm-btn02");
    if (btn2 != null) {
        if (!list02.some(value => value?.state > 0)) {
            btn2.disabled = false;
            if (!btn2.classList.contains("ok")){
                btn2.classList.add("ok");
            }
        }
    }
}

// エンターフォーカス処理をイベントリスナーに登録する
function setEnterFocusFromEmployeeTimeworkList() {
    tabFocusElements = createTabFocusElements();
    setEnterFocus("table-02");
    const btn2 = document.getElementById("confirm-btn02");
    if (btn2 != null) {
        if (list02.length > 0) {
            btn2.disabled = false;
            if (!btn2.classList.contains("ok")){
                btn2.classList.add("ok");
            }
        } else {
            btn2.disabled = true;
            if (btn2.classList.contains("ok")){
                btn2.classList.remove("ok");
            }
        }
    }
}