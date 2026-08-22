"use strict"

import { initCommon } from "../../../bootstrap/initPage.js";
import { initPageCache } from "../../../bootstrap/initPageCache.js";
import { createOrderListColumns } from "./columns.js";
import { registerController } from "../../../applcation/controllerRegistry.js";
import { initParentChildLink } from "../../../util/link.js";
import { getToday } from "../../../util/time.js";
import { toExclusiveDate } from "../../../util/date.js";
import { OrderRepository } from "../../../repositories/business/order/OrderRepository.js";
import { createOrderPage } from "../createOrderPage.js";
import { getController } from "../../../applcation/controllerRegistry.js";
import { filterFactory } from "../../../util/filterFactory.js";

export async function init() {
    await initCommon();
    await initPageCache("/api/order/init/cache");

    // tab1
    const list = orderListPage();
    registerController("orderList", list);
    list.init();
    await list.executeAction("search");

    initParentChildLink();
}

export const orderListPage = () =>
    createOrderPage({
        key: "orderList",
        components: {combo: true, input: true},
        tableId: "table-01",
        footerId: "footer-01",
        formId: "form-01",
        columns: createOrderListColumns(),
        submitText: "保存",
        cancelText: "キャンセル",
        onInit: () => {
            const today = getToday();
            const from = document.getElementById("date-from01");
            const to = document.getElementById("date-to01");
            if(from && !from.value){
                from.value = today;
            }
            if(to && !to.value){
                to.value = today;
            }
        },
        buildParams: () => {
            const cate = document.getElementById("category01")?.value;
            const from = document.getElementById("date-from01")?.value;
            const to = document.getElementById("date-to01")?.value;
            return {
                state: APP.cache.common.state.INITIAL,
                compState: APP.cache.common.state.COMPLETE,
                category: cate,
                dateFrom: from,
                dateTo: toExclusiveDate(to)
            };
        },
        buildCsvParams: () => ({
            state: APP.cache.common.state.INITIAL
        }),
        actions: {
            search: async (controller) => {await controller.refresh();},
            orderChanged: async (controller) => {await controller.refresh();}
        },
    });