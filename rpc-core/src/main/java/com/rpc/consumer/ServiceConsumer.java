package com.rpc.consumer;

import com.rpc.registry.instance.RegistryInstance;

import java.util.List;

/**
 * @author slorui
 * data 2021/5/22
 */
public interface ServiceConsumer {

    List<RegistryInstance> getServices(String serviceName);

    void registerServices(String serviceName, List<RegistryInstance> instances);
}
