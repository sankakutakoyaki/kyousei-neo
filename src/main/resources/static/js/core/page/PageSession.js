"use strict";

export function createPageSession(root) {
    if(!root) throw new Error("ページの表示領域が見つかりません。");
    let disposed = false;
    const cleanups = [];
    return {
        root,
        isActive: () => !disposed && root.isConnected,
        byId: id => root.querySelector(`#${id}`),
        onDispose: cleanup => cleanups.push(cleanup),
        dispose() {
            if(disposed) return;
            disposed = true;
            cleanups.splice(0).forEach(cleanup => cleanup());
        }
    };
}
