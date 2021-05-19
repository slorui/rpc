package com.rpc.provider;

import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

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
    private final Set<String > registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public  <T> void register(String interfaceName, T service) {
        String serviceName = service.getClass().getCanonicalName();
        if(registeredService.contains(interfaceName)){
            return;
        }
        synchronized (this){
            registeredService.add(interfaceName);
            serviceMap.put(interfaceName, service);
        }
    }

    @Override
    public Object getService(String serviceName) {
        return serviceMap.get(serviceName);
    }
}
