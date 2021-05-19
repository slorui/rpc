package com.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.loadbalancer.RandomLoadBalancer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author slorui
 * data 2021/5/19
 */
@Slf4j
public class NacosServiceRegistry implements ServerRegistry {

    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService ;

    private final LoadBalancer loadBalancer;

    static {
        try{
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    public NacosServiceRegistry(){
        this.loadBalancer = new RandomLoadBalancer();
    }

    public NacosServiceRegistry(LoadBalancer loadBalancer){
        this.loadBalancer = loadBalancer;
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.registerInstance(serviceName, inetSocketAddress.getHostName(),inetSocketAddress.getPort());
        } catch (NacosException e) {
            log.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress loopUpService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (Exception e) {
            log.error("获取服务时有错误发生:", e);
        }
        return null;
    }
}
