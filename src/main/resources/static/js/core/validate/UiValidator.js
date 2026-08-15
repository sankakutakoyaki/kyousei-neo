"use strict"

import { validate, hasRequiredInput } from "../form/components/check.js";

// export const UiValidator = {
//     validate(form){
//         validate(form);
//     }
// };
export const UiValidator = {
    validate(form){
        validate(form);
    },

    hasRequiredInput(form, selector = null){
        return hasRequiredInput(form, selector);
    }
};