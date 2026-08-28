// "use strict";

// import { setInertState } from "../core/ui/dialog/dialogCore.js";

// let onScanCallback = null;
// let mediaStream = null;
// let animationFrameId = null;
// let detector = null;
// let scanning = false;

// export const BarcodeScanner = {

//     async open(options = {}) {

//         const {
//             onScan = () => {}
//         } = options;

//         onScanCallback = onScan;

//         const area = document.getElementById(
//             "barcode-dialog-area"
//         );

//         const dialog = document.getElementById(
//             "barcode-dialog"
//         );

//         const video = document.getElementById(
//             "barcode-scanner-video"
//         );

//         if (!area || !dialog || !video) {
//             console.error(
//                 "JANスキャンに必要な要素が見つかりません。"
//             );
//             return;
//         }

//         area.classList.add("dialog");
//         dialog.classList.remove("none");

//         setInertState(true);

//         // ×ボタン
//         const closeBtn = dialog.querySelector(
//             '[name="closeBtn"]'
//         );

//         // キャンセルボタン
//         const cancelBtn = dialog.querySelector(
//             '[name="cancelBtn"]'
//         );

//         if (closeBtn) {
//             closeBtn.onclick = () => {
//                 BarcodeScanner.close();
//             };
//         }

//         if (cancelBtn) {
//             cancelBtn.onclick = () => {
//                 BarcodeScanner.close();
//             };
//         }

//         // カメラ起動
//         try {

//             mediaStream =
//                 await navigator.mediaDevices.getUserMedia({
//                     video: {
//                         facingMode: {
//                             ideal: "environment"
//                         }
//                     },
//                     audio: false
//                 });

//             video.srcObject = mediaStream;

//             await video.play();

//             // JANコード認識開始
//             BarcodeScanner.startDetection(video);

//         } catch (error) {

//             console.error(
//                 "カメラの起動に失敗しました。",
//                 error
//             );

//             const message =
//                 document.getElementById(
//                     "barcode-scanner-message"
//                 );

//             if (message) {
//                 message.textContent =
//                     "カメラを起動できませんでした。カメラの使用を許可してください。";
//             }

//             BarcodeScanner.close();
//         }
//     },

//     startDetection(video) {

//         // BarcodeDetector が使えるか確認
//         if (!("BarcodeDetector" in window)) {

//             console.warn(
//                 "このブラウザは BarcodeDetector に対応していません。"
//             );

//             const message =
//                 document.getElementById(
//                     "barcode-scanner-message"
//                 );

//             if (message) {
//                 message.textContent =
//                     "このブラウザではJANコードの自動認識に対応していません。";
//             }

//             return;
//         }

//         detector = new BarcodeDetector({
//             formats: [
//                 "ean_13"
//             ]
//         });

//         scanning = true;

//         const detect = async () => {

//             if (!scanning) {
//                 return;
//             }

//             if (
//                 video.readyState >=
//                 HTMLMediaElement.HAVE_ENOUGH_DATA
//             ) {

//                 try {

//                     const barcodes =
//                         await detector.detect(video);

//                     if (barcodes.length > 0) {

//                         const code =
//                             barcodes[0].rawValue;

//                         console.log(
//                             "JANコード:",
//                             code
//                         );

//                         // 読み取り停止
//                         scanning = false;

//                         // 呼び出し元へ返す
//                         if (onScanCallback) {
//                             onScanCallback(code);
//                         }

//                         // ダイアログを閉じる
//                         BarcodeScanner.close();

//                         return;
//                     }

//                 } catch (error) {

//                     console.error(
//                         "JANコードの認識に失敗しました。",
//                         error
//                     );
//                 }
//             }

//             animationFrameId =
//                 requestAnimationFrame(detect);
//         };

//         detect();
//     },

//     close() {

//         // バーコード認識停止
//         scanning = false;

//         if (animationFrameId) {

//             cancelAnimationFrame(
//                 animationFrameId
//             );

//             animationFrameId = null;
//         }

//         // カメラ停止
//         if (mediaStream) {

//             mediaStream
//                 .getTracks()
//                 .forEach(track => track.stop());

