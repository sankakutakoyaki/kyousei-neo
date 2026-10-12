"use strict";

export function cloneTemplate(id, root = document) {
    const template = root.querySelector(`#${id}`);
    const element = template?.content?.firstElementChild;
    if(!element) throw new Error(`表示用テンプレートが見つかりません: ${id}`);
    return element.cloneNode(true);
}
