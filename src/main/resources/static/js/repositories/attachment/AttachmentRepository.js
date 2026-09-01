"use strict";
import { apiFetch } from "../../core/api/apiFetch.js";

const base = (parentType, parentId) => `/api/attachments/${encodeURIComponent(parentType)}/${parentId}`;
export const AttachmentRepository = {
    async groups(parentType,parentId) { return (await apiFetch(`${base(parentType,parentId)}/groups`,{method:"GET"})).data ?? []; },
    async createGroup(parentType,parentId,name) { return apiFetch(`${base(parentType,parentId)}/groups`,{data:{name}}); },
    async renameGroup(groupId,name) { return apiFetch(`/api/attachments/groups/${groupId}`,{method:"PATCH",data:{name}}); },
    async deleteGroup(groupId) { return apiFetch(`/api/attachments/groups/${groupId}`,{method:"DELETE"}); },
    async upload(parentType,parentId,groupId,files) {
        const data=new FormData(); [...files].forEach(file=>data.append("files",file));
        return apiFetch(`${base(parentType,parentId)}/groups/${groupId}/files`,{data,timeout:120000});
    },
    async renameFile(fileId,name) { return apiFetch(`/api/attachments/files/${fileId}`,{method:"PATCH",data:{name}}); },
    async deleteFile(fileId) { return apiFetch(`/api/attachments/files/${fileId}`,{method:"DELETE"}); },
    contentUrl(fileId,download=false) { return `/api/attachments/files/${fileId}/content${download?"?disposition=attachment":""}`; }
};
