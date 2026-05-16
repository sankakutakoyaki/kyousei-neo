"use strict"

/******************************************************************************************************* 入力画面 */

// リスト画面の本体部分を作成する
// function createTableContent(tableId, list) {
//     const tbl = document.getElementById(tableId);
//     list.forEach(function (item) {
//         let newRow = tbl.insertRow();
//         // ID（Post送信用）
//         newRow.setAttribute('name', 'data-row');
//         newRow.setAttribute('data-id', item.qualifications_id);
//         // シングルクリック時の処理
//         newRow.onclick = function (e) {
//             if (!e.currentTarget.classList.contains('selected')){
//                 // すべての行の選択状態を解除する
//                 detachmentSelectClassToAllRow(tbl, false);
//                 // 選択した行にセレクトクラスを付与する
//                 const result = addSelectClassToRow(e.currentTarget);
//             } else {
//                 // 選択済みの要素をクリックした時の処理
//                 const clickedTd = e.target.closest("td");
//                 // 取得したTDの処理
//             }
//         }

//         createTableRow(newRow, item, "01");
//     });
// }
// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const table = document.getElementById(tableId);

    list.forEach(item => {
        const row = createSelectableRow({
            table,
            item,
            idKey: "qualificationsId",
            onDoubleClick: (item) => {
                execEdit(item.employeeId, this);
            }
        });

        createTableRow(row, item);
    });
}

// テーブル行を作成する
function createTableRow(newRow, item, tab) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    // ID
    newRow.insertAdjacentHTML('beforeend', '<td name="id-cell" class="link-cell" onclick="execEdit(' + item.ownerId + ', this)">' + String(item.ownerId).padStart(4, '0') + '</td>');
    // 名前
    newRow.insertAdjacentHTML('beforeend', '<td name="name-cell"><span class="kana">' + item.ownerNameKana + '</span><br><span>' + item.ownerName + '</span></td>');
    // 資格名
    newRow.insertAdjacentHTML('beforeend', '<td name="qualification-name-cell"><span>' + (item.qualificationName ?? "登録なし") + '</span></td>');
    // 取得状況
    newRow.insertAdjacentHTML('beforeend', '<td name="status-cell" data-status="' + item.status + '"><span>' + item.status + '</span></td>');
    // 有効期限
    newRow.insertAdjacentHTML('beforeend', '<td name="expiry-cell"><span>' + (item.expiryDate == "9999-12-31" ? "": item.expiryDate) + '</span></td>');
    // 有効
    newRow.insertAdjacentHTML('beforeend', '<td name="enabled-cell"><span>' + (item.status == "取得済み" && item.is_enabled == 0 ? "期限切れ": "") + '</span></td>');
}

/******************************************************************************************************* 保存 */

// // 保存処理
// async function execSave() {
//     // 登録済みかチェックする
//     const chkCombo = document.getElementById('qualification-combo');
//     const targetLi = qualificationList01.querySelector('li[data-id="' + chkCombo.value + '"]');

//     if (targetLi) {
//         if  (!targetLi.classList.contains('selected')) {
//             openMsgDialog("msg-dialog", "すでに登録済みです", "red");
//             return;
//         }
//     }

//     // 従業員が選択されているかチェックする
//     const name = document.getElementById('name-box-01');
//     if (name.value == "") return;

//     const form = document.getElementById('form-01');
//     // エラーチェック
//     if (formDataCheck(form) == false) {
//         return;
//     } else {
//         const formData = new FormData(form);
//         const formdata = structuredClone(formEntity);
//         const id = document.getElementById('code-box-01');
//         formdata.owner_id = id.value;
//         formdata.owner_category = owner_category;
//         formdata.qualifications_id = Number(fileId01.value);
//         formdata.qualification_master_id = formData.get('qualification-combo');
//         formdata.number = formData.get('number').trim();
//         if (formData.get('acquisition-date') == "") {
//             formdata.acquisition_date = "9999-12-31";
//         } else {
//             formdata.acquisition_date = formData.get('acquisition-date');
//         }
//         if (formData.get('expiry-date') == "") {
//             formdata.expiry_date = "9999-12-31";
//         } else {
//             formdata.expiry_date = formData.get('expiry-date');
//         }
//         formdata.version = Number(formData.get('version'));

