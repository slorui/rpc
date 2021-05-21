package com.rpc.server;

import com.rpc.registry.instance.RegistryInstance;

/**
 * @author slorui
 * data 2021/5/19
 */
public interface RpcServer {

    public void start();

    <T> void publishService(String serviceName, RegistryInstance registryInstance);
}
