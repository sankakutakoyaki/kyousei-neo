"use strict"

/**
 * Query結果を班単位にまとめる
 */
export function groupDispatchCrews(rows){

    const map =
        new Map();

    rows.forEach(row => {

        const dailyCrewId =
            row.dailyCrewId;

        if(!map.has(dailyCrewId)){

            map.set(
                dailyCrewId,
                {
                    dailyCrewId,

                    vehicleId:
                        row.vehicleId,

                    vehicleName:
                        row.vehicleName,

                    registrationArea:
                        row.registrationArea,

                    registrationClass:
                        row.registrationClass,

                    registrationKana:
                        row.registrationKana,

                    registrationNumber:
                        row.registrationNumber,

                    members: []
                }
            );
        }


        if(row.employeeId){

            map.get(dailyCrewId)
                .members
                .push({
                    dailyCrewMemberId:
                        row.dailyCrewMemberId,

                    employeeId:
                        row.employeeId,

                    employeeName:
                        row.employeeName,

                    role:
                        row.role,

                    shiftType:
                        row.shiftType,

                    startTime:
                        row.startTime,

                    endTime:
                        row.endTime
                });
        }
    });

    return [
        ...map.values()
    ];
}


/**
 * 班一覧描画
 */
export function renderDispatchCrews(
    crews,
    {
        initCrewDrop,
        onUnassign,
        expandedCrewIds
    } = {}
){

    const area =
        document.getElementById(
            "dispatch-crew-list"
        );

    if(!area) return;

    area.replaceChildren();

    crews.forEach(crew => {

        area.appendChild(
            createCrewCard(
                crew,
                {
                    initCrewDrop,
                    onUnassign,
                    expandedCrewIds
                }
            )
        );
    });
}


/**
 * 班カード
 */
function createCrewCard(
    crew,
    {
        initCrewDrop,
        onUnassign,
        expandedCrewIds
    } = {}
){

    const card =
        document.createElement("div");

    card.className =
        "dispatch-crew-card";

    card.dataset.dailyCrewId =
        crew.dailyCrewId;

    card.dataset.assignedOrderIds =
        JSON.stringify(
            (crew.assignments ?? [])
                .map(assignment =>
                    assignment.orderId
                )
        );

    const header =
        document.createElement("div");

    header.className =
        "dispatch-crew-card-header";


    const name =
        document.createElement("strong");

    name.textContent =
        crew.vehicleName;


    const number =
        document.createElement("span");

    number.textContent =
        [
            crew.registrationArea,
            crew.registrationClass,
            crew.registrationKana,
            crew.registrationNumber
        ]
        .filter(Boolean)
        .join(" ");


    header.append(
        name,
        number
    );


    const members =
        document.createElement("div");

    members.className =
        "dispatch-crew-members";


    if(crew.members.length === 0){

        const empty =
            document.createElement("div");

        empty.className =
            "dispatch-member-unassigned";

        empty.textContent =
            "乗務員未設定";

        members.appendChild(
            empty
        );

    } else {

        crew.members.forEach(member => {

            members.appendChild(
                createCrewMember(
                    member
                )
            );
        });
    }

const assignmentCount =
    crew.assignments?.length ?? 0;


const orders =
    document.createElement("div");

const expanded =
    expandedCrewIds?.has(
        crew.dailyCrewId
    ) ?? false;

orders.className =
    expanded
        ? "dispatch-assignment-list"
        : "dispatch-assignment-list collapsed";


renderCrewAssignments(
    orders,
    crew.assignments ?? [],
    {
        onUnassign,
        dailyCrewId:
            crew.dailyCrewId
    }
);


card.addEventListener(
    "click",
    event => {

        if(
            card.classList.contains(
                "drag-over"
            )
        ){
            return;
        }

        if(
            event.target.closest(
                "button, a, input, select, textarea"
            )
        ){
            return;
        }

        const collapsed =
            orders.classList.toggle(
                "collapsed"
            );

        if(collapsed){

            expandedCrewIds?.delete(
                crew.dailyCrewId
            );

        } else {

            expandedCrewIds?.add(
                crew.dailyCrewId
            );
        }
    }
);


card.append(
    header,
    members,
    orders
);


// 伝票がある場合だけ右肩バッジを表示
if(assignmentCount > 0){

    const assignmentBadge =
        document.createElement("span");

    assignmentBadge.className =
        "dispatch-assignment-badge";

    assignmentBadge.textContent =
        assignmentCount;

    card.appendChild(
        assignmentBadge
    );
}


if(initCrewDrop){
    initCrewDrop(
        card
    );
}


return card;
}


/**
 * 班員
 */
function createCrewMember(member){

    const row =
        document.createElement("div");

    row.className =
        "dispatch-crew-member";


    const role =
        member.role === 1
            ? "担当"
            : "助手";


    const roleSpan =
        document.createElement("span");

    roleSpan.className =
        "dispatch-member-role";

    roleSpan.textContent =
        role;


    const name =
        document.createElement("span");

    name.textContent =
        member.employeeName;


    row.append(
        roleSpan,
        name
    );


    if(member.shiftType !== 1){

        const status =
            document.createElement("span");

        status.className =
            "dispatch-member-warning";

        status.textContent =
            getShiftStatusText(
                member.shiftType
            );

        row.appendChild(
            status
        );
    }


    return row;
}


/**
 * シフト状態
 */
function getShiftStatusText(shiftType){

    switch(shiftType){

        case 2:
            return "休み";

        case 3:
            return "有休";

        case 4:
            return "午前休";

        case 5:
            return "午後休";

        default:
            return "シフト未登録";
    }
}


/**
 * 班内伝票
 */
function renderCrewAssignments(
    area,
    assignments,
    {
        onUnassign,
        dailyCrewId
    } = {}
){

    area.replaceChildren();

    assignments.forEach(
        assignment => {

            const item =
                document.createElement("div");

            item.className =
                "dispatch-assignment-item";

            item.dataset.dispatchAssignmentId =
                assignment.dispatchAssignmentId;

            item.dataset.orderId =
                assignment.orderId;

            item.draggable = true;

            item.addEventListener(
                "dragstart",
                event => {

                    event.stopPropagation();

                    event.dataTransfer.effectAllowed =
                        "move";

                    event.dataTransfer.setData(
                        "application/x-dispatch-assignment",
                        JSON.stringify({
                            dispatchAssignmentId:
                                assignment.dispatchAssignmentId,

                            orderId:
                                assignment.orderId,

                            sourceDailyCrewId:
                                dailyCrewId
                        })
                    );

                    item.classList.add(
                        "dragging"
                    );
                }
            );

            item.addEventListener(
                "dragend",
                () => {

                    item.classList.remove(
                        "dragging"
                    );
                }
            );

            const content =
                document.createElement("div");

            content.className =
                "dispatch-assignment-content";


            const title =
                document.createElement("strong");

            title.textContent =
                assignment.title
                || "名称未設定";


            const address =
                document.createElement("span");

            address.textContent =
                assignment.fullAddress
                || "";


            content.append(
                title,
                address
            );


            const remove =
                document.createElement("button");

            remove.type = "button";

            remove.className =
                "dispatch-assignment-remove";

            remove.textContent =
                "×";

            remove.title =
                "配車解除";


            remove.addEventListener(
                "click",
                async event => {

                    event.stopPropagation();

                    await onUnassign?.(
                        assignment
                    );
                }
            );


            item.append(
                content,
                remove
            );

            area.appendChild(
                item
            );
        }
    );
}