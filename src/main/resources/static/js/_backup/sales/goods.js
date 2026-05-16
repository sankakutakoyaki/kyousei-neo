"use strict"

/******************************************************************************************************* 入力画面 */

// リスト画面の本体部分を作成する
function createTableContent(tableId, list) {
    const tbl = document.getElementById(tableId);
    list.forEach(function (item) {
        let newRow = tbl.insertRow();
        // ID（Post送信用）
        newRow.setAttribute('name', 'data-row');
        newRow.setAttribute('data-id', item.order_item_id);
        // シングルクリック時の処理
        newRow.onclick = function (e) {
            if (!e.currentTarget.classList.contains('selected')){
                // すべての行の選択状態を解除する
                detachmentSelectClassToAllRow(tbl, false);
                // 選択した行にセレクトクラスを付与する
                const result = addSelectClassToRow(e.currentTarget);
            } else {
                // 選択済みの要素をクリックした時の処理
                const clickedTd = e.target.closest("td");
                // 取得したTDの処理
            }
        }
        // ダブルクリック時の処理
        newRow.ondblclick = function (e) { 
            // チェックボックスの動作を停止させる
            e.preventDefault();
            // 選択済みの場合
            if (!e.currentTarget.classList.contains('selected')){
                // すべての行の選択状態を解除する
                detachmentSelectClassToAllRow(tableId, false);
                // 選択した行にセレクトクラスを付与する
                const result = addSelectClassToRow(e.currentTarget);
            }
            
            // フォーム入力画面を表示する
            execEdit(item.order_item_id, this);
        }

        switch (tableId) {
            case "table-01-content":
                createTable01Row(newRow, item);
                break;
            case "table-02-content":
                createTable02Row(newRow, item);
                break;
            case "table-03-content":
                createTable03Row(newRow, item);
                break;
            case "table-04-content":
                createTable04Row(newRow, item);
                break;
            default:
                break;
        }
    });
}

// リスト画面の本体部分を作成する
function createFormTableContent(tableId, list) {
    if (list == null) return;
    const tbl = document.getElementById(tableId);
    list.forEach(function (item) {
        if (item.state != deleteCode) {
            let newRow = tbl.insertRow();
            // ID（Post送信用）
            newRow.setAttribute('name', 'data-row');
            newRow.setAttribute('data-id', item.order_item_id);

            switch (tableId) {
                case "table-11-content":
                    createTable11Row(newRow, item);
                    break;
                case "table-12-content":
                    createTable12Row(newRow, item);
                    break;
                case "table-13-content":
                    createTable13Row(newRow, item);
                    break;
                case "table-14-content":
                    createTable14Row(newRow, item);
                    break;
                default:
                    break;
            }                        
        }
    });
}

/******************************************************************************************************* テーブル行作成 */

// テーブル行を作成する
function createTab01Row(newRow, item) {
    // 入荷日 検品者
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.arrival_date == '9999-12-31' ? "": item.arrival_date) + '</span><br><span>' + (item.inspector_name ?? "") + '</span></td>');
    // 運送会社 伝票番号
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.shipping_company_name ?? "") + '</span><br><span>' + (item.document_number ?? "") + '</span></td>');
    // 商品名 型式
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.item_name ?? "") + '</span><br><span>' + (item.item_model ?? "") + '</span></td>');
    // 数量
    newRow.insertAdjacentHTML('beforeend', '<td name="quantity-cell" class="text-right"><span>' + (item.item_quantity ?? "0") + '</span></td>');
    // 発荷主 支店
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.company_name ?? "") + '</span><br><span>' + (item.office_name ?? "") + '</span></td>');
    // 備考 お届け先
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.remarks ?? "") + '</span><br><span>' + (item.delivery_address ?? "") + '</span></td>');
}

