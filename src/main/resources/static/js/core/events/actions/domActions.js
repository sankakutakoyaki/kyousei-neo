"use strict"

import { handleTab } from "./tabActions.js";
import { handleDateMove, handleDateArrowMove } from "./dateActions.js";
import { BarcodeScanner } from "../../../util/barcodeScanner.js";
import { handleRecycleBarcode } from "./recycleAction.js";

export const domActions = {
    tab: handleTab,
    "date-move": handleDateMove,
    "date-arrow": handleDateArrowMove,
    "barcode-scan": (e, el) => {

        const targetId =
            el.dataset.barcodeTarget;

        const target =
            document.getElementById(targetId);

        if (!target) {
            console.warn(
                "barcode target not found:",
                targetId
            );
            return;
        }
        
        const formatName =
            el.dataset.barcodeFormat ?? "EAN_13";

        const format =
            ZXingBrowser.BarcodeFormat[formatName];

        BarcodeScanner.open({

            // formats: [
            //     // ZXingBrowser.BarcodeFormat.EAN_13
            //     ZXingBrowser.BarcodeFormat.CODABAR
            // ],
            formats: [format],

            onScan: code => {

                // // リサイクル券用に加工
                // const value = handleRecycleBarcode(code);

                // // 読み取った値をセット
                // target.value = value;
                target.value = code;

                // 入力欄へフォーカス
                target.focus();

                // 通常入力と同じ input イベント
                target.dispatchEvent(
                    new Event("input", {
                        bubbles: true
                    })
                );

                // Enter相当のイベント
                target.dispatchEvent(
                    new KeyboardEvent("keydown", {
                        key: "Enter",
                        code: "Enter",
                        keyCode: 13,
                        which: 13,
                        bubbles: true,
                        cancelable: true
                    })
                );
            }
        });
    }
};

