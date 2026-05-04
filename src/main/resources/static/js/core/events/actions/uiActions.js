"use strict"

import { handleTab } from "./tabActions.js";
import { handleDateMove } from "./dateActions.js";
import { handleDateArrowMove } from "./dateActions.js";
export const uiActions = {
    tab: handleTab,
    "date-move": handleDateMove,
    "date-arrow": handleDateArrowMove
};