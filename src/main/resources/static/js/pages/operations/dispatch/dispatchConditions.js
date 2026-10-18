"use strict";

export function getDispatchConditions(root) {
    const officeId = Number(root.querySelector("#dispatch-office")?.value || 0);
    const orderOfficeValue = root.querySelector("#dispatch-order-office")?.value;

    return {
        workDate: root.querySelector("#dispatch-work-date")?.value ?? "",
        officeId,
        orderOfficeId: orderOfficeValue ? Number(orderOfficeValue) : officeId,
        dispatchCategory: Number(root.querySelector("#dispatch-category")?.value || 0)
    };
}

export const hasDispatchConditions = ({workDate, officeId, dispatchCategory}) => Boolean(workDate && officeId && dispatchCategory);