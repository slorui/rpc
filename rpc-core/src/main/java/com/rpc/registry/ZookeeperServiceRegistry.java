package com.rpc.registry;

import com.alibaba.fastjson.JSON;
import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.loadbalancer.RandomLoadBalancer;
import com.rpc.registry.instance.RegistryInstance;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author slorui
 * data 2021/5/20
 */
@Slf4j
public class ZookeeperServiceRegistry implements ServerRegistry{

    private static final String SERVER_ADDR = "127.0.0.1:2181";

    private static final int PORT = 6379;

    private final LoadBalancer loadBalancer;

    private static final ZkClient zkClient;

    private static final String ROOT_PATH = "/slorui-rpc";

    static {
        zkClient = new ZkClient(SERVER_ADDR);
        if(!zkClient.exists(ROOT_PATH)){
            zkClient.createPersistent(ROOT_PATH);
        }
    }

    public ZookeeperServiceRegistry(){
        this.loadBalancer = new RandomLoadBalancer();
    }

    public ZookeeperServiceRegistry(LoadBalancer loadBalancer){
        this.loadBalancer = loadBalancer;
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            String path = ROOT_PATH + "/" + serviceName;
            if(!zkClient.exists(path)){
                zkClient.createEphemeral(path);
            }
            zkClient.writeData(path, JSON.toJSONString(Collections.singleton(
                    new RegistryInstance(inetSocketAddress.getHostName(),
                            inetSocketAddress.getPort()))));
        } catch (Exception e) {
            log.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress loopUpService(String serviceName) {
        try {
            String obj = zkClient.readData(ROOT_PATH + "/" + serviceName);
            if(obj == null){
                log.error("未找到服务");
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            List<RegistryInstance> instances = JSON.parseArray(obj, RegistryInstance.class);
             RegistryInstance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        }catch (Exception e) {
            log.error("获取服务时有错误发生:", e);
        }
        return null;
    }
}
