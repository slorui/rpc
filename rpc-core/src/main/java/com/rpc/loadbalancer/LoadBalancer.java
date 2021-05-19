package com.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author slorui
 * data 2021/5/19
 */
public interface LoadBalancer {

    Instance select(List<Instance> instances);

}
