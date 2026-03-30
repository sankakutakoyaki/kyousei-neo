"use strict"

// import { partnerCompanyController } from "../../pages/corporation/partner/partnerController.js";
// import { partnerEmployeeController } from "../../pages/corporation/partner/partnerController.js";

// import { createPartnerControllers } from "../../pages/corporation/partner/partnerController.js";

// let controllerMap = {};

// export function initControllers(){

//     const elements = document.querySelectorAll("[data-controller]");

//     controllerMap = {};

//     elements.forEach(el => {
//         const name = el.dataset.controller;

//         const factory = controllerFactory[name];
//         if(!factory){
//             console.warn("controller not found:", name);
//             return;
//         }

//         controllerMap[name] = factory();
//     });
// }

// export function initControllers(){
//     const partner = createPartnerControllers();
//     controllerMap = {
//         ...partner
//     };
// }

// export function getController(target){
//     return controllerMap[target];
// }

// const controllerMap = {
//     partnerCompany: partnerCompanyController,
//     partnerEmployee: partnerEmployeeController
// };

// export function getController(target){
//     return controllerMap[target];
// }