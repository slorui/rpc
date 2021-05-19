package com.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * @author slorui
 * data 2021/5/19
 */
public class RandomLoadBalancer implements LoadBalancer {

    public static Random random = new Random();

    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(random.nextInt(instances.size()));
    }
}
