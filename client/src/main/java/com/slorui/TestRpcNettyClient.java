package com.slorui;

import com.rpc.client.RpcClientProxy;
import com.rpc.client.netty.RpcNettyClient;
import com.rpc.consumer.DefaultServiceConsumer;
import com.rpc.loadbalancer.RandomLoadBalancer;
import com.rpc.loadbalancer.RoundRobinLoadBalancer;
import com.rpc.pojo.HelloObject;
import com.rpc.pojo.HelloService;
import com.rpc.registry.NacosServiceRegistry;
import com.rpc.registry.RedisServiceRegistry;
import com.rpc.registry.ZookeeperServiceRegistry;
import com.rpc.tolerant.FailBackInvoker;

/**
 * @author slorui
 * data 2021/5/19
 */
public class TestRpcNettyClient {

    public static void main(String[] args) {

        RpcNettyClient rpcNettyClient = new RpcNettyClient(new ZookeeperServiceRegistry("127.0.0.1:2181"),
                new DefaultServiceConsumer(),new FailBackInvoker(new RandomLoadBalancer()));
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcNettyClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);

        helloService.hello(new HelloObject(1,"this is a message"));

        System.out.println("xx");


        System.out.println("xxxxxxx"+helloService.hello(new HelloObject(1, "this is a message")));
        System.out.println("xxxxxxx"+helloService.hello(new HelloObject(1, "this is a message")));
        System.out.println("xxxxxxx"+helloService.hello(new HelloObject(1, "this is a message")));
        System.out.println("xxxxxxx"+helloService.hello(new HelloObject(1, "this is a message")));
        System.out.println("xxxxxxx"+helloService.hello(new HelloObject(1, "this is a message")));
        System.out.println("xxxxxxx"+helloService.hello(new HelloObject(1, "this is a message")));
        System.out.println("xxxxxxx"+helloService.hello(new HelloObject(1, "this is a message")));



    }
}
