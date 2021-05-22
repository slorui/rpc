package com.rpc.registry;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.loadbalancer.RandomLoadBalancer;
import com.rpc.registry.instance.RegistryInstance;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author slorui
 * data 2021/5/20
 */
@Slf4j
public class RedisServiceRegistry extends AbstractServiceRegistry {

    private static final String HOST = "127.0.0.1";

    private static final int PORT = 6379;

    private final LoadBalancer loadBalancer;

    private static final Jedis jedis;

    static {
        jedis = new Jedis(new HostAndPort(HOST,PORT));
    }

    public RedisServiceRegistry(){
        this.loadBalancer = new RandomLoadBalancer();
    }

    public RedisServiceRegistry(LoadBalancer loadBalancer){
        this.loadBalancer = loadBalancer;
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            jedis.zadd(serviceName,1, JSON.toJSONString(createRegistryInstance(serviceName ,inetSocketAddress)));
        } catch (Exception e) {
            log.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public void register(String serviceName, RegistryInstance registryInstance) {
        try {
            jedis.zadd(serviceName,1, JSON.toJSONString(registryInstance));
        } catch (Exception e) {
            log.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress loopUpService(String serviceName) {
        try {
            List<RegistryInstance> instances = jedis.zrange(serviceName, 0, -1)
                    .stream().map(str -> JSON.parseObject(str, RegistryInstance.class)).collect(Collectors.toList());
            RegistryInstance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        }catch (Exception e) {
            log.error("获取服务时有错误发生:", e);
        }
        return null;
    }

    @Override
    public void clearRegistry() {
        if(!serviceProvider.isEmpty()) {
            Iterator<String> iterator = serviceProvider.iterator();
            while(iterator.hasNext()) {
                String serviceName = iterator.next();
                RegistryInstance service = (RegistryInstance) serviceProvider.getInstance(serviceName);
                try {
                    jedis.zrem(serviceName, JSON.toJSONString(service));
                } catch (Exception e) {
                    log.error("注销服务 {} 失败", serviceName, e);
                }
            }
        }
    }
}
