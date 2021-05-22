package com.rpc.registry;

import com.rpc.provider.ServiceProvider;
import com.rpc.registry.instance.RegistryInstance;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * @author slorui
 * data 2021/5/21
 */
public abstract class AbstractServiceRegistry implements ServiceRegistry{

    protected ServiceProvider serviceProvider;

    @Override
    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

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

    @Override
    public abstract void register(String serviceName, RegistryInstance RegistryInstance);

    @Override
    public abstract void clearRegistry();
}
