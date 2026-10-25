"use strict";

import { handleTab } from "./tabActions.js";
import { handleDateMove, handleDateArrowMove } from "./dateActions.js";
import { handleBarcodeScan } from "./barcodeAction.js";
import { handlePhone, handleMap } from "./linkActions.js";

export const domActions = {
    tab: handleTab,
    "date-move": handleDateMove,
    "date-arrow": handleDateArrowMove,
    "barcode-scan": handleBarcodeScan,
    tel: handlePhone,
    map: handleMap
};