export function assignmentPair(source,target) {
    if(source?.kind===target?.kind)return null;
    const order=source?.kind==='order'?source:target?.kind==='order'?target:null;
    const vehicle=source?.kind==='vehicle'?source:target?.kind==='vehicle'?target:null;
    return order&&vehicle?{orderId:order.id,vehicleCode:vehicle.code}:null;
}
