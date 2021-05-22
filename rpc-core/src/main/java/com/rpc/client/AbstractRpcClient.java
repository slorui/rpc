package com.rpc.client;

import com.rpc.consumer.DefaultServiceConsumer;
import com.rpc.consumer.ServiceConsumer;
import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.pojo.RpcRequest;
import com.rpc.provider.ServiceProvider;
import com.rpc.registry.ServiceRegistry;
import com.rpc.registry.instance.RegistryInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author slorui
 * data 2021/5/22
 */
@Slf4j
public abstract class AbstractRpcClient implements RpcClient{

    protected final ServiceRegistry serviceRegistry;
    protected final LoadBalancer loadBalancer;
    protected final ServiceConsumer serviceConsumer;

    public AbstractRpcClient(ServiceRegistry serviceRegistry, LoadBalancer loadBalancer, ServiceConsumer serviceProvider) {
        this.serviceRegistry = serviceRegistry;
        this.loadBalancer = loadBalancer;
        this.serviceConsumer = serviceProvider;
    }

    @Override
    public Object doRequest(RpcRequest rpcRequest) {
        // 获取服务
        List<RegistryInstance> instances = loopUpService(rpcRequest.getInterfaceName());

        if(instances == null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        // 负载均衡
        RegistryInstance select = loadBalancer.select(instances);
        // 集群容错
        Object result = sendRequest(rpcRequest, select);
        //
        return result;
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

    public abstract Object sendRequest(RpcRequest rpcRequest,RegistryInstance instance);

}
