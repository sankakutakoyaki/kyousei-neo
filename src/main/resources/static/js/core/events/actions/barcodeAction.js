"use strict"

import { BarcodeScanner } from "../../../util/barcodeScanner.js";
import { executeEnter } from "./enterAction.js";

export function handleBarcodeScan(e, el) {
    const targetId = el.dataset.barcodeTarget;
    const target = document.getElementById(targetId);
    if (!target) {
        console.warn("barcode target not found:", targetId);
        return;
    }

    const formatName = el.dataset.barcodeFormat ?? "EAN_13";
    const format = ZXingBrowser.BarcodeFormat[formatName];
    if (!format) {
        console.warn("barcode format not found:", formatName);
        return;
    }

    BarcodeScanner.open({
        formats: [format],
        onScan: code => {
            // 読み取った値をセット
            target.value = code;
            executeEnter(target);
        }
    });
}