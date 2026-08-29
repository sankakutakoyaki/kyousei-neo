"use strict"

export const BarcodeScanner = {

    video: null,
    codeReader: null,
    controls: null,
    onScan: null,
    completed: false,

    // ============================================================
    // スキャナーダイアログ作成
    // ============================================================
    init() {

        // すでに存在するなら何もしない
        if (document.getElementById("barcode-scan-dialog")) {
            return;
        }

        const area = document.createElement("div");

        area.id = "barcode-dialog-area";

        area.innerHTML = `
            <div
                id="barcode-scan-dialog"
                class="barcode-scan-dialog none">

                <div class="barcode-scan-content">

                    <video
                        id="barcode-video"
                        autoplay
                        playsinline
                        muted>
                    </video>

                    <div class="barcode-scan-frame"></div>

                    <div id="barcode-scan-message">
                        JANコードをカメラに映してください
                    </div>

                    <button
                        type="button"
                        id="barcode-scan-cancel">
                        キャンセル
                    </button>

                </div>
            </div>
        `;

        document.body.appendChild(area);

        // キャンセル
        document
            .getElementById("barcode-scan-cancel")
            ?.addEventListener("click", () => {
                this.close();
            });
    },

    async open(options = {}) {

        // ダイアログがなければ作成
        this.init();

        this.video =
            document.getElementById("barcode-video");

        if (!this.video) {
            console.error(
                "barcode-video がありません"
            );
            return;
        }

        // ダイアログ表示
        const dialog =
            document.getElementById(
                "barcode-scan-dialog"
            );

        dialog?.classList.remove("none");

        this.onScan =
            options.onScan ?? (() => {});

        this.completed = false;

        const formats =
            options.formats ?? [
                // ZXingBrowser.BarcodeFormat.EAN_13
                ZXingBrowser.BarcodeFormat.CODABAR
            ];

        try {

            this.codeReader =
                new ZXingBrowser.BrowserMultiFormatReader();

            this.codeReader.possibleFormats =
                formats;
console.log("★★ 読み取り形式 ★★", formats);
            this.controls =
                await this.codeReader.decodeFromConstraints(
                    {
                        video: {
                            facingMode: {
                                ideal: "environment"
                            }
                        },
                        audio: false
                    },
                    this.video,
                    (result, error) => {

                        if (this.completed) {
                            return;
                        }

                        if (!result) {
                            return;
                        }

                        const code =
                            result.getText();

                        console.log(
                            "読み取り:",
                            code
                        );

                        this.completed = true;

                        setTimeout(() => {
                            this.complete(code);
                        }, 0);
                    }
                );

        } catch (error) {

            console.error(
                "バーコードスキャナー起動エラー",
                error
            );

            alert(
                "バーコード読み取りを開始できませんでした。\n"
                + error.message
            );

            this.close();
        }
    },

    complete(code) {

        if (!this.completed) {
            return;
        }

        const callback = this.onScan;

        this.close();

        if (callback) {
            callback(code);
        }
    },

    close() {

        this.completed = true;

        if (this.controls) {

            try {
                this.controls.stop();
            } catch (e) {
                console.warn(
                    "ZXing停止時のエラー:",
                    e
                );
            }

            this.controls = null;
        }

        this.codeReader = null;

        if (this.video) {

            const stream =
                this.video.srcObject;

            if (stream) {
                stream
                    .getTracks()
                    .forEach(track => {
                        try {
                            track.stop();
                        } catch (e) {
                            console.warn(
                                "カメラ停止エラー:",
                                e
                            );
                        }
                    });
            }

            this.video.pause();
            this.video.srcObject = null;
        }

        const dialog =
            document.getElementById(
                "barcode-scan-dialog"
            );

        if (dialog) {
            dialog.classList.add("none");
        }

        this.onScan = null;
    }
};

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
