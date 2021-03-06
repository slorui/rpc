package com.rpc.exception;

/**
 * @author slorui
 * data 2021/5/19
 */
public enum RpcError {

    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE,
    SERVICE_NOT_FOUND,
    UNKNOWN_PROTOCOL,
    UNKNOWN_PACKAGE_TYPE,
    UNKNOWN_SERIALIZER,
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY,
    REGISTER_SERVICE_FAILED,
    SERVICE_SCAN_PACKAGE_NOT_FOUND,
    UNKNOWN_ERROR,
    CONNECT_TIME_OUT,
    REQUEST_TIME_OUT,
    REQUEST_ERROR,
    SEND_MESSAGE_ERROR;

}
