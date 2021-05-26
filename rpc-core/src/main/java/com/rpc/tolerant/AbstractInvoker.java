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
public abstract class AbstractInvoker implements Invoker{

    LoadBalancer loadBalancer;
    RpcClient rpcClient;
    ConcurrentHashMap<String ,RpcResponse> rpcContext ;

    public AbstractInvoker(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    @Override
    public abstract Result invoke(RpcRequest rpcRequest, List<RegistryInstance> instances);

    @Override
    public void setRpcClient(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public void setRpcContext(ConcurrentHashMap<String ,RpcResponse> rpcContext) {
        this.rpcContext = rpcContext;
    }
}
