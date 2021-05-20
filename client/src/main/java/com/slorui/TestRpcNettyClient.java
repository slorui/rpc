package com.slorui;

import com.rpc.client.RpcClientProxy;
import com.rpc.client.netty.RpcNettyClient;
import com.rpc.pojo.HelloObject;
import com.rpc.pojo.HelloService;
import com.rpc.registry.NacosServiceRegistry;
import com.rpc.registry.RedisServiceRegistry;
import com.rpc.registry.ZookeeperServiceRegistry;

/**
 * @author slorui
 * data 2021/5/19
 */
public class TestRpcNettyClient {

    public static void main(String[] args) {

        RpcNettyClient rpcNettyClient = new RpcNettyClient(new NacosServiceRegistry());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcNettyClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);

        helloService.hello(new HelloObject(1,"this is a message"));
        helloService.hello(new HelloObject(1,"this is a message"));

    }
}
