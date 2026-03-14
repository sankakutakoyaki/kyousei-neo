"use strict"

import {init as initCombo} from "../components/form/combo.js";
import {init as initSearch} from "../components/form/search.js";
import {init as initTab} from "../components/ui/tab.js";
import {init as initFocus} from "../components/form/enterfocus.js";
import {init as initTable} from "../components/form/table.js";
import {DataResolver} from "../core/data/DataResolver.js";

export async function initPage(config, area = document){

    // コンボボックス
    if (config.combo) {
        initCombo(area, config.combo);
    }

    // ID → NAME 解決
    if (config.resolve) {
        DataResolver.init(area);
    }

    // 検索ボックス
    if(config.search){
        initSearch(area, config.search);
    }

    // タブ
    if(config.tabs){
        initTab(area);
    }

    // フォーカス移動
    if(config.enterfocus){
        initFocus(area);
    }

    // テーブル
    if(config.tables) {
        await initTable(config.tables);
    }
}