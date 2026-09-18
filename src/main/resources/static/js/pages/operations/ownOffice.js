export async function loadOwnOffices() {
    const response = await fetch('/api/own-offices/context');
    if (!response.ok) throw new Error('営業所情報を取得できませんでした。');
    return response.json();
}
export function fillOwnOffices(select, context, { unassigned = false, all = true, value = context.defaultOfficeId } = {}) {
    select.replaceChildren();
    if (all) select.add(new Option('すべて', ''));
    if (unassigned) select.add(new Option('未設定', '0'));
    for (const office of context.offices) select.add(new Option(office.label, office.value));
    select.value = String(value ?? '');
    if (select.selectedIndex < 0 && !all && value && String(value) !== '0') {
        select.add(new Option('登録済み営業所（現在は無効）', String(value)));
        select.value = String(value);
    }
    if (select.selectedIndex < 0) select.selectedIndex = 0;
}
