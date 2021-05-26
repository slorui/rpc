package com.rpc.pojo;

import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

/**
 * @author slorui
 * data 2021/5/26
 */
@Slf4j
public class SyncRpcResponse implements Result {


    private RpcRequest rpcRequest;

    private RpcResponse rpcResponse;

    public SyncRpcResponse(RpcRequest rpcRequest, RpcResponse rpcResponse) {
        this.rpcResponse = rpcResponse;
        this.rpcRequest = rpcRequest;
    }


    @Override
    public Object getData() {
        return rpcResponse.getData();
    }

}
