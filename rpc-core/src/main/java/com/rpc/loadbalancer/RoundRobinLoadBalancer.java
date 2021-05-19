package com.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author slorui
 * data 2021/5/19
 */
public class RoundRobinLoadBalancer implements LoadBalancer {


    private int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(++index % instances.size());
    }
}
