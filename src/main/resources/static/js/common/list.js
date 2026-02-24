"use strict"

/**
 * シングルクリックのリストアイテムを作成する
 * @param {*} fileName 
 * @returns 
 */
// function createListItemWithSelection(id, fileName, area = document) {
//     const li = document.createElement('li');
//     li.dataset.id = id;
//     li.textContent = fileName;

//     li.addEventListener('click', () => {
//         // すでに選択されているなら解除して return
//         if (li.classList.contains('selected')) {
//             li.classList.remove('selected');
//             return;
//         }

//         // 他のliから選択クラスを外す
//         area.querySelectorAll('.normal-list>li').forEach(el => el.classList.remove('selected'));

//         // このliに選択クラスを追加
//         li.classList.add('selected');

//         if (funcName != null) () => funcName();
//     });

//     return li;
// }
function createListItemWithSelection(
    id,
    {
        area,
        onSecondClick = null,
        onDoubleClick = null
    }
) {
    const li = document.createElement('li');
    li.dataset.id = id;

    let clickTimer = null;

    li.addEventListener('click', (e) => {

        if (clickTimer) return;

        clickTimer = setTimeout(() => {

            // 既に選択されている場合 → 再クリック
            if (li.classList.contains('selected')) {
                if (onSecondClick) onSecondClick(li);
            } else {

                area.querySelectorAll('.file-item.selected')
                    .forEach(el => el.classList.remove('selected'));

                li.classList.add('selected');
            }

            clickTimer = null;

        }, 250); // ← ダブルクリック判定待ち
    });

    li.addEventListener('dblclick', () => {

        // clickのタイマーをキャンセル
        if (clickTimer) {
            clearTimeout(clickTimer);
            clickTimer = null;
        }

        if (onDoubleClick) onDoubleClick(li);
    });

    return li;
}

/**
 * クリックした時にファイルを新しいタブで表示するリストアイテムを作成する
 * @param {*} pdfUrl 
 * @param {*} fileName 
 * @returns 
 */
function createListItem(fileUrl, fileName) {
    const li = document.createElement('li');
    li.textContent = fileName;
    li.style.cursor = 'pointer';
    li.addEventListener('dblclick', () => {
        window.open(fileUrl, '_blank');
    });

    return li;
}

/**
 * リストの削除ボタンを作成する
 * @returns 
 */
function createRemoveBtn() {
    const removeBtn = document.createElement('button');
    removeBtn.innerHTML = '✖️';
    removeBtn.className = 'remove-btn';

    return removeBtn;
}

/**
 * ファイルの削除処理を登録する
 * @param {*} removeBtn 
 * @param {*} li 
 * @param {*} path 
 * @param {*} url 
 * @returns 
 */
function setFileRemoveEventListner(removeBtn, li, path, url) {
    removeBtn.addEventListener('click', async (e) => {
        e.stopPropagation(); // liの他のイベントが誤動作しないように
        const result = await deleteFiles(path, url);
        if (result.success) {
            li.remove();
        }
    });
    return removeBtn;
}

/**
 * プレースホルダー表示の更新
 * @param {*} list 
 */
function updatePlaceholder(list) {
    if (list.children.length === 0) {
        list.classList.add('placeholder');
    } else {
        list.classList.remove('placeholder');
    }
}

/**
 * 1回目のクリックで選択、2回目のクリックで編集
 * @param {*} file 
 * @param {*} config 
 * @returns 
 */
async function startInlineEdit(file, config) {

    const li = document.querySelector(`li[data-id="${file.fileId}"]`);
    if (!li) return;

    const nameSpan = li.querySelector("span");
    if (!nameSpan) {
        finish(true);
        return;
    }

    const originalName = file.displayName;

    // すでにinputが存在するなら何もしない
    if (li.querySelector("input")) {
        finish(true);
        return;
    }

    const input = document.createElement("input");
    input.type = "text";
    input.value = originalName;
    input.className = "inline-edit-input";

    const finish = async (commit) => {

        if (!commit) {
            nameSpan.textContent = originalName;
            input.replaceWith(nameSpan);
            return;
        }

        const newName = input.value.trim();

        if (!newName || newName === originalName) {
            nameSpan.textContent = originalName;
            input.replaceWith(nameSpan);
            return;
        }

        try {
            await fetch(`/api/files/file/${file.fileId}/rename`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": config.csrfToken
                },
                body: JSON.stringify({
                    displayName: newName
                })
            });

            file.displayName = newName;
            nameSpan.textContent = newName;

        } catch (e) {
            console.error("Rename failed", e);
            nameSpan.textContent = originalName;
        }

        input.replaceWith(nameSpan);
    };

    // Enter確定
    input.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            finish(true);
        }
        if (e.key === "Escape") {
            finish(false);
        }
    });

    // フォーカス外れ確定
    input.addEventListener("blur", () => finish(true));

    nameSpan.replaceWith(input);
    input.focus();
    input.select();
}