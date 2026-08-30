"use strict"

export const BarcodeScanner = {

    video: null,
    codeReader: null,
    controls: null,
    onScan: null,
    completed: false,

    // スキャナーダイアログ作成
    init() {
        // すでに存在するなら何もしない
        if (document.getElementById("barcode-scan-dialog")) {
            return;
        }

        const area = document.createElement("div");
        area.id = "barcode-dialog-area";
        area.innerHTML = `
            <div id="barcode-scan-dialog" class="barcode-scan-dialog none">
                <div class="barcode-scan-content">
                    <video id="barcode-video" autoplay playsinline muted></video>
                    <div class="barcode-scan-frame"></div>
                    <div id="barcode-scan-message">JANコードをカメラに映してください</div>
                    <button type="button" id="barcode-scan-cancel">キャンセル</button>
                </div>
            </div>
        `;
        document.body.appendChild(area);
        // キャンセル
        document.getElementById("barcode-scan-cancel")?.addEventListener("click", () => {
            this.close();
        });
    },

    async open(options = {}) {
        // ダイアログがなければ作成
        this.init();

        this.video = document.getElementById("barcode-video");
        if (!this.video) {
            console.error("barcode-video がありません");
            return;
        }

        // ダイアログ表示
        const dialog = document.getElementById("barcode-scan-dialog");
        dialog?.classList.remove("none");

        this.onScan = options.onScan ?? (() => {});
        this.completed = false;
        const formats = options.formats ?? [
            ZXingBrowser.BarcodeFormat.EAN_13
        ];

        try {
            this.codeReader = new ZXingBrowser.BrowserMultiFormatReader();
            this.codeReader.possibleFormats = formats;
            this.controls = await this.codeReader.decodeFromConstraints(
                {
                    video: {
                        facingMode: {ideal: "environment"}
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
                    const code = result.getText();
                    console.log("読み取り:", code);
                    this.completed = true;
                    setTimeout(() => {this.complete(code);}, 0);
                }
            );
        } catch (error) {
            console.error("バーコードスキャナー起動エラー", error);
            alert("バーコード読み取りを開始できませんでした。\n" + error.message);
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
                console.warn("ZXing停止時のエラー:", e);
            }
            this.controls = null;
        }
        this.codeReader = null;

        if (this.video) {
            const stream = this.video.srcObject;
            if (stream) {
                stream.getTracks().forEach(track => {
                    try {
                        track.stop();
                    } catch (e) {
                        console.warn("カメラ停止エラー:", e);
                    }
                });
            }
            this.video.pause();
            this.video.srcObject = null;
        }

        const dialog = document.getElementById("barcode-scan-dialog");
        if (dialog) {
            dialog.classList.add("none");
        }
        this.onScan = null;
    }
};