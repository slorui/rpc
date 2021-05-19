package com.rpc.coder;

/**
 * @author slorui
 * data 2021/5/19
 */
public enum PackageType {

    REQUEST_PACK(1),
    RESPONSE_PACK(2);

    public int getCode() {
        return code;
    }

    private int code;

    PackageType(int code) {
        this.code = code;
    }
}
