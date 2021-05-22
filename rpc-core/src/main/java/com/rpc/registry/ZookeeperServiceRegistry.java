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
import org.I0Itec.zkclient.ZkClient;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author slorui
 * data 2021/5/20
 */
@Slf4j
public class ZookeeperServiceRegistry extends AbstractServiceRegistry {

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
            zkClient.writeData(path, JSON.toJSONString(
                    Collections.singleton(createRegistryInstance(serviceName, inetSocketAddress))));
        } catch (Exception e) {
            log.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public void register(String serviceName, RegistryInstance registryInstance) {
        try {
            String path = ROOT_PATH + "/" + serviceName;
            if(!zkClient.exists(path)){
                zkClient.createEphemeral(path);
            }
            zkClient.writeData(path, JSON.toJSONString(Collections.singleton(registryInstance)));
        } catch (Exception e) {
            log.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public List<RegistryInstance> loopUpService(String serviceName) {
        try {
            String obj = zkClient.readData(ROOT_PATH + "/" + serviceName);
            if(obj == null){
                log.error("未找到服务");
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            List<RegistryInstance> instances = JSON.parseArray(obj, RegistryInstance.class);
            return instances;
        }catch (Exception e) {
            log.error("获取服务时有错误发生:", e);
        }
        return null;
    }

    @Override
    public void clearRegistry() {
        List<String> children = zkClient.getChildren(ROOT_PATH);
        if(!children.isEmpty()) {
            for (String childPath : children) {
                try {
                    zkClient.delete(childPath);
                } catch (Exception e) {
                    log.error("注销服务 {} 失败", childPath, e);
                }
            }
        }
    }
}
