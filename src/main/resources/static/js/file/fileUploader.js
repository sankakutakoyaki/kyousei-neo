"use strict";

export const FileUploader = {

    attachDrop(element, manager){

        element.addEventListener(
            "dragover",
            e=> e.preventDefault()
        );

        element.addEventListener(
            "drop",
            async e=>{

                e.preventDefault();

                await manager.upload(
                    e.dataTransfer.files
                );

            }
        );

    },

    attachInput(input, manager){

        input.addEventListener(
            "change",
            async e=>{

                await manager.upload(
                    e.target.files
                );

            }
        );

    }

};
// export const FileUploader = {

//     attachInput(input,manager){

//         input.addEventListener(
//             "change",
//             async e=>{

//                 await manager.upload(
//                     e.target.files
//                 );

//             }
//         );

//     },

//     attachDrop(element,manager){

//         element.addEventListener(
//             "dragover",
//             e=>e.preventDefault()
//         );

//         element.addEventListener(
//             "drop",
//             async e=>{

//                 e.preventDefault();

//                 await manager.upload(
//                     e.dataTransfer.files
//                 );

//             }
//         );

//     }

// };