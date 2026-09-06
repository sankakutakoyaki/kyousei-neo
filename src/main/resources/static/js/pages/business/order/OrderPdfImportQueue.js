"use strict";

// One queue belongs to one visit to the order page. It never confirms reviews.
export class OrderPdfImportQueue {
    constructor({upload, recognize, review = async () => {}, onChange = () => {}}) {
        this.upload = upload;
        this.recognize = recognize;
        this.review = review;
        this.onChange = onChange;
        this.entries = [];
        this.pending = [];
        this.active = null;
        this.running = false;
        this.disposed = false;
    }

    enqueue(files, shipperId, shipperName) {
        if (!/^[1-9]\d*$/.test(String(shipperId ?? ""))) {
            throw new Error("荷主を選択してください。");
        }
        for (const file of files) {
            this.pending.push({
                file, shipperId, shipperName, name: file.name, size: file.size,
                status: "waiting", orderImportId: null, candidates: null, confirmed: false,
                error: "", completedAt: null
            });
        }
        return this.drain();
    }

    retry(entry) {
        if (entry.status !== "failed" || this.disposed) return;
        this.entries = this.entries.filter(item => item !== entry);
        entry.status = "waiting";
        entry.error = "";
        this.pending.push(entry);
        return this.drain();
    }

    dispose() {
        this.disposed = true;
        this.pending = [];
    }

    async drain() {
        if (this.running || this.disposed) return;
        this.running = true;
        try {
            while (this.pending.length && !this.disposed) {
                const entry = this.pending.shift();
                this.active = entry;
                entry.status = "processing";
                this.onChange();
                try {
                    if (!/\.pdf$/i.test(entry.name) || (entry.file?.type && entry.file.type !== "application/pdf")) {
                        throw new Error("PDFファイルを選択してください。");
                    }
                    if (!entry.size || entry.size > 50 * 1024 * 1024) {
                        throw new Error("空のPDF、または50MBを超えるPDFは取り込めません。");
                    }
                    if (!entry.orderImportId) {
                        const saved = await this.upload(entry.file, entry.shipperId);
                        if (!saved?.orderImportId) throw new Error("PDFの保存結果を取得できませんでした。");
                        entry.orderImportId = saved.orderImportId;
                        entry.file = null;
                    }
                    entry.candidates = await this.recognize(entry.orderImportId);
                    if (!entry.candidates || typeof entry.candidates !== "object" || Array.isArray(entry.candidates)) {
                        throw new Error("読取結果の形式が不正です。");
                    }
                    if (this.disposed) break;
                    entry.status = "reviewing";
                    this.onChange();
                    await this.review(entry);
                    entry.status = "completed";
                } catch (error) {
                    entry.status = "failed";
                    entry.error = error.message || "PDFの取込・読取に失敗しました。";
                }
                entry.completedAt = new Date().toISOString();
                this.entries.push(entry);
                this.active = null;
                if (!this.disposed) this.onChange();
            }
        } finally {
            this.running = false;
            this.active = null;
            if (!this.disposed) this.onChange();
        }
    }
}
