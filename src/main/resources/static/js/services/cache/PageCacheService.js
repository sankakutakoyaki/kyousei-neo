"use strict"

export const PageCacheService = {
    get(key){
        return APP.cache ?.page?.[key];
    },

    set(key, value){
        APP.cache.page[key] = value;
    },

    remove(key){
        delete APP.cache.page[key];
    },

    clear(){
        APP.cache.page = {};
    }
};