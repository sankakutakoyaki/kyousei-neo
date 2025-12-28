"use strict"

/**
 * シングルクリックのリストアイテムを作成する
 * @param {*} fileName 
 * @returns 
 */
function createListItemWithSelection(id, fileName) {
    const li = document.createElement('li');
    li.dataset.id = id;
    li.textContent = fileName;

    li.addEventListener('click', () => {
        // すでに選択されているなら解除して return
        if (li.classList.contains('selected')) {
            li.classList.remove('selected');
            // updatePlaceholder();
            return;
        }

        // 他のliから選択クラスを外す
        document.querySelectorAll('.normal-list>li').forEach(el => el.classList.remove('selected'));

        // このliに選択クラスを追加
        li.classList.add('selected');
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
    li.addEventListener('click', () => {
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

    // // 削除処理を登録する
    // removeBtn.addEventListener('click', async (e) => {
    //     e.stopPropagation(); // liの他のイベントが誤動作しないように
    //     const result = await deleteFiles(url);
    //     if (result.number > 0) {
    //         li.remove();
    //     }
    //     // updatePlaceholder();
    // });

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