package com.rpc.provider;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author slorui
 * data 2021/5/22
 */
public abstract class AbstractServiceProvider implements ServiceProvider{

    protected final Map<String ,Object> serviceMap = new ConcurrentHashMap<>();
    protected final Map<String ,Object> instanceMap = new ConcurrentHashMap<>();
    protected final Set<String > registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public abstract  <T> void register(String interfaceName, T service, T instance);

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
