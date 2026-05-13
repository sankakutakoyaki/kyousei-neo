"use strict"

import { handleTab } from "./tabActions.js";
import { handleDateMove, handleDateArrowMove } from "./dateActions.js";

export const domActions = {
    tab: handleTab,
    "date-move": handleDateMove,
    "date-arrow": handleDateArrowMove
};