function createTab02Row(newRow, item) {
    // 入荷日 検品者
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.arrival_date == '9999-12-31' ? "": item.arrival_date) + '</span><br><span>' + (item.inspector_name ?? "") + '</span></td>');
    // 仕入先 支店
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.company_name ?? "") + '</span><br><span>' + (item.office_name ?? "") + '</span></td>');
    // 購入者
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.buyer_company_name ?? "") + '</span><br><span>' + (item.buyer_name ?? "") + '</span></td>');
    // 商品名 型式
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.item_name ?? "") + '</span><br><span>' + (item.item_model ?? "") + '</span></td>');
    // 数量
    newRow.insertAdjacentHTML('beforeend', '<td class="text-right"><span>' + (item.item_quantity ?? "0") + '</span></td>');
    // 単価
    newRow.insertAdjacentHTML('beforeend', '<td class="text-right"><span>' + (item.payment ?? 0).toLocaleString('ja-JP') + '</span></td>');
    // 備考 伝票番号
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.remarks ?? "") + '</span><br><span>' + (item.document_number ?? "") + '</span></td>');
}

function createTab03Row(newRow, item) {
    // 入荷日 検品者
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.arrival_date == '9999-12-31' ? "": item.arrival_date) + '</span><br><span>' + (item.inspector_name ?? "") + '</span></td>');
    // 運送会社 伝票番号
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.shipping_company_name ?? "") + '</span><br><span>' + (item.document_number ?? "") + '</span></td>');
    // 商品名 型式
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.item_name ?? "") + '</span><br><span>' + (item.item_model ?? "") + '</span></td>');
    // 数量
    newRow.insertAdjacentHTML('beforeend', '<td class="text-right"><span>' + (item.item_quantity ?? "") + '</span></td>');
    // 発荷主
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.company_name ?? "") + '</span>><br><span>' + (item.office_name ?? "") + '</span></td>');
    // 備考
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.remarks ?? "") + '</span></td>');
}

function createTab04Row(newRow, item) {
    // 返却日 検品者
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.arrival_date == '9999-12-31' ? "": item.arrival_date) + '</span><br><span>' + (item.inspector_name ?? "") + '</span></td>');
    // 運送会社 伝票番号
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.shipping_company_name ?? "") + '</span><br><span>' + (item.document_number ?? "") + '</span></td>');
    // 商品名 型式
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.item_name ?? "") + '</span><br><span>' + (item.item_model ?? "") + '</span></td>');
    // 数量
    newRow.insertAdjacentHTML('beforeend', '<td class="text-right"><span>' + (item.item_quantity ?? "") + '</span></td>');
    // 取引先
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.company_name ?? "") + '</span><br><span>' + (item.office_name ?? "") + '</span></td>');
    // 備考
    newRow.insertAdjacentHTML('beforeend', '<td><span>' + (item.remarks ?? "") + '</span></td>');
}

function createTable01Row(newRow, item) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    createTab01Row(newRow, item);
}

function createTable02Row(newRow, item) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    createTab02Row(newRow, item);
}
function createTable03Row(newRow, item) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    createTab03Row(newRow, item);
}
function createTable04Row(newRow, item) {
    // 選択用チェックボックス
    newRow.insertAdjacentHTML('beforeend', '<td name="chk-cell" class="pc-style"><input class="normal-chk" name="chk-box" type="checkbox"></td>');
    createTab04Row(newRow, item);
}
function createTable11Row(newRow, item) {
    createTab01Row(newRow, item);
    // 削除ボタン
    newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteItem(this, ' + item.order_item_id + ')"><img src="/icons/dust.png"></div></td>');
}

function createTable12Row(newRow, item) {
    createTab02Row(newRow, item);
    // 削除ボタン
    newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteItem(this, ' + item.order_item_id + ')"><img src="/icons/dust.png"></div></td>');
}

function createTable13Row(newRow, item) {
    createTab03Row(newRow, item);
    // 削除ボタン
    newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteItem(this, ' + item.order_item_id + ')"><img src="/icons/dust.png"></div></td>');
}

function createTable14Row(newRow, item) {
    createTab04Row(newRow, item);
    // 削除ボタン
    newRow.insertAdjacentHTML('beforeend', '<td name="delete-btn"><div class="img-btn" onclick="deleteItem(this, ' + item.order_item_id + ')"><img src="/icons/dust.png"></div></td>');
}

/******************************************************************************************************* 入力画面 */

