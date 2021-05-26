package com.rpc.loadbalancer;

import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
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
    public RegistryInstance select(List<RegistryInstance> instances){
        if(instances.size() == 0){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        int sum = 0;
        for (RegistryInstance instance : instances){
            sum += instance.getWeight();
        }
        int randomWeight = random.nextInt(sum);
        int partSum = 0;
        for (RegistryInstance instance : instances) {
            partSum += instance.getWeight();
            if (randomWeight < partSum) {
                return instance;
            }
        }
        return null;
    }
}
