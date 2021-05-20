package com.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.loadbalancer.RandomLoadBalancer;
import com.rpc.registry.instance.RegistryInstance;
import com.rpc.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;

import java.beans.IntrospectionException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            RegistryInstance instance = loadBalancer.select(copyInstance(instances));
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (Exception e) {
            log.error("获取服务时有错误发生:", e);
        }
        return null;
    }

    public List<RegistryInstance> copyInstance(List<Instance> instances) throws IntrospectionException {
        List<RegistryInstance> registryInstances = new ArrayList<>();
        for(Instance instance : instances){
            RegistryInstance registryInstance = new RegistryInstance();
            BeanUtil.copyBean(registryInstance, instance);
            registryInstances.add(registryInstance);
        }
        return registryInstances;
    }
}
