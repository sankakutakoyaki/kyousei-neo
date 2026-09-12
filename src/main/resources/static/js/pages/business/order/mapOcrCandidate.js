"use strict";

export function mapOcrCandidate(entry) {
    const c = entry.candidates;
    const numberText = value => String(value ?? "").normalize("NFKC").replace(/,/g, "").trim();
    const rows = (value, fields) => {
        const parsed = typeof value === "string" ? JSON.parse(value || "[]") : value ?? [];
        if (!Array.isArray(parsed) || parsed.some(row => !row || typeof row !== "object"))
            throw new Error("商品・作業項目の読取形式が不正です。");
        return parsed.map(row => Object.fromEntries(fields.map(key => [key, /Quantity$|Price$/.test(key) ? numberText(row[key]) : row[key] ?? ""])));
    };
    const date = String(c.requestedDate ?? "").trim();
    const parsedDate = /^\d{4}-\d{2}-\d{2}$/.test(date) && !Number.isNaN(Date.parse(date)) && new Date(date).toISOString().slice(0, 10) === date ? date : "";
    let items = rows(c.items, ["itemName", "itemModel", "itemQuantity"]);
    if (!items.length) items = [c.itemModel1, c.itemModel2].filter(Boolean).map(itemModel => ({itemModel, itemQuantity: ""}));
    return {
        ocrLogId: entry.ocrLogId, orderImportId: entry.orderImportId,
        primeConstractorId: entry.shipperId,
        requestNumber: c.requestNumber ?? "", title: c.customerName ?? "",
        postalCode: c.postalCode ?? "", fullAddress: c.address ?? "",
        contactInformation: c.mobilePhone ?? "", remarks: c.contactNote ?? "",
        visitDate: parsedDate, visitTime: c.visitTime ?? "",
        ocrDateWarning: date && !parsedDate ? `日付の読取値：${date}。年を含めて日付を確認してください。` : "",
        items, works: rows(c.works, ["orderWorkName", "orderWorkQuantity", "orderWorkPrice"])
    };
}
