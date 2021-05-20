package com.rpc.loadbalancer;

import com.rpc.registry.instance.RegistryInstance;

import java.util.List;
import java.util.Random;

/**
 * @author slorui
 * data 2021/5/19
 */
public class RandomLoadBalancer implements LoadBalancer {

    public static Random random = new Random();


    @Override
    public RegistryInstance select(List<RegistryInstance> instances) {
        return instances.get(random.nextInt(instances.size()));
    }
}
