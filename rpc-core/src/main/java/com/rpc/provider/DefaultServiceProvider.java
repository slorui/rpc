package com.rpc.provider;

import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author slorui
 * data 2021/5/19
 */
@Slf4j
public class DefaultServiceProvider implements ServiceProvider {


    private final Map<String ,Object> serviceMap = new ConcurrentHashMap<>();
    private final Map<String ,Object> instanceMap = new ConcurrentHashMap<>();
    private final Set<String > registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public  <T> void register(String interfaceName, T service, T instance) {
        if(registeredService.contains(interfaceName)){
            return;
        }
        synchronized (this){
            registeredService.add(interfaceName);
            serviceMap.put(interfaceName, service);
            instanceMap.put(interfaceName, instance);
        }
    }

    @Override
    public boolean isEmpty(){
        return registeredService.isEmpty();
    }

    @Override
    public Iterator<String> iterator(){
        return registeredService.iterator();
    }

    @Override
    public Object getService(String serviceName) {
        return serviceMap.get(serviceName);
    }

    @Override
    public Object getInstance(String serviceName) {
        return instanceMap.get(serviceName);
    }

}