//             mediaStream = null;
//         }

//         const video = document.getElementById(
//             "barcode-scanner-video"
//         );

//         if (video) {
//             video.srcObject = null;
//         }

//         const area = document.getElementById(
//             "barcode-dialog-area"
//         );

//         const dialog = document.getElementById(
//             "barcode-dialog"
//         );

//         if (area) {
//             area.classList.remove("dialog");
//         }

//         if (dialog) {
//             dialog.classList.add("none");
//         }

//         setInertState(false);

//         onScanCallback = null;
//         detector = null;
//     }

// };

"use strict";

export const BarcodeScanner = {

    video: null,
    codeReader: null,
    controls: null,
    onScan: null,

    async open(options = {}) {

        this.video =
            document.getElementById("barcode-video");

        if (!this.video) {
            console.error(
                "barcode-video がありません"
            );
            return;
        }

        /*
         * 読み取り対象
         *
         * デフォルト：JANコード
         *
         * 将来：
         *   QR_CODE
         *   EAN_8
         *   CODE_128
         *   CODE_39
         *   ITF
         *   など
         */
        const formats =
            options.formats ?? ["EAN_13"];

        this.onScan =
            options.onScan ?? (() => {});

        try {

            this.codeReader =
                new ZXing.BrowserMultiFormatReader();

            /*
             * ZXingのフォーマットへ変換
             */
            const zxingFormats =
                formats.map(format => {

                    switch (format) {

                        case "EAN_13":
                            return ZXing.BarcodeFormat.EAN_13;

                        case "EAN_8":
                            return ZXing.BarcodeFormat.EAN_8;

                        case "QR_CODE":
                            return ZXing.BarcodeFormat.QR_CODE;

                        case "CODE_128":
                            return ZXing.BarcodeFormat.CODE_128;

                        case "CODE_39":
                            return ZXing.BarcodeFormat.CODE_39;

                        case "ITF":
                            return ZXing.BarcodeFormat.ITF;

                        default:
                            throw new Error(
                                `未対応のバーコード形式です: ${format}`
                            );
                    }
                });

            this.codeReader.possibleFormats =
                zxingFormats;

            /*
             * カメラ一覧
             */
            const devices =
                await ZXing.BrowserCodeReader
                    .listVideoInputDevices();

            if (!devices.length) {
                throw new Error(
                    "カメラが見つかりません。"
                );
            }

            /*
             * 背面カメラを優先
             */
            let deviceId =
                devices[0].deviceId;

            const backCamera =
                devices.find(device =>
                    /back|rear|environment/i.test(
                        device.label
                    )
                );

            if (backCamera) {
                deviceId =
                    backCamera.deviceId;
            }

            console.log(
                "使用カメラ:",
                deviceId
            );

            /*
             * 読み取り開始
             */
            this.controls =
                await this.codeReader
                    .decodeFromVideoDevice(
                        deviceId,
                        this.video,
                        (result, error) => {

                            if (!result) {
                                return;
                            }

                            const code =
                                result.getText();

                            console.log(
                                "読み取り:",
                                code
                            );

                            this.complete(code);
                        }
                    );

        } catch (error) {

            console.error(
                "バーコードスキャナー起動エラー",
                error
            );

            alert(
                "バーコード読み取りを開始できませんでした。\n" +
                error.message
            );

            this.close();
        }
    },

    complete(code) {

        /*
         * 二重読み取り防止
         */
        const callback =
            this.onScan;

        this.close();

        callback?.(code);
    },

    close() {

        /*
         * 読み取り停止
         */
        if (this.controls) {

            this.controls.stop();

            this.controls = null;
        }

        /*
         * ZXing停止
         */
        if (this.codeReader) {

            this.codeReader.reset();

            this.codeReader = null;
        }

        /*
         * video停止
         */
        if (this.video) {

            this.video.pause();

            this.video.srcObject = null;
        }

        /*
         * ダイアログを閉じる
         */
        const dialog =
            document.getElementById(
                "barcode-scan-dialog"
            );

        dialog?.classList.add("none");

        this.onScan = null;
    }
};