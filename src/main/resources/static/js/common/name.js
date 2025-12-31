
// 数字が被らなように名前を作成する
function createUniqueName({
    list,
    groupFn,      // 省略可
    nameFn,
    baseName
}) {    
    const effectiveGroupFn = groupFn ?? (() => true);
    const filteredList = list.filter(effectiveGroupFn);

    const usedNumbers = filteredList
        .filter(effectiveGroupFn)
        .map(item => {
            const name = nameFn(item);
            const m = name?.match(/^新しい項目\((\d+)\)$/);
            return m ? Number(m[1]) : null;
        })
        .filter(n => n !== null);

    // baseName 単体が存在するか
    const plainExists = filteredList
        .some(item => nameFn(item) === baseName);

    // 何も存在しない場合だけ baseName
    if (!plainExists && usedNumbers.length === 0) {
        return baseName;
    }

    let num = 1;
    while (usedNumbers.includes(num)) {
        num++;
    }
    return `${baseName}(${num})`;
}