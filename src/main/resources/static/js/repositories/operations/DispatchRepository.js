import {RequestClient} from '../../core/api/RequestClient.js';
async function query(queryId,params) {
    const result=await RequestClient.request({queryId,params});
    if(result.ok===false)throw new Error('配車情報を取得・保存できませんでした。');
    return result.data;
}
export const DispatchRepository={
    find:params=>query('operationDispatchDetail',params),
    crew:params=>query('operationCrewDetail',params),
    save:async params=>({data:await query('operationDispatchSave',params),count:1})
};

// 左右のDataTableが同じ検索条件・同じ取得結果を使用する。
export function createDispatchRepositories() {
    let pending=null,key='';
    const snapshot=params=>{
        const next=JSON.stringify(params);
        if(!pending||key!==next){
            key=next;
            pending=Promise.all([
                query('operationDispatchList',params),
                query('operationList',{entity:'VEHICLE'}),
                query('operationList',{entity:'CREW',workDate:params.dateFrom})
            ]).then(([orders,vehicles,crews])=>({orders,vehicles:vehicles.map(vehicle=>{
                const crew=crews.find(c=>String(c.vehicleId)===String(vehicle.id));
                return {...vehicle,crew,assignedOrders:orders.filter(order=>crew&&String(order.crewIds??'').split(',').includes(String(crew.id)))};
            })}));
        }
        return pending;
    };
    return {
        invalidate(){pending=null;},
        orders:{...DispatchRepository,search:async params=>(await snapshot(params)).orders},
        vehicles:{search:async params=>(await snapshot(params)).vehicles}
    };
}
