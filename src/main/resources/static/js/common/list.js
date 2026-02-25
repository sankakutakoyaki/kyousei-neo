"use strict"

"use strict";

/**
 * 1回クリックで選択
 * 2回目クリックで編集
 * ダブルクリックで開く
 */
function createListItemWithSelection(
    id,
    {
        area,
        onSecondClick = null,
        onDoubleClick = null
    }
) {
    const li = document.createElement("li");
    li.dataset.id = id;

    let clickTimer = null;

    li.addEventListener('click', (e) => {

        // 🔥 span/input以外をクリックしたら編集中inputを確定させる
        if (!e.target.closest("span") && !e.target.closest("input")) {
            const editingInput = li.querySelector("input.inline-edit-input");
            if (editingInput) {
                editingInput.blur(); // ← これでfinish(true)が走る
            }
        }

        if (clickTimer) return;

        clickTimer = setTimeout(() => {
            if (li.classList.contains('selected') && e.target.closest("span")) {
                if (onSecondClick) onSecondClick(li);
            } else {

                area.querySelectorAll('.file-item.selected')
                    .forEach(el => el.classList.remove('selected'));

                li.classList.add('selected');
            }

            clickTimer = null;

        }, 250);
    });

    li.addEventListener("dblclick", () => {

        if (clickTimer) {
            clearTimeout(clickTimer);
            clickTimer = null;
        }

        if (onDoubleClick) onDoubleClick(li);
    });

    return li;
}


/**
 * インライン編集（ファイル名変更）
 */
async function startInlineEdit(file, config) {

    const li = document.querySelector(`li[data-id="${file.fileId}"]`);
    if (!li) return;

    const nameSpan = li.querySelector(".file-name");
    if (!nameSpan) return;

    if (li.querySelector("input")) return;

    const originalName = file.displayName;

    const input = document.createElement("input");
    input.type = "text";
    input.value = originalName;
    input.className = "inline-edit-input";

    const finish = async (commit) => {

        const newName = input.value.trim();

        if (!commit || !newName || newName === originalName) {
            input.replaceWith(nameSpan);
            return;
        }

        try {
            await fetch(`/api/files/file/rename/${file.fileId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": config.csrfToken
                },
                body: JSON.stringify({ displayName: newName })
            });

            file.displayName = newName;
            nameSpan.textContent = newName;

        } catch (e) {
            console.error("Rename failed:", e);
        }

        input.replaceWith(nameSpan);
    };

    input.addEventListener("keydown", e => {
        if (e.key === "Enter") finish(true);
        if (e.key === "Escape") finish(false);
    });

    input.addEventListener("blur", () => finish(true));

    nameSpan.replaceWith(input);
    input.focus();
    input.select();
}