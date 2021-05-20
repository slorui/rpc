package com.rpc.loadbalancer;

import com.rpc.registry.instance.RegistryInstance;

import java.util.List;

/**
 * @author slorui
 * data 2021/5/19
 */
public interface LoadBalancer {

    RegistryInstance select(List<RegistryInstance> instances);

}
