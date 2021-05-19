package com.rpc.client;


import com.rpc.pojo.RpcRequest;

/**
 * @author slorui
 * data 2021/5/19
 */
public interface RpcClient {

    Object sendRequest(RpcRequest rpcRequest);
}
