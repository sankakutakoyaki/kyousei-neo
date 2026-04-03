"use strict"

// import { initCombo } from "./initCombo.js";
import { DataResolver } from "../behavior/DataResolver.js"
import { setEnterFocus } from "../ui/enterfocus.js";
import { initClick } from "./initClick.js";
import { initChange } from "./initChange.js";
import { initInput } from "./initInput.js";
import { initFocus } from "./initFocus.js";

export function initCommon(){
    
    initClick();
    initChange();
    initInput();
    
    initFocus();
    // initCombo();

    setEnterFocus();
    DataResolver.init();
}