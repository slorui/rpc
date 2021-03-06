package com.rpc.registry;

import com.rpc.consumer.ServiceConsumer;
import com.rpc.provider.ServiceProvider;
import com.rpc.registry.instance.RegistryInstance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author slorui
 * data 2021/5/19
 */
public interface ServiceRegistry {

    void register(String serviceName, InetSocketAddress inetSocketAddress);

    void register(String serviceName, RegistryInstance instance);

    <T> List<RegistryInstance> loopUpService(String serviceName);

    void clearRegistry();

    void setServiceProvider(ServiceProvider serviceProvider);

    void setServiceConsumer(ServiceConsumer serviceConsumer);

    void subscribe();
}
