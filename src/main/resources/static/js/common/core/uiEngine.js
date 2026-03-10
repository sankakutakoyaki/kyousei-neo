"use strict"

import { LookupEngine } from "./lookupEngine.js";

export const UiEngine = {

    init() {

        LookupEngine.init();
        this.enterFocus();

    },

    enterFocus() {

        const inputs = document.querySelectorAll("[data-enter-focus]");

        inputs.forEach(input => {

            input.addEventListener("keydown", (e) => {

                if (e.key !== "Enter") return;

                const target = document.querySelector(
                    `[data-focus="${input.dataset.enterFocus}"]`
                );

                if (target) target.focus();

            });

        });

    }

};