// 商品登録画面を開く
async function execEdit(id, self) {
    hamburgerItemAddSelectClass('.normal-sidebar', '入荷')
    // スピナー表示
    startProcessing();

    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;

    // // 入力フォームダイアログを開く
    // openFormDialog("form-dialog-01"); 

    let entity = {};
    if (id > 0) {
        // 選択されたIDのエンティティを取得
        // const data = "id=" + encodeURIComponent(parseInt(id));
        const result = await searchFetch('/api/order/item/get/id', JSON.stringify({id:parseInt(id)}), token);
        if (!result?.ok) return;
        // const result = await resultResponse.json();
        // if (result.order_item_id == 0) {
        //     openMsgDialog("msg-dialog", "データがありません", "red");
        //     processingEnd();
        //     return;
        // }
        entity = structuredClone(result);
    } else {
        entity = structuredClone(formEntity);
        entity.arrival_date = getDate();
        entity.item_quantity = 1;
        switch (tab) {
            case "01":
                entity.classification = goodsCode;
                break;
            case "02":
                entity.classification = materialsCode;
                break;
            case "03":
                entity.classification = equipmentCode;
                break;
            case "04":
                entity.classification = returnsCode;
                break;
            default:
                break;
        }
    }

    let form = "";
    let formId = "";
    let formDialogId = "";

    switch (tab) {
        case "01":
            formDialogId = "form-dialog-01";
            formId = "form-01";
            break;
        case "02":
            formDialogId = "form-dialog-02";
            formId = "form-02";
            break;
        case "03":
            formDialogId = "form-dialog-03";
            formId = "form-03";
            break;
        case "04":
            formDialogId = "form-dialog-04";
            formId = "form-04";
            break;
        default:
            break;
    }

    
    form = document.getElementById(formDialogId);
    resetFormInput(form, tab);
    setFormContent(form, entity, tab);
    setEnterFocus(formId);

    openFormDialog(formDialogId); 

    // スピナー消去
    processingEnd();
}

// コンテンツ部分作成
function setFormContent(form, entity, tab) {
    itemList = [];
    form.querySelector('[name="order-item-id"]').value = entity.order_item_id;
    form.querySelector('[name="order-id"]').value = entity.order_id;
    form.querySelector('[name="version"]').value = entity.version;

    if (entity.inspector_id > 0) {
        form.querySelector('[name="inspector-id"]').value = entity.inspector_id;
        form.querySelector('[name="inspector-name"]').value = entity.inspector_name;
    } 

    if (form.querySelector('[name="arrival-date"]') != null) {
        if (entity.arrivalt_date == "9999-12-31") {
            form.querySelector('[name="arrival-date"]').value = "";
        } else {
            form.querySelector('[name="arrival-date"]').value = entity.arrival_date;
        }
    }

    if (form.querySelector('[name="delivery-address"]') != null) {
        form.querySelector('[name="delivery-address"]').value = entity.delivery_address;
    }

    if (form.querySelector('[name="buyer-id"]') != null) {
        if (entity.buyer_id > 0) {
            form.querySelector('[name="buyer-id"]').value = entity.buyer_id;
            form.querySelector('[name="buyer-company-name"]').value = entity.buyer_company_name;
            form.querySelector('[name="buyer-name"]').value = entity.buyer_name;
        } 
    }

    if (form.querySelector('[name="document-number"]') != null) {
        form.querySelector('[name="document-number"]').value = entity.document_number;
    }
    
    if (form.querySelector('[name="item-maker"]') != null) {
        form.querySelector('[name="item-maker"]').value = entity.item_maker;
    }

    if (form.querySelector('[name="item-name"]') != null) {
        form.querySelector('[name="item-name"]').value = entity.item_name;
    }

    if (form.querySelector('[name="item-model"]') != null) {
        form.querySelector('[name="item-model"]').value = entity.item_model;
    }

    if (form.querySelector('[name="item-quantity"]') != null) {
        form.querySelector('[name="item-quantity"]').value = entity.item_quantity;
    }

    if (form.querySelector('[name="item-payment"]') != null) {
        form.querySelector('[name="item-payment"]').value = entity.item_payment;
    }
    
    if (form.querySelector('[name="remarks"]') != null) {
        form.querySelector('[name="remarks"]').value = entity.remarks;
    }
    
    let selectId = 0;  
    // 荷主コンボボックス
    if (form.querySelector('select[name="company"]') != null) {
        const companyArea = form.querySelector('select[name="company"]');
        selectId = companyArea.value;
        switch(tab) {
            case "01":
                createComboBoxWithTop(companyArea, companyComboList, "");
                break;
            case "02":
                createComboBoxWithTop(companyArea, supplierComboList, "");
                break;
            case "03":
                createComboBoxWithTop(companyArea, supplierComboList, "");
                break;
            case "04":
                createComboBoxWithTop(companyArea, companyComboList, "");
                break;
            default:
                break;
        }
        createComboBoxWithTop(companyArea, companyComboList, "");
        setComboboxSelected(companyArea, entity.company_id);
        companyArea.onchange = function() { createOfficeComboBox(form) };
    }

    // 支店コンボボックス
    if (form.querySelector('select[name="office"]') != null) {
        const officeArea = form.querySelector('select[name="office"]');
        const officeComboList = officeList.filter(value => { return value.company_id == selectId }).map(item => ({number:item.office_id, text:item.name}));
        createComboBox(officeArea, officeComboList);
    }

    // 運送会社コンボボックス
    const shippingCompanyArea = form.querySelector('select[name="shipping-company"]');
    createComboBoxWithTop(shippingCompanyArea, shippingCompanyComboList, "");
    setComboboxSelected(shippingCompanyArea, entity.shipping_company_id);

    form.querySelector('[name="inspector-id"]').focus();
}

