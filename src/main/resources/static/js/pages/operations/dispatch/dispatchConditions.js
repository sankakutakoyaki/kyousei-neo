"use strict";

export function getDispatchConditions(root) {
    return {
        workDate: root.querySelector("#dispatch-work-date")?.value ?? "",
        officeId: Number(root.querySelector("#dispatch-office")?.value || 0),
        dispatchCategory: Number(root.querySelector("#dispatch-category")?.value || 0)
    };
}
export const hasDispatchConditions = ({workDate, officeId, dispatchCategory}) =>
    Boolean(workDate && officeId && dispatchCategory);
