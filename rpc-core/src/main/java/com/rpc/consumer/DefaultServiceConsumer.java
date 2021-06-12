package com.rpc.consumer;

import com.rpc.provider.ServiceProvider;
import com.rpc.registry.ServiceRegistry;
import com.rpc.registry.instance.RegistryInstance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author slorui
 * data 2021/5/22
 */
public class DefaultServiceConsumer implements ServiceConsumer {


    private final Map<String , List<RegistryInstance>> serviceMap = new ConcurrentHashMap<>();

    private ServiceRegistry serviceRegistry;

    private volatile Thread thread;

    public void isSubscribe(){
        if(thread == null){
            synchronized (this){
                if(thread == null){
                    thread = new Thread(() -> serviceRegistry.subscribe());
                    thread.start();
                }
            }
        }
    }

    @Override
    public List<RegistryInstance> getServices(String serviceName) {
        isSubscribe();
        return serviceMap.get(serviceName);
    }

    @Override
    public void registerServices(String serviceName, List<RegistryInstance> instances) {
        processCache(serviceName, instances);
    }

    public void processCache(String serviceName, List<RegistryInstance> instances){
        if(instances == null){
            serviceMap.remove(serviceName);
            return;
        }
        serviceMap.put(serviceName, instances);
    }

    @Override
    public void setRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
}
