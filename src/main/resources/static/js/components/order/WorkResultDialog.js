"use strict";
import { apiFetch } from "../../core/api/apiFetch.js";

export const workResultLabels = {COMPLETED: "完了", DELIVERY_FAILED: "搬入不可", POSTPONED: "日延"};

export class WorkResultDialog {
    constructor(root, onSaved) {
        this.root = root;
        this.onSaved = onSaved;
        this.type = root.querySelector("[data-result-type]");
        this.reason = root.querySelector("[data-result-reason]");
        this.typeField = root.querySelector("[data-result-type-field]");
        this.reasonField = root.querySelector("[data-result-reason-field]");
        this.confirmation = root.querySelector("[data-result-confirmation]");
        this.hint = root.querySelector("[data-result-hint]");
        this.status = root.querySelector("[data-result-status]");
        this.submit = root.querySelector("[data-result-submit]");
        this.cancel = root.querySelector("[data-result-cancel]");
        this.generation = 0;
        root.querySelector("form").addEventListener("submit", event => {event.preventDefault(); void this.save();});
        this.type.addEventListener("change", () => this.updateFields());
        this.cancel.addEventListener("click", () => {if (!this.saving) root.close();});
        root.addEventListener("cancel", event => {if (this.saving) event.preventDefault();});
        root.addEventListener("close", () => {this.generation++;});
    }
    async open(orderId, assignmentId, type, canApply = () => true) {
        if (this.opening || this.saving) return;
        const generation = ++this.generation;
        this.opening = true;
        try {
            const url = `/api/my-orders/${orderId}/assignments/${assignmentId}/result`;
            const response = await apiFetch(url, {method: "GET"});
            if (generation !== this.generation || !this.root.isConnected || !canApply()) return;
            this.url = url;
            this.snapshot = response.data;
            this.type.value = type;
            this.confirmCompletion = !this.snapshot.workResultId && type === "COMPLETED";
            this.reason.value = "";
            this.signature = null;
            this.status.textContent = this.snapshot.workResultId
                ? `訪問日：${this.snapshot.workDate}。現在の結果：${workResultLabels[this.snapshot.resultType] ?? "未報告"}。${this.snapshot.reason || ""} 訂正理由を入力してください。`
                : `訪問日：${this.snapshot.workDate}。この訪問の作業結果を報告します。`;
            this.root.showModal();
            this.updateFields();
            (this.confirmCompletion ? this.submit : this.type).focus();
        } finally {this.opening = false;}
    }
    updateFields() {
        this.reason.required = !!this.snapshot?.workResultId || this.type.value !== "COMPLETED";
        this.typeField.hidden = this.confirmCompletion;
        this.reasonField.hidden = !this.reason.required;
        this.hint.hidden = !this.reason.required;
        this.confirmation.hidden = this.reason.required;
        this.submit.textContent = this.reason.required ? "報告を保存" : "完了を報告";
    }
    async save() {
        if (this.saving || !this.root.open) return;
        const payload = {
            assignmentVersion: this.snapshot.assignmentVersion,
            resultVersion: this.snapshot.resultVersion ?? null,
            resultType: this.type.value, reason: this.reason.required ? this.reason.value.trim() : "", workDate: this.snapshot.workDate
        };
        if (this.reason.required && !payload.reason) {this.status.textContent = "理由を入力してください。";return;}
        const signature = JSON.stringify(payload);
        if (signature !== this.signature) {this.signature = signature;this.requestId = crypto.randomUUID();}
        const url = this.url;
        this.saving = true;
        for (const el of [this.submit, this.cancel, this.type, this.reason]) el.disabled = true;
        this.status.textContent = "報告を保存しています…";
        let saved = false;
        try {
            await apiFetch(url, {data: {...payload, requestId: this.requestId}});
            saved = true;
            this.root.close();
        } catch (error) {
            this.status.textContent = error.message || "保存できませんでした。入力を確認して再試行してください。";
        } finally {
            this.saving = false;
            for (const el of [this.submit, this.cancel, this.type, this.reason]) el.disabled = false;
            this.updateFields();
        }
        if (saved) await this.onSaved();
    }
}
