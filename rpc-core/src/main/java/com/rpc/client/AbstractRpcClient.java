package com.rpc.client;

import com.rpc.consumer.DefaultServiceConsumer;
import com.rpc.consumer.ServiceConsumer;
import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.pojo.Result;
import com.rpc.pojo.RpcRequest;
import com.rpc.pojo.RpcResponse;
import com.rpc.provider.ServiceProvider;
import com.rpc.registry.ServiceRegistry;
import com.rpc.registry.instance.RegistryInstance;
import com.rpc.tolerant.Invoker;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author slorui
 * data 2021/5/22
 */
@Slf4j
public abstract class AbstractRpcClient implements RpcClient{

    protected final ServiceRegistry serviceRegistry;
    protected final ServiceConsumer serviceConsumer;
    protected final Invoker invoker;
    protected final ConcurrentHashMap<String ,RpcResponse>rpcContext = new ConcurrentHashMap<>();

    public AbstractRpcClient(ServiceRegistry serviceRegistry,ServiceConsumer serviceConsumer, Invoker invoker) {
        this.serviceRegistry = serviceRegistry;
        this.serviceConsumer = serviceConsumer;
        this.invoker = invoker;
        invoker.setRpcContext(rpcContext);
    }

    @Override
    public Result doRequest(RpcRequest rpcRequest) {
        // 获取服务
        List<RegistryInstance> instances = loopUpService(rpcRequest.getInterfaceName());
        if(instances == null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        // 集群容错
        return invoker.invoke(rpcRequest, instances);
    }

    private List<RegistryInstance> loopUpService(String serviceName) {
        // 本地获取服务
        List<RegistryInstance> instances = serviceConsumer.getServices(serviceName);
        if(instances == null){
            instances = serviceRegistry.loopUpService(serviceName);
            serviceConsumer.registerServices(serviceName, instances);
        }
        return instances;
    }

    @Override
    public abstract Result sendRequest(RpcRequest rpcRequest, RegistryInstance instance);

}
