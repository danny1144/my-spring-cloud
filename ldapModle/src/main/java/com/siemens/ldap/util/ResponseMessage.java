package com.siemens.ldap.util;

import lombok.Data;

import java.io.Serializable;


/**
 * 响应类
 * @author z00403vj
 * @param <T>
 */
@Data
public class ResponseMessage<T> implements Serializable {

    protected String message;

    protected T result;

    protected int code;

    protected boolean success;

    private Long timestamp;

    public static <T> ResponseMessage<T> error(String message) {
        return error(-1, message);
    }

    public static <T> ResponseMessage<T> error(int code, String message) {
        return new ResponseMessage<T>()
                .message(message)
                .code(code)
                .isSuccessRes(Boolean.FALSE)
                .putTimeStamp();
    }

    public static <T> ResponseMessage<T> ok(T result, String msg) {
        return new ResponseMessage<T>()
                .message(msg)
                .result(result)
                .code(0)
                .isSuccessRes(Boolean.TRUE)
                .putTimeStamp();
    }

    public static <T> ResponseMessage<T> okWithoutMsg(T result) {
        return new ResponseMessage<T>()
                .result(result)
                .code(0)
                .isSuccessRes(Boolean.TRUE)
                .putTimeStamp();

    }

    public static <T> ResponseMessage<T> okWithoutRes(String msg) {
        return new ResponseMessage<T>()
                .message(msg)
                .code(0)
                .isSuccessRes(Boolean.TRUE)
                .putTimeStamp();
    }

    private ResponseMessage<T> result(T result) {
        this.result = result;
        return this;
    }

    private ResponseMessage<T> message(String msg) {
        this.message = msg;
        return this;
    }

    private ResponseMessage<T> putTimeStamp() {
        this.timestamp = System.currentTimeMillis();
        return this;
    }

    private ResponseMessage<T> code(int code) {
        this.code = code;
        return this;
    }

    private ResponseMessage<T> isSuccessRes(boolean isSuccess) {
        this.success = isSuccess;
        return this;
    }
}
