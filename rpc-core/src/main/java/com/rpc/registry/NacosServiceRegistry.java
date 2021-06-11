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
import java.util.Iterator;
import java.util.List;

/**
 * @author slorui
 * data 2021/5/19
 */
@Slf4j
public class NacosServiceRegistry extends AbstractServiceRegistry {

    private NamingService namingService;

    private final LoadBalancer loadBalancer;


    public NacosServiceRegistry(){
        this(new RandomLoadBalancer());
    }

    public NacosServiceRegistry(String addr) {
        this(addr, new RandomLoadBalancer());
    }
    public NacosServiceRegistry(LoadBalancer loadBalancer) {
        this("127.0.0.1:8848", loadBalancer);
    }

    public NacosServiceRegistry(String addr, LoadBalancer loadBalancer) {
        try {
            namingService = NamingFactory.createNamingService(addr);
        } catch (NacosException e) {
            e.printStackTrace();
        }
        this.loadBalancer = new RandomLoadBalancer();
    }



    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            Instance instance = new Instance();
            instance.setIp(inetSocketAddress.getHostName());
            instance.setPort(inetSocketAddress.getPort());
            instance.setWeight(100);
            instance.setServiceName(serviceName);
            namingService.registerInstance(serviceName, instance);
        } catch (NacosException e) {
            log.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public void register(String serviceName, RegistryInstance registryInstance) {
        Instance instance = new Instance();
        try {
            BeanUtil.copyBean(instance, registryInstance);
            namingService.registerInstance(serviceName, instance);
        } catch (IntrospectionException | NacosException e) {
            log.error("注册服务时有错误发生:", e);
        }
    }

    @Override
    public List<RegistryInstance> loopUpService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            if (instances == null){
                return null;
            }
//            RegistryInstance instance = loadBalancer.select(copyInstance(instances));
            return copyInstance(instances);
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

    @Override
    public void clearRegistry() {
        if(!serviceProvider.isEmpty()) {
            Iterator<String> iterator = serviceProvider.iterator();
            while(iterator.hasNext()) {
                String serviceName = iterator.next();
                Object service = serviceProvider.getInstance(serviceName);
                Instance instance = new Instance();
                try {
                    BeanUtil.copyBean(instance, service);
                    namingService.deregisterInstance(serviceName, instance);
                } catch (NacosException | IntrospectionException e) {
                    log.error("注销服务 {} 失败", serviceName, e);
                }
            }
        }
    }
}
