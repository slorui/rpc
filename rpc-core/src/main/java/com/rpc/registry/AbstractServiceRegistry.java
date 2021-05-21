package com.rpc.registry;

import com.rpc.registry.instance.RegistryInstance;

import java.net.InetSocketAddress;

/**
 * @author slorui
 * data 2021/5/21
 */
public abstract class AbstractServiceRegistry implements ServiceRegistry{

    @Override
    public abstract void register(String serviceName, InetSocketAddress inetSocketAddress);

    @Override
    public abstract InetSocketAddress loopUpService(String serviceName);

    public RegistryInstance createRegistryInstance(String serviceName, InetSocketAddress inetSocketAddress){
        RegistryInstance registryInstance = new RegistryInstance();
        registryInstance.setServiceName(serviceName);
        registryInstance.setIp(inetSocketAddress.getHostName());
        registryInstance.setPort(inetSocketAddress.getPort());
        registryInstance.setWeight(100);
        return registryInstance;
    }

    public abstract void register(String serviceName, RegistryInstance RegistryInstance);
}
