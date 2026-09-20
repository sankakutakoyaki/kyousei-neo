"use strict"

/**
 * 伝票一覧描画
 */
export function renderDispatchOrders(
    orders,
    status,
    {
        onDragStart,
        onDragEnd
    } = {}
){

    const area =
        document.getElementById(
            "dispatch-order-list"
        );

    if(!area) return;

    area.replaceChildren();


    const list =
        orders.filter(order => {

            if(status === "unassigned"){
                return order.assignmentCount === 0;
            }

            if(status === "assigned"){
                return order.assignmentCount > 0;
            }

            return true;
        });


    if(list.length === 0){

        const empty =
            document.createElement("div");

        empty.className =
            "dispatch-empty";


        if(status === "assigned"){

            empty.textContent =
                "配車済み伝票はありません";

        } else if(status === "all"){

            empty.textContent =
                "伝票はありません";

        } else {

            empty.textContent =
                "未配車伝票はありません";
        }


        area.appendChild(
            empty
        );

        return;
    }


    list.forEach(order => {

        area.appendChild(
            createDispatchOrderCard(
                order,
                {
                    onDragStart,
                    onDragEnd
                }
            )
        );
    });
}


/**
 * 伝票カード
 */
function createDispatchOrderCard(
    order,
    {
        onDragStart,
        onDragEnd
    } = {}
){

    const card =
        document.createElement("div");

    card.className =
        "dispatch-order-card";

    card.dataset.orderId =
        order.orderId;

    card.draggable = true;


    card.addEventListener(
        "dragstart",
        event => {

            event.dataTransfer.effectAllowed =
                "move";

            event.dataTransfer.setData(
                "text/plain",
                String(order.orderId)
            );

            card.classList.add(
                "dragging"
            );

            onDragStart?.(
                order,
                event
            );
        }
    );


    card.addEventListener(
        "dragend",
        event => {

            card.classList.remove(
                "dragging"
            );

            onDragEnd?.(
                order,
                event
            );
        }
    );


    const header =
        document.createElement("div");

    header.className =
        "dispatch-order-card-header";


    const title =
        document.createElement("strong");

    title.textContent =
        order.title
        || "名称未設定";


    const time =
        document.createElement("span");

    time.textContent =
        order.visitTime
        || "";


    header.append(
        title,
        time
    );


    const address =
        document.createElement("div");

    address.className =
        "dispatch-order-address";

    address.textContent =
        order.fullAddress
        || "";


    card.append(
        header,
        address
    );


    if(order.assignmentCount > 0){

        const assigned =
            document.createElement("div");

        assigned.className =
            "dispatch-order-assigned";

        assigned.textContent =
            `配車済み ${order.assignmentCount}班`;

        card.appendChild(
            assigned
        );
    }


    return card;
}