package com.rpc.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * @author slorui
 * data 2021/5/19
 */
@Data
@AllArgsConstructor
@ToString
public class RpcException extends RuntimeException{

    private RpcError rpcError;
}
