export function qualificationInView(row, view, today) {
    const expiry=String(row.expiryDate??'').slice(0,10);
    const renewal=String(row.renewalDate??'').slice(0,10);
    if(view==='expired')return !!expiry && expiry<today;
    if(view!=='renewal')return true;
    if(expiry && expiry<today)return false;
    const end=new Date(today+'T00:00:00Z');end.setUTCDate(end.getUTCDate()+90);
    const limit=end.toISOString().slice(0,10);
    return !!((expiry && expiry<=limit) || (renewal && renewal<=limit));
}
