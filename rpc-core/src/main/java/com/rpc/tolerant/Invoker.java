package com.rpc.tolerant;

import com.rpc.client.RpcClient;
import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.pojo.Result;
import com.rpc.pojo.RpcRequest;
import com.rpc.pojo.RpcResponse;
import com.rpc.registry.instance.RegistryInstance;
import io.netty.channel.ChannelFuture;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author slorui
 * data 2021/5/25
 */
public interface Invoker {

    Result invoke(RpcRequest rpcRequest, List<RegistryInstance> instances);

    void setLoadBalancer(LoadBalancer loadBalancer);

    LoadBalancer getLoadBalancer();

    void setRpcClient(RpcClient rpcClient);

    void setRpcContext(ConcurrentHashMap<String ,RpcResponse>rpcContext);
}
