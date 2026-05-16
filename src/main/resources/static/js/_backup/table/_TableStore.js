// "use strict"

// export class TableStore {

//     constructor(){
//         this.origin = [];
//         this.view = [];
//     }

//     setData(list){
//         this.origin = list;
//         this.view = list;
//     }

//     applyFilters(filters){
//         let list = this.origin;

//         for(const fn of filters){
//             list = list.filter(fn);
//         }
//         this.view = list;
//     }

//     getData(){
//         return this.view;
//     }

// }