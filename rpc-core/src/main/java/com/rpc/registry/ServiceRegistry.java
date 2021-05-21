package com.rpc.registry;

import com.rpc.registry.instance.RegistryInstance;

import java.net.InetSocketAddress;

/**
 * @author slorui
 * data 2021/5/19
 */
public interface ServiceRegistry {

    void register(String serviceName, RegistryInstance instance);

    InetSocketAddress loopUpService(String serviceName);
}
