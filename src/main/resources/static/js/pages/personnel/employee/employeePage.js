"use strict"

export async function init() {

    await initCommon();
    await initPageCache("/api/employee/init/cache");

    // tab1
    const fulltime = fulltimeEmployeePage();
    registerController("fulltimeEmployee", fulltime);

    fulltime.init({
        columns: createEmployeeColumns(fulltime),
        data: [],
        components: { combo: true }
    });
    await fulltime.dataTable.refresh();

    // tab2
    const parttime = parttimeEmployeePage();
    registerController("parttimeEmployee", parttime);

    parttime.init({
        columns: createEmployeeColumns(parttime),
        data: [],
        components: { combo: true, input: true }
    });
    await employee.dataTable.refresh();
}

export const fulltimeEmployeePage = () => {

    const controller = new PageController({
        key: "fulltime",

        table: {
            create: (controller, columns) => new DataTable({
                controller: controller,
                tableId: "table-01",
                footerId: "footer-01",
                columns,
                idKey: "employeeId",
                checkable: true,
                buildParams: () => ({
                    state: APP.cache.common.state.INITIAL,
                    category: APP.cache.common.employeeCategory.FULLTIME,
                }),
                buildCsvParams: () => ({
                    state: APP.cache.common.state.INITIAL
                }),
                api: {
                    request: api.request,
                    select: "employeeList",
                    delete: "employeeDeleteByIds",
                    download: "employeeCsv"
                },
                model: {
                    filters: {
                        officeId: filterFactory.equals("officeId")
                    }
                },
                onDoubleClick: (item) => controller.openEdit(item.employeeId),
            })
        },
        form: {
            create: (controller) => new FormController({
                formId: "form-01",
                key: controller.key,
                controller: controller, 
                beforeSave: (payload, form) => {
                    const key = form.dataset.key;
                    const id = payload[key];
                    if (!id || Number(id) === 0) {
                        payload.category = APP.cache.common.employeeCategory.FULLTIME;
                    }
                },
                onSaved: async (id) => {
                    await controller.dataTable.refresh();
                    controller.scrollToRow(id);
                },
                buildParams: (id) => ({
                    state: APP.cache.common.state.INITIAL,
                    employeeId: id                   
                }),
                api: {
                    request: api.request,
                    find: "employeeDetail",
                    save: "employeeSave"
                }
            })
        }
    });
    return controller;
};

export const parttimeEmployeePage = () => {

    const controller = new PageController({
        key: "parttime",

        table: {
            create: (controller, columns) => new DataTable({
                controller: controller,
                tableId: "table-02",
                footerId: "footer-02",
                columns,
                idKey: "employeeId",
                checkable: true,
                buildParams: () => ({
                    state: APP.cache.common.state.INITIAL,
                    category: APP.cache.common.employeeCategory.PARTTIME,
                }),
                buildCsvParams: () => ({
                    state: APP.cache.common.state.INITIAL
                }),
                api: {
                    request: api.request,
                    select: "employeeList",
                    delete: "employeeDeleteByIds",
                    download: "employeeCsv"
                },
                model: {
                    filters: {
                        officeId: filterFactory.equals("officeId")
                    }
                },
                onDoubleClick: (item) => controller.openEdit(item.employeeId),
            })
        },
        form: {
            create: (controller) => new FormController({
                formId: "form-01",
                key: controller.key,
                controller: controller, 
                beforeSave: (payload, form) => {
                    const key = form.dataset.key;
                    const id = payload[key];
                    if (!id || Number(id) === 0) {
                        payload.category = APP.cache.common.employeeCategory.PARTTIME;
                    }
                },
                onSaved: async (id) => {
                    await controller.dataTable.refresh();
                    controller.scrollToRow(id);
                },
                buildParams: (id) => ({
                    state: APP.cache.common.state.INITIAL,
                    employeeId: id                   
                }),
                api: {
                    request: api.request,
                    find: "employeeDetail",
                    save: "employeeSave"
                }
            })
        }
    });
    return controller;
};