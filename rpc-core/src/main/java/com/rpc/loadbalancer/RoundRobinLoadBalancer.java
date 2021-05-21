package com.rpc.loadbalancer;

import com.rpc.registry.instance.RegistryInstance;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author slorui
 * data 2021/5/19
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    protected static class WeightedRoundRobin {
        private int weight;
        private AtomicLong current = new AtomicLong(0);
        private long lastUpdate;

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
            current.set(0);
        }

        public long increaseCurrent() {
            return current.addAndGet(weight);
        }

        public void sel(int total) {
            current.addAndGet(-1 * total);
        }

        public long getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(long lastUpdate) {
            this.lastUpdate = lastUpdate;
        }
    }

    private static final String CURRENT_WEIGHT = "currentWeight";

    private final ConcurrentHashMap<String ,ConcurrentHashMap<String ,WeightedRoundRobin>> methodWeightMap
        = new ConcurrentHashMap<>();

    @Override
    public RegistryInstance select(List<RegistryInstance> instances) {
        String serviceName = instances.get(0).getServiceName();
        ConcurrentHashMap<String, WeightedRoundRobin> map =
                methodWeightMap.computeIfAbsent(serviceName, k -> new ConcurrentHashMap<>(instances.size()));
        int totalWeight = 0;
        double maxCurrent = Integer.MIN_VALUE;
        RegistryInstance selectedInstance = null;
        WeightedRoundRobin selectedWrr = null;
        for (RegistryInstance instance: instances){
            String identifyString = instance.getServiceName() + instance.getIp() + instance.getPort();
            WeightedRoundRobin weightedRoundRobin =
                    map.computeIfAbsent(identifyString, k -> {
                        WeightedRoundRobin wrr = new WeightedRoundRobin();
                        wrr.setWeight((int) instance.getWeight());
                        return wrr;
                    });
            totalWeight += weightedRoundRobin.getWeight();
            long weight = weightedRoundRobin.increaseCurrent();
            if(weight > maxCurrent){
                maxCurrent = weight;
                selectedInstance = instance;
                selectedWrr = weightedRoundRobin;
            }
        }
        if(instances.size() != map.size()){
            map.entrySet().removeIf(map::contains);
        }
        if(selectedInstance != null){
            selectedWrr.sel(totalWeight);
            return selectedInstance;
        }
        return instances.get(0);
    }

}