//         // 保存処理
//         const result = await searchFetch("/api/qualifications/save", JSON.stringify(formdata), token);
//         if (result.ok) {
//             // 画面更新
//             updatePanels();
//             await execUpdate();
//             openMsgDialog("msg-dialog", result.message, "blue");
//         }
//     }
// }

// // 入力チェック
// function formDataCheck(area) {
//     let msg = "";
//     // 資格が選択されていないとFalseを返す
//     const combo = area.querySelector('input[name="qualification-combo"]');
//     if (combo != null && combo.value < 1) msg += '\n資格が選択されていません';
//     // 番号が入力されていないとFalseを返す
//     const number = area.querySelector('input[name="number"]');
//     if (number != null && number.value == "") msg += '\n番号が入力されていません';
//     // エラーが一つ以上あればエラーメッセージダイアログを表示する
//     if (msg != "") {
//         openMsgDialog("msg-dialog", msg, "red");
//         return false;
//     }
//     return true;
// }

/******************************************************************************************************* ダウンロード */

// async function execDownloadCsv(self) {

//     const tbl = document.getElementById("table-01-content");
//     if (tbl == null) return;

//     const ids = tbl.querySelectorAll('tr:has(input[name="chk-box"]:checked)');

//     if (ids.length == 0) {
//         // 選択された要素がなければメッセージを表示して終了
//         openMsgDialog("msg-dialog", "選択されていません", "red");
//     } else {
//         // スピナー表示
//         startProcessing();

//         let csvLines = [];
//         // ヘッダー行
//         csvLines.push(["所有者ID", "所有者", "資格名", "取得状況", "有効期限", "期限切れ"]);

//         for (let data of ids) {
//             const row = [
//                 data.querySelector('[name="id-cell"]').textContent.trim(),
//                 data.querySelector('[name="name-cell"]>span:nth-of-type(2)').textContent.trim(),
//                 data.querySelector('[name="qualification-name-cell"]').textContent.trim(),
//                 data.querySelector('[name="status-cell"]').textContent.trim(),
//                 data.querySelector('[name="expiry-cell"]').textContent.trim(),
//                 data.querySelector('[name="enabled-cell"]').textContent.trim(),
//             ];

//             // CSVフォーマット用にエスケープ
//             const escapedRow = row.map(value => {
//                 const v = value.trim();
//                 if (v.includes(",") || v.includes('"') || v.includes("\n")) {
//                     return `"${v.replace(/"/g, '""')}"`; // ダブルクォート内のダブルクォートをエスケープ
//                 }
//                 return v;
//             });

//             csvLines.push(escapedRow);
//         }

//         // 最終的なCSV文字列を作成
//         const csvString = csvLines.map(line => line.join(",")).join("\n");

//         // 文字列データが返却されなければ、エラーメッセージを表示
//         if (csvString == null || csvString == "") {
//             openMsgDialog("msg-dialog", "取得できませんでした", "red");
//         } else {
//             createCsvThenDownload(csvString);
//         }

//         // スピナー消去
//         processingEnd();
//     }
// }

/******************************************************************************************************* アップロード */

// async function uploadFile(files) {
//     const id = document.getElementById('qualifications-id-02');
//     const formData = new FormData();
//     for (let i = 0; i < files.length; i++) {
//         formData.append('files', files[i]); // name="files" がサーバと一致する必要あり
//     }
//     formData.append("folder_name", "qualification/" + String(id.value) + "/");
//     formData.append("id", parseInt(id.value));

