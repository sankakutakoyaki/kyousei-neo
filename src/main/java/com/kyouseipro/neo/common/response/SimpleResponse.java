package com.kyouseipro.neo.common.response;

public class SimpleResponse<T> {

    private final String message;
    private final T data;

    private SimpleResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public static <T> SimpleResponse<T> ok(T data) {
        return new SimpleResponse<>(null, data);
    }

    public static <T> SimpleResponse<T> ok(String message, T data) {
        return new SimpleResponse<>(message, data);
    }

    public static <T> SimpleResponse<T> error(String message) {
        return new SimpleResponse<>(message, null);
    }

    public String getMessage() { return message; }
    public T getData() { return data; }
}