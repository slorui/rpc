package com.rpc.pojo;

import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author slorui
 * data 2021/5/26
 */
@Slf4j
public class AsyncRpcResponse implements Result {

    private RpcRequest rpcRequest;
    private ConcurrentHashMap<String ,RpcResponse>rpcContext;

    public AsyncRpcResponse(ConcurrentHashMap<String ,RpcResponse>rpcContext,RpcRequest rpcRequest){
        this.rpcContext = rpcContext;
        this.rpcRequest = rpcRequest;
    }


    @Override
    public Object getData() {
        try {
            RpcResponse response;
            while (true){
                if(rpcContext.get(rpcRequest.getUuid()) != null){
                    response = rpcContext.get(rpcRequest.getUuid());
                    break;
                }
            }
            rpcContext.remove(rpcRequest.getUuid());
            return response.getData();
        }catch (Exception e){
            log.error("获取调用结果出错",e);
            throw new RpcException(RpcError.REQUEST_ERROR);
        }
    }

}
