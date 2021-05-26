package com.rpc.client;


import com.rpc.pojo.Result;
import com.rpc.pojo.RpcRequest;
import com.rpc.pojo.RpcResponse;
import com.rpc.registry.instance.RegistryInstance;

/**
 * @author slorui
 * data 2021/5/19
 */
public interface RpcClient {

    Result doRequest(RpcRequest rpcRequest);

    Result sendRequest(RpcRequest rpcRequest, RegistryInstance instance);
}
