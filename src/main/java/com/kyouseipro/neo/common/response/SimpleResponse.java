package com.kyouseipro.neo.common.response;

public record SimpleResponse<T>(String message, T data) {}