//     const response = await fetch("/api/files/upload/qualifications", {
//         headers: {  // リクエストヘッダを追加
//             'X-CSRF-TOKEN': token,
//         },
//         method: "POST",
//         body: formData,
//     });

//     const result = await response.json();
//     return result;
// }

// function setDragAndDrop() {
//     fileInput.addEventListener('change', () => {
//         handleFiles(fileInput.files);
//     });

//     fileList.addEventListener('dragover', (e) => {
//         e.preventDefault();
//         fileList.classList.add('dragover');
//     });

//     fileList.addEventListener('dragleave', () => {
//         fileList.classList.remove('dragover');
//     });

//     fileList.addEventListener('drop', (e) => {
//         e.preventDefault();
//         fileList.classList.remove('dragover');
//         handleFiles(e.dataTransfer.files);
//     });
// }

// async function handleFiles(files) {
//     const id = document.getElementById('qualifications-id-02');
//     if (id.value == "") {
//         openMsgDialog("msg-dialog", "資格が選択されていません", "red");
//         return;
//     }

//     startProcessing();

//     const result = await uploadFile(files);
//     if (result.length > 0) {
//         fileList.innerHTML = "";
//         for (const file of result) {
//             // リストに登録する
//             setPdfListItem(id.value, file.file_name, file.internal_name);
//         }
//     }
//     updatePlaceholder(fileList);

//     processingEnd();
// }

/******************************************************************************************************* 削除 */

// async function removeQualificationById(id) {
//     const result = await searchFetch('/api/qualifications/delete/id', JSON.stringify({id:id}), token);
//     if (result.ok) {
//         openMsgDialog("msg-dialog", result.message, "blue");
//         updatePanels();
//     }
// }

/******************************************************************************************************* 取得 */