// 選択した会社の支店をコンボボックスに登録する
function createOfficeComboBox(form) {
    const companyArea = form.querySelector('select[name="company"]');
    const officeArea = form.querySelector('select[name="office"]');

    const selectId = companyArea.value;  
    const officeComboList = officeList.filter(value => { return value.company_id == selectId }).map(item => ({number:item.office_id, text:item.name}));
    createComboBoxWithTop(officeArea, officeComboList, "");
}

/******************************************************************************************************* 削除 */

async function execDelete(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    let result;
    switch (tab) {
        case "01":
            result = await deleteTablelist('table-01-content', '/api/order/item/delete');
            break;
        case "02":
            result = await deleteTablelist('table-02-content', '/api/order/item/delete');
            break;
        case "03":
            result = await deleteTablelist('table-03-content', '/api/order/item/delete');
            break;
        case "04":
            result = await deleteTablelist('table-04-content', '/api/order/item/delete');
            break;
        default:
            break;
    }
    
    if (result.success) {                
        await execDateSearch(self);
        openMsgDialog("msg-dialog", result.message, "blue");
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }
}

async function deleteItem(self, id) {
    if (id < 0) {
        itemList = itemList.filter(value => value.order_item_id != id);
    } else {
        const item = itemList.find(value => value.order_item_id == id);
        item.state = deleteCode;
    }
    
    const panel = self.closest('.dialog-content');
    const tab = panel.dataset.panel;                
    switch (tab) {
        case "11":
            await updateFormTableDisplay("table-11-content");
            break;
        case "12":
            await updateFormTableDisplay("table-12-content");
            break;
        case "13":
            await updateFormTableDisplay("table-13-content");
            break;
        case "14":
            await updateFormTableDisplay("table-14-content");
            break;
        default:
            break;
    }
}

/******************************************************************************************************* 登録 */

