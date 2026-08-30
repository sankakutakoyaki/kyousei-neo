"use strict"

import { handleTab } from "./tabActions.js";
import { handleDateMove, handleDateArrowMove } from "./dateActions.js";
import { BarcodeScanner } from "../../../util/barcodeScanner.js";
import { handleBarcodeScan } from "./barcodeAction.js";

export const domActions = {
    tab: handleTab,
    "date-move": handleDateMove,
    "date-arrow": handleDateArrowMove,
    "barcode-scan": handleBarcodeScan
};

