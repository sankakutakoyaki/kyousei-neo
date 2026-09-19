"use strict"

export function createDispatchVehicleColumns(){
    return [
        {
            label: "営業所",
            field: "office-name"
        },
        {
            label: "車両",
            field: "vehicle-name"
        },
        {
            label: "車番",
            render: (item) => {
                return [
                    item.registrationArea,
                    item.registrationClass,
                    item.registrationKana,
                    item.registrationNumber
                ]
                .filter(Boolean)
                .join(" ");
            }
        },
        {
            label: "メーカー",
            field: "manufacturer"
        },
        {
            label: "型式",
            field: "model-code"
        }
    ];
}