async function execRegistItem(self) {
    const panel = self.closest('.dialog-content');
    const tab = panel.dataset.panel;

    let tableId = "";
    let form = [];
    const formdata = structuredClone(formEntity);

    switch (tab) {
        case "11":
            document.getElementById('regist-btn11').focus();
            form = document.getElementById('form-01');
            formdata.classification = goodsCode;
            tableId = "table-11-content";
            if (formDataCheck(form) == false) {
                return;
            }
            break;
        case "12":
            document.getElementById('regist-btn12').focus();
            form = document.getElementById('form-02');
            formdata.classification = materialsCode;
            tableId = "table-12-content";
            if (formDataCheck(form) == false) {
                return;
            }
            break;
        case "13":
            document.getElementById('regist-btn13').focus();
            form = document.getElementById('form-03');
            formdata.classification = equipmentCode;
            tableId = "table-13-content";
            if (formDataCheck(form) == false) {
                return;
            }
            break;
        case "14":
            document.getElementById('regist-btn14').focus();
            form = document.getElementById('form-04');
            formdata.classification = returnsCode;
            tableId = "table-14-content";
            if (formDataCheck(form) == false) {
                return;
            }
            break;
        default:
            break;
    }

    const formData = new FormData(form);
    
    formdata.order_id = formData.get('order-id');
    if (formData.get('order-item-id') > 0) {
        formdata.order_item_id = formData.get('order-item-id');
    } else {
        itemId -= 1;
        formdata.order_item_id = itemId;
    }
    formdata.state = 0;
    formdata.version = formData.get('version');

    if (formData.get('arrival-date') == "") {
        formdata.arrival_date = "9999-12-31";
    } else {
        formdata.arrival_date = formData.get('arrival-date');
    }

    if (formData.get('shipping-company') != null) {
        formdata.shipping_company_id = formData.get('shipping-company');
        const selectShippingCompany = form.querySelector('[name="shipping-company"]');
        const shippingCompanyName = selectShippingCompany.options[selectShippingCompany.selectedIndex].text;
        formdata.shipping_company_name = shippingCompanyName;
    }

    if (formData.get('inspector-id') != null) {
        if (formData.get('inspector-id') == "") {
            formdata.inspector_id = 0;
            formdata.inspector_name = "";
        } else {
            formdata.inspector_id = formData.get('inspector-id');
            formdata.inspector_name = formData.get('inspector-name');
        }
    }

    if (formData.get('buyer-id') != null) {
        if (formData.get('buyer-id') == "") {
            formdata.buyer_id = 0;
            formdata.buyer_company_name = "";
            formdata.buyer_name = "";
        } else {
            formdata.buyer_id = formData.get('buyer-id');
            formdata.buyer_company_name = formData.get('buyer-company-name');
            formdata.buyer_name = formData.get('buyer-name');
        }
    }

    if (formData.get('document-number') != null) {
        formdata.document_number = formData.get('document-number');
    }

    if (formData.get('company') != null) {
        formdata.company_id = formData.get('company');
        const selectCompany = form.querySelector('[name="company"]');
        const companyName = selectCompany.options[selectCompany.selectedIndex].text;
        formdata.company_name = companyName;
    }

    if (formData.get('office') != null) {
        formdata.office_id = formData.get('office');
        const selectOffice = form.querySelector('[name="office"]');
        const officeName = selectOffice.options[selectOffice.selectedIndex].text;
        formdata.office_name = officeName;
    }

    if (formData.get('delivery-address') != null) {
        formdata.delivery_address = formData.get('delivery-address');
    }

    if (formData.get('item-maker') != null) {
        if (formData.get('item-maker') == "") {
            formdata.item_maker = "-----";
        } else {
            formdata.item_maker = formData.get('item-maker');
        }
    }

    if (formData.get('item-name') != null) {
        if (formData.get('item-name') == "") {
            formdata.item_name = "-----";
        } else {
            formdata.item_name = formData.get('item-name');
        }
    }

    if (formData.get('item-model') != null) {
        if (formData.get('item-model') == "") {
            formdata.item_model = "-----";
        } else {
            formdata.item_model = formData.get('item-model');
        }
    }

    if (formData.get('item-quantity') != null) {
        if (formData.get('item-quantity') == "") {
            formdata.item_quantity = 1;
        } else {
            formdata.item_quantity = formData.get('item-quantity');
        }                          
    }

    if (formData.get('item-payment') != null) {
        if (formData.get('item-payment') == "") {
            formdata.item_payment = 1;
        } else {
            formdata.item_payment = formData.get('item-payment');
        }                          
    }                 

    if (formData.get('remarks') != null) {
        formdata.remarks = formData.get('remarks');
    }

    itemList.push(formdata);

    await updateFormTableDisplay(tableId, itemList);
    scrollIntoTableList(tableId, itemId);
    resetFormInput(form, tab);
}

