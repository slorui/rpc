package com.rpc.serializer;

/**
 * @author slorui
 * data 2021/5/19
 */
public enum SerializerCode {

    JSON(1),
    KRYO(0);

    private int code;

    public int getCode() {
        return code;
    }

    SerializerCode(int code){
        this.code = code;
    }
}
