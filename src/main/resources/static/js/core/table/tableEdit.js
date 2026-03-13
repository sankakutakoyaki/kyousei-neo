"use strict"

// クリックしたTDを編集可能にする
export function tdEnableEdit(newRow) {
    newRow.addEventListener('click', function (e) {
        const td = e.target.closest('td.editable');
        if (!td) return;
        if (td.querySelector('input, select')) return;

        const originalText = td.textContent.trim();
        const originalValue = td.dataset.value; // select 用
        const editType = td.dataset.editType || 'text';

        let editor;
        let canceled = false; // ★ Esc 判定用

        if (editType === 'select') {
            editor = document.createElement('select');
            editor.classList.add('normal-input');

            const key = td.dataset.optionsKey;
            const options = SELECT_OPTIONS[key] || [];

            options.forEach(opt => {
                const option = document.createElement('option');
                option.value = opt.number;
                option.textContent = opt.text;
                if (String(opt.number) === originalValue) {
                    option.selected = true;
                }
                editor.appendChild(option);
            });
        } else {
            editor = document.createElement('input');
            editor.type = 'text';
            editor.classList.add('normal-input');
            editor.value = originalText === '-----' ? '' : originalText;
        }

        editor.style.width = '100%';
        td.textContent = '';
        td.appendChild(editor);
        editor.focus();

        if (editor instanceof HTMLInputElement) editor.select();

        // ===== イベント =====

        editor.addEventListener('keydown', e => {

            // 日本語変換中は無視
            if (e.isComposing) return;

            if (e.key === 'Escape') {
                e.preventDefault();
                canceled = true;
                restoreCell(td, originalText, originalValue);
                return;
            }

            if (e.key === 'Enter') {
                e.preventDefault();
                editor.blur();
            }
        });

        editor.addEventListener('change', () => {
            if (canceled) return;
            handleTdChange(editor);
            saveEditor(td, editor, originalText);
        });

        editor.addEventListener('blur', () => {
            if (canceled) return;
            saveEditor(td, editor, originalText);
        });
    });
}

export function restoreCell(td, text, value) {
    td.textContent = text || '-----';
    if (value !== undefined) {
        td.dataset.value = value;
    }
}

// セル復元用ヘルパー
export function saveEditor(td, editor, currentValue) {
    if (editor instanceof HTMLSelectElement) {
        const opt = editor.selectedOptions[0];
        td.dataset.value = opt.value;
        td.textContent = opt.textContent;
    } else {
        const value = editor.value?.trim();
        td.textContent = value || currentValue || '-----';
    }
}