// 入力フォームの内容をリセットする
function resetFormInput(form, tab) {
    form.querySelector('[name="document-number"]').value = "";
    form.querySelector('[name="item-maker"]').value = "";
    form.querySelector('[name="item-name"]').value = "";
    form.querySelector('[name="item-model"]').value = "";
    form.querySelector('[name="item-quantity"]').value = 1;
    form.querySelector('[name="remarks"]').value = "";
    switch (tab) {
        case "01":
            form.querySelector('[name="delivery-address"]').value ="";
            form.querySelector('[name="shipping-company"]').value = 0;
            break;
        case "02":
            form.querySelector('[name="item-payment"]').value = 0;
            form.querySelector('[name="buyer-id"]').value = "";
            form.querySelector('[name="buyer-company-name"]').value = "";
            form.querySelector('[name="buyer-name"]').value = "";
            break;
        case "03":
            form.querySelector('[name="shipping-company"]').value = 0;
            break;
        case "04":
            form.querySelector('[name="shipping-company"]').value = 0;
            break;
        default:
            break;
    }
    form.querySelector('[name="inspector-id"]').focus();
}

// 入力チェック
function formDataCheck(area) {
    let msg = "";
    // 型番が入力されていないとFalseを返す
    const name = area.querySelector('input[name="item-name"]');
    const model = area.querySelector('input[name="item-model"]');
    if (name == null || model == null) {
        msg += '\n予期せぬエラーです';
    } else {
        if (name.value == "" && model.value == "") msg += '\n品目か型番を入力して下さい';
    }
    
    // エラーが一つ以上あればエラーメッセージダイアログを表示する
    if (msg != "") {
        openMsgDialog("msg-dialog", msg, "red");
        return false;
    }
    return true;
}

/******************************************************************************************************* 保存 */

// 保存処理
async function execSave(formId, tableId) {
    // 保存処理
    const resultResponse = await postFetch("/api/order/item/save", JSON.stringify({list:itemList}), token, "application/json");
    const result = await resultResponse.json();
    if (result.success) {
        const tbl = document.getElementById(tableId);
        // 画面更新
        openMsgDialog("msg-dialog", result.message, "blue");
        await execDateSearch(tbl);
        // 追加・変更行に移動
        scrollIntoTableList(tableId, result.id);
    } else {
        openMsgDialog("msg-dialog", result.message, "red");
    }
    // ダイアログを閉じる
    closeFormDialog(formId);
}

/******************************************************************************************************* ダウンロード */

async function execDownloadCsv(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    let result;
    switch (tab) {
        case "01":
            result = await downloadCsv('table-01-content', '/api/order/item/download/csv');
            break;
        case "02":
            result = await downloadCsv('table-02-content', '/api/order/item/download/csv');
            break;
        case "03":
            result = await downloadCsv('table-03-content', '/api/order/item/download/csv');
            break;
        case "04":
            result = await downloadCsv('table-04-content', '/api/order/item/download/csv');
            break;
        default:
            break;
    }
}

/******************************************************************************************************* 画面更新 */

// テーブルリスト画面を更新する
async function updateTableDisplay(tableId, footerId, searchId, list) {
    // フィルター処理
    const result = filterDisplay(searchId, list);
    // リスト画面を初期化
    deleteElements(tableId);
    // リスト作成
    createTableContent(tableId, result);
    // フッター作成
    createTableFooter(footerId, list);
    // チェックボタン押下時の処理を登録する
    registCheckButtonClicked(tableId);
    // すべて選択ボタンをオフにする
    turnOffAllCheckBtn(tableId);
    // テーブルのソートをリセットする
    resetSortable(tableId);
    // スクロール時のページトップボタン処理を登録する
    setPageTopButton(tableId);
    // テーブルにスクロールバーが表示されたときの処理を登録する
    document.querySelectorAll('.scroll-area').forEach(el => {
        toggleScrollbar(el);
    });
}

// テーブルリスト画面を更新する
async function updateFormTableDisplay(tableId, list) {
    // リスト画面を初期化
    deleteElements(tableId);
    // リスト作成
    createFormTableContent(tableId, list);
    // スクロール時のページトップボタン処理を登録する
    setPageTopButton(tableId);
    // テーブルにスクロールバーが表示されたときの処理を登録する
    document.querySelectorAll('.scroll-area').forEach(el => {
        toggleScrollbar(el);
    });
}

