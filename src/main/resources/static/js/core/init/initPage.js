"use strict"

import { initTable } from "./initTable.js";
import { initCombo } from "./initCombo.js";
import { DataResolver } from "../data/DataResolver.js"
import { setEnterFocus } from "../ui/enterfocus.js";
import { initClick } from "./initClick.js";
import { initChange } from "./initChange.js";
import { initInput } from "./initInput.js";
import { initFocus } from "./initFocus.js";
import { initControllers } from "./initControllers.js";

export async function initPage(){

    initControllers();
    initTable();

    initCombo();
    initClick();
    initChange();
    initInput();
    initFocus();

    setEnterFocus();
    DataResolver.init();
}