"use strict";

import { FormController } from "../../../application/FormController.js";
import { DailyCrewRepository } from "../../../repositories/operations/dispatch/DailyCrewRepository.js";

export function createCrewMemberForm(controller, {session, afterSave} = {}) {
    let crewMemberInitialState = {members: [], candidates: []};
    let crewMemberForm = null;
    const byId = session.byId;
    const form = new FormController({
        controller,
        formId: "dispatch-member-form",
        key: "dailyCrewId",
        idKey: "dailyCrewId",
        submitText: "保存",
        cancelText: "キャンセル",
        // 乗務員一括保存
        saveHandler: async payload => {
            return await DailyCrewRepository.saveMembers({
                dailyCrewId: payload.dailyCrewId,
                members: payload.members ?? []
            });
        },
        // フォームを開いたとき
        onOpen: async (crew, form, canApply) => {
            const workDate = byId("dispatch-work-date")?.value ?? "";
            const officeId = Number(byId("dispatch-office")?.value || 0);
            const dispatchCategory = Number(byId("dispatch-category")?.value || 0);
            const result = await DailyCrewRepository.findMemberCandidates({
                dailyCrewId: crew.dailyCrewId,
                workDate,
                officeId,
                dispatchCategory,
                shiftType: 1,
                state: APP.cache.common.state.INITIAL
            });
            if(!session.isActive() || !canApply()) return;
            const candidates = result.data ?? [];
            // リセット・変更判定用
            crewMemberInitialState = {
                members: structuredClone(crew.members ?? []),
                candidates: structuredClone(candidates)
            };
            renderCrewMemberForm(crew, candidates);
        },
        // 明細
        buildAdditionalPayload: () => ({members: collectCrewMembers()}),
        // 明細変更判定
        hasAdditionalChanges: () => hasCrewMemberChanges(),
        // リセット
        resetAdditional: () => resetCrewMembers(),
        // 保存後
        afterSave: async () => {await afterSave?.();}
    });
    crewMemberForm = form;
    form.addMember = addCrewMember;
    form.removeMember = removeCrewMember;
    return form;

    /**
     * 乗務員一覧描画
     */
    function renderCrewMemberForm(crew, candidates) {
        const list = byId("dispatch-member-current-list");
        const template = byId("dispatch-member-row-template");
        const candidateSelect = byId("dispatch-member-candidate");
        if(!list || !template || !candidateSelect){
            return;
        }
        list.replaceChildren();
        candidateSelect.replaceChildren();
        // 現在の乗務員
        const members = crew.members ?? [];
        members.forEach(member => {appendCrewMemberRow(
            {
                dailyCrewMemberId: member.dailyCrewMemberId,
                employeeId: member.employeeId,
                employeeName: member.employeeName,
                role: member.role
            });
        });
        updateMemberEmptyState();
        // 追加ボタン
        const addButton = byId("dispatch-member-add-button");
        // 役割ボタン
        const addRole = byId("dispatch-member-add-role");
        // 追加候補なし
        if(candidates.length === 0){
            candidateSelect.add(new Option("追加可能な担当者はいません", ""));
            candidateSelect.disabled = true;
            if(addRole){
                addRole.disabled = true;
            }
            if(addButton){
                addButton.disabled = true;
            }
            return;
        }
        // 追加候補あり
        candidates.forEach(candidate => {
            candidateSelect.add(new Option(candidate.fullName, candidate.employeeId));
        });
        candidateSelect.disabled = false;
        if(addRole){
            addRole.disabled = false;
        }
        if(addButton){
            addButton.disabled = false;
        }
    }

    /**
     * 乗務員行追加
     */
    function appendCrewMemberRow({dailyCrewMemberId = "", employeeId, employeeName, role}) {
        const list = byId("dispatch-member-current-list");
        const template = byId("dispatch-member-row-template");
        if(!list || !template){
            return;
        }
        const fragment = template.content.cloneNode(true);
        const row = fragment.querySelector("[data-member-row]");
        if(!row){
            return;
        }
        row.dataset.dailyCrewMemberId = dailyCrewMemberId ?? "";
        row.dataset.employeeId = employeeId;
        const roleSelect = row.querySelector('[data-field="role"]');
        if(roleSelect){roleSelect.value = String(role);}
        const name = row.querySelector('[data-field="employeeName"]');
        if(name){
            name.textContent = employeeName ?? "";
        }
        list.appendChild(fragment);
        updateMemberEmptyState();
    }

    /**
     * 現在の乗務員
     */
    function collectCrewMembers(){
        const rows = session.root.querySelectorAll("#dispatch-member-current-list [data-member-row]");
        return [...rows].map(row => {
            const role = row.querySelector('[data-field="role"]');
            return {
                dailyCrewMemberId: row.dataset.dailyCrewMemberId ? Number(row.dataset.dailyCrewMemberId): null,
                employeeId: Number(row.dataset.employeeId),
                role: Number(role?.value)
            };
        });
    }

    /**
     * 変更判定
     */
    function hasCrewMemberChanges(){
        const current = normalizeCrewMembers(collectCrewMembers());
        const original = normalizeCrewMembers(crewMemberInitialState.members);
        return JSON.stringify(current) !== JSON.stringify(original);
    }

    function normalizeCrewMembers(members){
        return (members ?? []).map(member => ({
            dailyCrewMemberId:  member.dailyCrewMemberId ? Number(member.dailyCrewMemberId): null,
            employeeId: Number(member.employeeId),
            role: Number(member.role)
        }));
    }

    /**
     * リセット
     */
    function resetCrewMembers() {
        const members = structuredClone(crewMemberInitialState.members);
        const candidates = structuredClone(crewMemberInitialState.candidates);
        renderCrewMemberForm({members}, candidates);
    }

    /**
     * 乗務員の有無に応じて
     * 未設定メッセージを更新
     */
    function updateMemberEmptyState() {
        const list = byId("dispatch-member-current-list");
        if(!list){
            return;
        }
        const hasMembers = list.querySelector("[data-member-row]") !== null;
        const empty = list.querySelector(".dispatch-member-empty");
        // 乗務員あり
        if(hasMembers){
            empty?.remove();
            return;
        }
        // すでに表示済み
        if(empty){
            return;
        }
        // 乗務員なし
        const message = document.createElement("div");
        message.className = "dispatch-member-empty";
        message.textContent = "乗務員は未設定です";
        list.appendChild(message);
    }

    /**
     * 追加候補の有無に応じて
     * 候補コンボ・役割・追加ボタンの状態を更新
     */
    function updateCandidateState() {
        const candidateSelect = byId("dispatch-member-candidate");
        const addRole = byId("dispatch-member-add-role");
        const addButton = byId("dispatch-member-add-button");
        if(!candidateSelect){
            return;
        }
        const hasCandidate = [...candidateSelect.options].some(option => option.value !== "");
        if(!hasCandidate){
            candidateSelect.replaceChildren();
            candidateSelect.add(new Option("追加可能な担当者はいません", ""));
            candidateSelect.disabled = true;
            if(addRole){
                addRole.disabled = true;
            }
            if(addButton){
                addButton.disabled = true;
            }
            return;
        }
        candidateSelect.disabled = false;
        if(addRole){
            addRole.disabled = false;
        }
        if(addButton){
            addButton.disabled = false;
        }
    }

    function addCrewMember() {
        const candidate = byId("dispatch-member-candidate");
        const role = byId("dispatch-member-add-role");
        if (!candidate?.value || !role?.value || !crewMemberForm) return;

        const option = candidate.selectedOptions[0];

        appendCrewMemberRow({
            employeeId: Number(candidate.value),
            employeeName: option?.textContent ?? "",
            role: Number(role.value)
        });

        option?.remove();
        updateCandidateState();
        crewMemberForm.updateSubmitState();
    }

    function removeCrewMember(button) {
        if (!crewMemberForm) return;

        const row = button.closest("[data-member-row]");
        if (!row) return;

        const employeeId = Number(row.dataset.employeeId);
        const employeeName = row.querySelector('[data-field="employeeName"]')?.textContent ?? "";

        row.remove();
        updateMemberEmptyState();

        const candidate = byId("dispatch-member-candidate");

        if (candidate && employeeId) {
            [...candidate.options]
                .filter(option => option.value === "")
                .forEach(option => option.remove());

            const exists = [...candidate.options]
                .some(option => Number(option.value) === employeeId);

            if (!exists) candidate.add(new Option(employeeName, employeeId));
            updateCandidateState();
        }

        crewMemberForm.updateSubmitState();
    }

}
