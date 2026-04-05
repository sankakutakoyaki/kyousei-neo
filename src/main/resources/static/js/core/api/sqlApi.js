"use strict"

import { api } from "./apiService.js";

export async function select(queryId, params = {}) {
  const res = await api.post("/select", {
    queryId,
    params
  });
  return res.data;
}

export async function execute(queryId, params = {}) {
  const res = await api.post("/execute", {
    queryId,
    params
  });
  return res.data;
}