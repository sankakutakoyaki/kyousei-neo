"use strict"

// 数字が被らなように名前を作成する
function createUniqueName({
    list,
    groupFn,      // 省略可
    nameFn,
    baseName
}) {    
    const effectiveGroupFn = groupFn ?? (() => true);
    const filteredList = list.filter(effectiveGroupFn);

    const usedNumbers = filteredList
        .filter(effectiveGroupFn)
        .map(item => {
            const name = nameFn(item);
            const m = name?.match(/^新しい項目\((\d+)\)$/);
            return m ? Number(m[1]) : null;
        })
        .filter(n => n !== null);

    // baseName 単体が存在するか
    const plainExists = filteredList
        .some(item => nameFn(item) === baseName);

    // 何も存在しない場合だけ baseName
    if (!plainExists && usedNumbers.length === 0) {
        return baseName;
    }

    let num = 1;
    while (usedNumbers.includes(num)) {
        num++;
    }
    return `${baseName}(${num})`;
}

// コードから[timeworks]を取得して、名前を表示
async function searchForEmployeeNameByCode(url, codeBox, nameBox) {
    if (codeBox == null || nameBox == null) return;
    if (codeBox.value == "" || isNaN(codeBox.value)) {
        nameBox.value = "";
        return;
    }

    const result = await searchFetch(url, JSON.stringify({id:parseInt(codeBox.value)}), token);
    if (result.ok) {
        const entity = result.data;
        if (entity != null && entity.employeeId > 0) {
            nameBox.value = entity.fullName;
            return entity;
        } else {
            codeBox.value = "";
            nameBox.value = "";
            openMsgDialog("msg-dialog", "コードが登録されていません", 'red');
            setFocusElement("msg-dialog", codeBox);
            return null;
        }
    }
}