// "use strict"

// import { updateTableDisplay } from "./tableRender";

// export async function fetchTableData(url) {
//     const res = await fetch(url);
//     const data = await res.json();
//     return data.data ?? [];
// }

// export async function refreshTableDisplay(config) {
//     if (!config) return;
//     const data = await fetchTableData(config.apiUrl);
//     await updateTableDisplay(
//         config.tableId,
//         config.footerId,
//         config.searchId,
//         data,
//         config.createTableContent
//     );
// }