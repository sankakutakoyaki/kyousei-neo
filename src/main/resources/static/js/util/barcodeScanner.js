"use strict";

import { setInertState } from "../core/ui/dialog/dialogCore.js";

let onScanCallback = null;
let mediaStream = null;
let animationFrameId = null;
let detector = null;
let scanning = false;

export const BarcodeScanner = {

    async open(options = {}) {

        const {
            onScan = () => {}
        } = options;

        onScanCallback = onScan;

        const area = document.getElementById(
            "barcode-dialog-area"
        );

        const dialog = document.getElementById(
            "barcode-dialog"
        );

        const video = document.getElementById(
            "barcode-scanner-video"
        );

        if (!area || !dialog || !video) {
            console.error(
                "JANスキャンに必要な要素が見つかりません。"
            );
            return;
        }

        area.classList.add("dialog");
        dialog.classList.remove("none");

        setInertState(true);

        // ×ボタン
        const closeBtn = dialog.querySelector(
            '[name="closeBtn"]'
        );

        // キャンセルボタン
        const cancelBtn = dialog.querySelector(
            '[name="cancelBtn"]'
        );

        if (closeBtn) {
            closeBtn.onclick = () => {
                BarcodeScanner.close();
            };
        }

        if (cancelBtn) {
            cancelBtn.onclick = () => {
                BarcodeScanner.close();
            };
        }

        // カメラ起動
        try {

            mediaStream =
                await navigator.mediaDevices.getUserMedia({
                    video: {
                        facingMode: {
                            ideal: "environment"
                        }
                    },
                    audio: false
                });

            video.srcObject = mediaStream;

            await video.play();

            // JANコード認識開始
            BarcodeScanner.startDetection(video);

        } catch (error) {

            console.error(
                "カメラの起動に失敗しました。",
                error
            );

            const message =
                document.getElementById(
                    "barcode-scanner-message"
                );

            if (message) {
                message.textContent =
                    "カメラを起動できませんでした。カメラの使用を許可してください。";
            }

            BarcodeScanner.close();
        }
    },

    startDetection(video) {

        // BarcodeDetector が使えるか確認
        if (!("BarcodeDetector" in window)) {

            console.warn(
                "このブラウザは BarcodeDetector に対応していません。"
            );

            const message =
                document.getElementById(
                    "barcode-scanner-message"
                );

            if (message) {
                message.textContent =
                    "このブラウザではJANコードの自動認識に対応していません。";
            }

            return;
        }

        detector = new BarcodeDetector({
            formats: [
                "ean_13"
            ]
        });

        scanning = true;

        const detect = async () => {

            if (!scanning) {
                return;
            }

            if (
                video.readyState >=
                HTMLMediaElement.HAVE_ENOUGH_DATA
            ) {

                try {

                    const barcodes =
                        await detector.detect(video);

                    if (barcodes.length > 0) {

                        const code =
                            barcodes[0].rawValue;

                        console.log(
                            "JANコード:",
                            code
                        );

                        // 読み取り停止
                        scanning = false;

                        // 呼び出し元へ返す
                        if (onScanCallback) {
                            onScanCallback(code);
                        }

                        // ダイアログを閉じる
                        BarcodeScanner.close();

                        return;
                    }

                } catch (error) {

                    console.error(
                        "JANコードの認識に失敗しました。",
                        error
                    );
                }
            }

            animationFrameId =
                requestAnimationFrame(detect);
        };

        detect();
    },

    close() {

        // バーコード認識停止
        scanning = false;

        if (animationFrameId) {

            cancelAnimationFrame(
                animationFrameId
            );

            animationFrameId = null;
        }

        // カメラ停止
        if (mediaStream) {

            mediaStream
                .getTracks()
                .forEach(track => track.stop());

            mediaStream = null;
        }

        const video = document.getElementById(
            "barcode-scanner-video"
        );

        if (video) {
            video.srcObject = null;
        }

        const area = document.getElementById(
            "barcode-dialog-area"
        );

        const dialog = document.getElementById(
            "barcode-dialog"
        );

        if (area) {
            area.classList.remove("dialog");
        }

        if (dialog) {
            dialog.classList.add("none");
        }

        setInertState(false);

        onScanCallback = null;
        detector = null;
    }

};