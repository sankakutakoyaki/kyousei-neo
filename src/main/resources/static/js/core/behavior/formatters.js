"use strict"

export const formatters = {
    // 数字のみ
    num: (v) => {
        return String(v ?? "").normalize("NFKC").replace(/\D/g, "");
    },
    // 英数字・記号を許可するコード
    code: (v) => {
        return String(v ?? "").normalize("NFKC");
    },
    // リサイクル券番号
    recycle: (v) => {
        const raw = String(v ?? "").normalize("NFKC").replace(/\D/g, "");
        if(raw.length !== 13){
            return raw;
        }
        return (
            raw.slice(0, 4) + "-" + raw.slice(4, 12) + "-" + raw.slice(12)
        );
    },

    currency: (v) => {
        if(v == null || v === "") return "";

        const num = Number(String(v).replace(/,/g, ""));
        if(Number.isNaN(num)){
            return v;
        }
        return num.toLocaleString("ja-JP");
    }
};

// "use strict"

// export const formatters = {
//     recycle: (v) => {
//         const raw = String(v ?? "").replace(/\D/g, "");
//         if(raw.length !== 13){
//             return raw;
//         }
//         return (
//             raw.slice(0,4) +
//             "-" +
//             raw.slice(4,12) +
//             "-" +
//             raw.slice(12)
//         );
//     },

//     currency: (v) => {
//         if(v == null || v === "") return "";
//         const num = Number(
//             String(v).replace(/,/g, "")
//         );
//         if(Number.isNaN(num)){
//             return v;
//         }
//         return num.toLocaleString("ja-JP");
//     }
// };