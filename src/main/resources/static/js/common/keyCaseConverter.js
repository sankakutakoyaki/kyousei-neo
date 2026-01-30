"use strict"

/**
 * camelCase → kebab-case
 * employeeId → employee-id
 */
function camelToKebab(str) {
    return str
        .replace(/([a-z0-9])([A-Z])/g, '$1-$2')
        .toLowerCase();
}

/**
 * camelCase → snake_case
 * employeeId → employee_id
 */
function camelToSnake(str) {
    return str
        .replace(/([a-z0-9])([A-Z])/g, '$1_$2')
        .toLowerCase();
}

/**
 * snake_case → camelCase
 * employee_id → employeeId
 */
function snakeToCamel(str) {
    return str.replace(/_([a-z])/g, (_, c) => c.toUpperCase());
}

/**
 * kebab-case → camelCase
 * employee-id → employeeId
 */
function kebabToCamel(str) {
    return str.replace(/-([a-z])/g, (_, c) => c.toUpperCase());
}

/**
 * 汎用変換
 * from / to を指定して変換
 */
function convertKey(key, from, to) {
    if (from === to) return key;

    let camel;

    switch (from) {
        case 'camel':
            camel = key;
            break;
        case 'snake':
            camel = snakeToCamel(key);
            break;
        case 'kebab':
            camel = kebabToCamel(key);
            break;
        default:
            throw new Error(`Unknown from case: ${from}`);
    }

    switch (to) {
        case 'camel':
            return camel;
        case 'snake':
            return camelToSnake(camel);
        case 'kebab':
            return camelToKebab(camel);
        default:
            throw new Error(`Unknown to case: ${to}`);
    }
}