// コードから担当者と登録資格を取得してリストに表示する
async function getQualifications(codeId, nameId) {
    // if ((e.key === "Enter") || e === "Enter") {
        const code = document.getElementById(codeId);
        const name = document.getElementById(nameId);

        if (code.value == "") return;
        if (isNaN(parseInt(code.value))) return;
        
        // startProcessing();

        const panel = code.closest('.tab-panel');
        const tab = panel.dataset.panel;

        const data = JSON.stringify({primaryId:parseInt(code.value), secondaryId:parseInt(owner_category)});
        const result = await searchFetch("/api/qualifications/get/id", data, token);

        // if (result.data.length > 0) {
        //     name.value = result.data[0].owner_name;

        //     switch (tab) {
        //         case "03":
        //             qualificationList01.innerHTML = "";
        //             initialForm03();
        //             for (const item of result.data) {
        //                 // 選択機能付きリストアイテムを取得する
        //                 const li = createListItemWithSelection(item.qualifications_id, item.qualification_name);
        //                 const removeBtn = createRemoveBtn();
        //                 removeBtn.addEventListener('click', async (e) => {
        //                     e.stopPropagation(); // liの他のイベントが誤動作しないように
        //                     removeQualificationById(item.qualifications_id);
        //                 });

        //                 li.appendChild(removeBtn);

        //                 if (!li.classList.contains('selected')) updatePlaceholder(fileList);
        //                 // クリック時に入力ボックスに値を挿入する処理を登録する
        //                 li.addEventListener('click', () => {
        //                     const form = document.getElementById('form-01');
        //                     if (li.classList.contains('selected')) {
        //                         fileId01.value = item.qualifications_id;
        //                         form.querySelector('[name="qualification-combo"]').value = item.qualification_master_id;
        //                         form.querySelector('[name="number"]').value = item.number;
        //                         if (item.acquisition_date != "9999-12-31") {
        //                             form.querySelector('[name="acquisition-date"]').value = item.acquisition_date;
        //                         } else {
        //                             form.querySelector('[name="acquisition-date"]').value = "";
        //                         }
        //                         if (item.expiry_date != "9999-12-31") {
        //                             form.querySelector('[name="expiry-date"]').value = item.expiry_date;
        //                         } else {
        //                             form.querySelector('[name="expiry-date"]').value = "";
        //                         }
        //                         form.querySelector('[name="version"]').value = item.version;
        //                     } else {
        //                         fileId01.value = "0";
        //                         form.querySelector('[name="qualification-combo"]').value = -1;
        //                         form.querySelector('[name="number"]').value = "";
        //                         form.querySelector('[name="acquisition-date"]').value = "";
        //                         form.querySelector('[name="expiry-date"]').value = "";
        //                         form.querySelector('[name="version"]').value = "0";
        //                     }
        //                 });

        //                 qualificationList01.appendChild(li);
        //             }
        //             break;
        //         case "04":
        //             qualificationList02.innerHTML = "";
        //             fileList.innerHTML = "";
        //             updatePlaceholder(fileList);
        //             for (const item of result.data) {
        //                 // 選択機能付きリストアイテムを取得する
        //                 const li = createListItemWithSelection(item.qualifications_id, item.qualification_name);
        //                 // クリック時にIDをリセットする処理を登録する
        //                 li.addEventListener('click', () => {
        //                     document.getElementById('qualifications-id-02').value = "";
        //                     updatePlaceholder(fileList);
        //                     // その他の処理
        //                     fileList.innerHTML = "";
        //                     getQualificationsFiles(item.qualifications_id);
        //                     fileId02.value = item.qualifications_id;
        //                 });

        //                 qualificationList02.appendChild(li);
        //             }
        //             fileId02.value = "";
        //             break;
        //         default:
        //             break;
        //     }
        // } else {                        
        //     const result2 = await searchFetch(url, JSON.stringify({id:parseInt(code.value)}), token);

        //     // リストの初期化
        //     switch (tab) {
        //         case "03":
        //             qualificationList01.innerHTML = "";
        //             initialForm03();
        //             break;
        //         case "04":
        //             qualificationList02.innerHTML = "";
        //             fileList.innerHTML = "";
        //             fileId02.value = "";
        //             break;
        //         default:
        //             break;
        //     }
        //     if (result2.ok) {
        //         switch (url) {
        //             case "/api/employee/get/id":
        //                 name.value = result2.data.full_name;
        //                 break;
        //             case "/api/company/get/id":
        //                 name.value = result2.data.name;
        //                 break;             
        //             default:
        //                 name.value = "";
        //                 break;
        //         }
                
        //     } else {
        //         initialPanel(tab);
        //     }
        // }

        // processingEnd();
    // }
}

// // 保持資格のPDFファイルリストを取得して表示する
// async function getQualificationsFiles(id) {
//     startProcessing();
    
//     const folderName = id;
//     const result = await postFetch('/api/files/get/qualifications', JSON.stringify({id:id}), token);

//     if (result.data.length > 0) {
//         for (const file of result.data) {
//             // リストに登録する
//             setPdfListItem(folderName, file.file_name, file.internal_name);
//         }
//     }
//     updatePlaceholder(fileList);

//     processingEnd();
// }

// // リストを登録する
// function setPdfListItem(folderName, fileName, internalName) {
//     const path = folderName + "/" + encodeURIComponent(internalName);

//     const li = createListItem("/api/files/qualification/" + path, fileName);

//     const removeBtn = setFileRemoveEventListner(createRemoveBtn(), li, path, '/api/files/delete/qualifications');
//     li.appendChild(removeBtn);

//     fileList.appendChild(li);

//     updatePlaceholder(fileList);
// }

// // パネルを初期化する
// function initialPanel(tab) {
//     switch (tab) {
//         case "01":
            
