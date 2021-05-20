package com.rpc.loadbalancer;

import com.rpc.registry.instance.RegistryInstance;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author slorui
 * data 2021/5/19
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private int index = 0;

    @Override
    public RegistryInstance select(List<RegistryInstance> instances) {
        return instances.get(++index % instances.size());
    }

}