async function execDateSearch(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    switch (tab) {
        case "01":
            // リスト取得
            origin = await getOrderItemsBetween("start-date01", "end-date01", "/api/order/item/get/between");
            origin01 = origin.filter(value => value.classification == goodsCode);
            // 画面更新
            await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin01);
            break;
        case "02":
            // リスト取得
            origin = await getOrderItemsBetween("start-date02", "end-date02", "/api/order/item/get/between");
            origin02 = origin.filter(value => value.classification == materialsCode);
            // 画面更新
            await updateTableDisplay("table-02-content", "footer-02", "search-box-02", origin02);
            break;
        case "03":
            // リスト取得
            origin = await getOrderItemsBetween("start-date03", "end-date03", "/api/order/item/get/between");
            origin03 = origin.filter(value => value.classification == equipmentCode);
            // 画面更新
            await updateTableDisplay("table-03-content", "footer-03", "search-box-03", origin03);
            break;
        case "04":
            // リスト取得
            origin = await getOrderItemsBetween("start-date04", "end-date04", "/api/order/item/get/between");
            origin04 = origin.filter(value => value.classification == returnsCode);
            // 画面更新
            await updateTableDisplay("table-04-content", "footer-04", "search-box-04", origin04);
            break;
        default:
            break;
    }
}

// 一覧表示用のリスト取得
async function getOrderItemsBetween(startId, endId, url) {
    const start = document.getElementById(startId).value;
    const end = document.getElementById(endId).value;
    // const data = "&start=" + encodeURIComponent(start) + "&end=" + encodeURIComponent(end);
    // const contentType = 'application/x-www-form-urlencoded';
    // // List<OrderItem>を取得
    // const resultResponse = await postFetch(url, data, token, contentType);
    // return await resultResponse.json();
    return await postFetch(url, JSON.stringify({start:start, end:end}), token);
}

async function execFilterDisplay(self) {
    const panel = self.closest('.tab-panel');
    const tab = panel.dataset.panel;
    switch (tab) {
        case "01":
            await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin01);
            break;
        case "02":
            await updateTableDisplay("table-02-content", "footer-02", "search-box-02", origin02);
            break;
        case "03":
            await updateTableDisplay("table-03-content", "footer-03", "search-box-03", origin03);
            break;
        case "04":
            await updateTableDisplay("table-04-content", "footer-04", "search-box-04", origin04);
            break;
        default:
            break;
    }
}

/******************************************************************************************************* コード入力時の処理 */
// コード入力ボックスからフォーカスが外れた時の処理
function execInspectorCodeBlur(e) {
    e.preventDefault();
    searchForNameByInspectorCode(e);
}

// コードから[employee]を取得して、名前と会社名を表示
async function searchForNameByInspectorCode(e) {
    if (e.currentTarget == null) return;
    const form = e.currentTarget.closest('.dialog-content');
    const tab = form.dataset.panel;
    let code = 0;
    switch (tab) {
        case "11":
            if (inspectorId11.value == "" || isNaN(inspectorId11.value)) {
                inspectorId11.value = "";
                inspectorName11.value = "";
                return;
            }
            code = inspectorId11.value;
            break;
        case "12":
            if (inspectorId12.value == "" || isNaN(inspectorId12.value)) {
                inspectorId12.value = "";
                inspectorName12.value = "";
                return;
            }
            code = inspectorId12.value;
            break;
        case "13":
            if (inspectorId13.value == "" || isNaN(inspectorId13.value)) {
                inspectorId13.value = "";
                inspectorName13.value = "";
                return;
            }
            code = inspectorId13.value;
            break;
        case "14":
            if (inspectorId14.value == "" || isNaN(inspectorId14.value)) {
                inspectorId14.value = "";
                inspectorName14.value = "";
                return;
            }
            code = inspectorId14.value;
            break;
        default:
            break;
    }

    const entity = await getEmployeeByCode(Number(code));

    if (entity != null && entity.employee_id > 0) {
        switch (tab) {
            case "11":
                inspectorName11.value = entity.full_name;
                break;
            case "12":
                inspectorName12.value = entity.full_name;
                break;
            case "13":
                inspectorName13.value = entity.full_name;
                break;
            case "14":
                inspectorName14.value = entity.full_name;
                break;
            default:
                break;
        }

    } else {
        switch (tab) {
            case "11":
                inspectorId11.value = "";
                inspectorName11.value = "";
                break;
            case "12":
                inspectorId12.value = "";
                inspectorName12.value = "";
                break;   
            case "13":
                inspectorId13.value = "";
                inspectorName13.value = "";
                break;
            case "14":
                inspectorId14.value = "";
                inspectorName14.value = "";
                break;
            default:
                break;
        }
        openMsgDialog("msg-dialog", "コードが登録されていません", 'red');
    }                
}

