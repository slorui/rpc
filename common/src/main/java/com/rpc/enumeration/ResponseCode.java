package com.rpc.enumeration;

/**
 * @author slorui
 * data 2021/5/19
 */
public enum ResponseCode {

    SUCCESS(200,"服务调用成功"),
    FAIL(404,"没有找到调用的服务"),
    METHOD_NOT_FOUND(404,"没有找到调用的服务");

    private int code;

    private String message;

    ResponseCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