//             break;
//         case "03":
//             document.getElementById('code-box-01').value = "";
//             document.getElementById('name-box-01').value = "";
//             initialForm03();
//             qualificationList01.innerHTML = "";
//             break;
//         case "04":
//             document.getElementById('code-box-02').value = "";
//             document.getElementById('name-box-02').value = "";
//             qualificationList02.innerHTML = "";
//             fileList.innerHTML = "";
//             fileId02.value = "";
//             break;
//         default:
//             break;
//     }
// }

// // パネルの中身をアップデートする
// function updatePanels() {
//     getQualifications('code-box-01', 'name-box-01');
//     initialForm03();
//     initialPanel("04");
// }

// function initialForm03() {
//     fileId01.value = "";
//     document.getElementById('qualification-combo').value = -1;
//     document.getElementById('number').value = "";
//     document.getElementById('acquisition-date').value = "";
//     document.getElementById('expiry-date').value = "";
//     document.getElementById('version01').value = "0";
// }

/******************************************************************************************************* 画面更新 */

async function execUpdate(tab) {
    let getUrl = "";
    // リスト取得
    if (owner_category == 0) {
        // 従業員の場合
        getUrl = "/api/qualifications/get";
    } else {
        // 会社の場合
        getUrl = "/api/qualifications/get/license";
    }
    const resultResponse = await fetch(getUrl);
    const result = await resultResponse.json();
    
    if (resultResponse.ok){
        origin = result.data;
        // 画面更新
        await execFilterDisplay(tab);        
    }
}

// 資格情報フィルター
function filterQualificationCombo(list) {
    const qualification = document.getElementById('qualification-search');
    let searchList;
    if (qualification.value === "0") {
        // 0 の場合は全件表示
        searchList = list;
    } else {
        searchList = list.filter(value => value.qualificationMasterId === Number(qualification.value));
    }
    return searchList;
}

// 取得状況フィルター
function filterStatusCombo(list) {
    const status = document.getElementById('status-search');
    let searchList;
    if (status.value === "すべて") {
        // すべて の場合は全件表示
        searchList = list;
    } else {
        searchList = list.filter(value => value.status === status.value);
    }
    return searchList;
}

// 一括フィルター
async function execFilterDisplay(tab) {
    const cfg = MODE_CONFIG[tab];
    let list = structuredClone(origin);
    list = filterQualificationCombo(list);
    list = filterStatusCombo(list);
    await updateTableDisplay(cfg.tableId, cfg.footerId, cfg.searchId, list, createTableContent);
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {

    // setDragAndDrop();

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        await execFilterDisplay("01");
    }, false);

    Object.values(CODE_CONFIG).forEach(cfg => {
        setEnterFocus(cfg.area);
        const el = document.getElementById(cfg.codeId);
        if (!el) return;

        el.addEventListener('blur', async () => {
            await getQualifications(cfg.codeId, cfg.nameId);
        });
    });

    // 検索用資格コンボボックスを作成する
    const qualificationSearchArea = document.getElementById('qualification-search');
    createComboBoxWithTop(qualificationSearchArea, qualificationComboList, "すべて");
    qualificationSearchArea.addEventListener('change', async () => {
        await execFilterDisplay("01");
    });

    // 検索用状況コンボボックスを作成する
    const statusSearchArea = document.getElementById('status-search');
    statusSearchArea.addEventListener('change', async () => {
        await execFilterDisplay("01");
    });

    // 資格コンボボックスを作成する
    const qualificationArea = document.getElementById('qualification-combo');
    createComboBox(qualificationArea, qualificationComboList);
    qualificationArea.value = -1;
    qualificationArea.addEventListener('change', function (e) {
        fileId01.value = "";
        document.getElementById('number').value = "";
        document.getElementById('acquisition-date').value = "";
        document.getElementById('expiry-date').value = "";
        document.getElementById('version01').value = "0";
    });

    // エンターフォーカス処理をイベントリスナーに登録する
    setEnterFocus("form-01");

    // 画面更新
    await execUpdate("01");
    // await execFilterDisplay("01");

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })
});