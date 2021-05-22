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
public class DefaultServiceProvider extends AbstractServiceProvider {


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


}