// コード入力ボックスからフォーカスが外れた時の処理
function execBuyerCodeBlur(e) {
    e.preventDefault();
    searchForNameByBuyerCode(e);
}

// コードから[employee]を取得して、名前と会社名を表示
async function searchForNameByBuyerCode(e) {
    if (e.currentTarget == null) return;
    if (buyerId12.value == "" || isNaN(buyerId12.value)) {
        buyerId12.value = "";
        buyerCompanyName12.value = "";
        buyerName12.value = "";
        return;
    }

    const entity = await getEmployeeByCode(Number(buyerId12.value));

    if (entity != null && entity.employee_id > 0) {
        buyerCompanyName12.value = entity.company_name;
        buyerName12.value = entity.full_name;
    } else {
        buyerId12.value = "";
        buyerCompanyName12.value = "";
        buyerName12.value = "";
        openMsgDialog("msg-dialog", "コードが登録されていません", 'red');
    }                
}

/******************************************************************************************************* 初期化時 */

// ページ読み込み後の処理
window.addEventListener("load", async () => {
    // hamburgerItemAddSelectClass('.header-title', 'sales');
    // hamburgerItemAddSelectClass('.normal-sidebar', 'goods');
    // スピナー表示
    startProcessing();

    // 検品者コード入力ボックスのフォーカスが外れた時の処理を登録
    inspectorId11.addEventListener('blur', function (e) { execInspectorCodeBlur(e); });
    // 検品者コード入力ボックスのフォーカスが外れた時の処理を登録
    inspectorId12.addEventListener('blur', function (e) { execInspectorCodeBlur(e); });
    // 検品者コード入力ボックスのフォーカスが外れた時の処理を登録
    inspectorId13.addEventListener('blur', function (e) { execInspectorCodeBlur(e); });
    // 検品者コード入力ボックスのフォーカスが外れた時の処理を登録
    inspectorId14.addEventListener('blur', function (e) { execInspectorCodeBlur(e); });
    // 検品者コード入力ボックスのフォーカスが外れた時の処理を登録
    buyerId12.addEventListener('blur', function (e) { execBuyerCodeBlur(e); });

    // 検索ボックス入力時の処理
    document.getElementById('search-box-01').addEventListener('search', async function(e) {
        await execFilterDisplay(e.currentTarget);
    }, false);
    // 検索ボックス入力時の処理
    document.getElementById('search-box-02').addEventListener('search', async function(e) {
        await execFilterDisplay(e.currentTarget);
    }, false);
    // 検索ボックス入力時の処理
    document.getElementById('search-box-03').addEventListener('search', async function(e) {
        await execFilterDisplay(e.currentTarget);
    }, false);
    // 検索ボックス入力時の処理
    document.getElementById('search-box-04').addEventListener('search', async function(e) {
        await execFilterDisplay(e.currentTarget);
    }, false);

    execSpecifyPeriod("today", 'start-date01', 'end-date01');
    execSpecifyPeriod("today", 'start-date02', 'end-date02');
    execSpecifyPeriod("today", 'start-date03', 'end-date03');
    execSpecifyPeriod("today", 'start-date04', 'end-date04');

    // 画面更新
    origin01 = origin.filter(value => value.classification == goodsCode);
    await updateTableDisplay("table-01-content", "footer-01", "search-box-01", origin01);
    origin02 = origin.filter(value => value.classification == materialsCode);
    await updateTableDisplay("table-02-content", "footer-02", "search-box-02", origin02);
    origin03 = origin.filter(value => value.classification == equipmentCode);
    await updateTableDisplay("table-03-content", "footer-03", "search-box-03", origin03);
    origin04 = origin.filter(value => value.classification == returnsCode);
    await updateTableDisplay("table-04-content", "footer-04", "search-box-04", origin04);

    // タブメニュー処理
    const tabMenus = document.querySelectorAll('.tab-menu-item');
    // イベント付加
    tabMenus.forEach((tabMenu) => {
        tabMenu.addEventListener('click', tabSwitch);
    })

    // スピナー消去
    processingEnd();
});