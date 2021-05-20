package com.slorui;

import com.rpc.annotation.ServiceScan;
import com.rpc.registry.NacosServiceRegistry;
import com.rpc.registry.RedisServiceRegistry;
import com.rpc.registry.ZookeeperServiceRegistry;
import com.rpc.server.netty.RpcNettyServer;

/**
 * @author slorui
 * data 2021/5/19
 */
@ServiceScan
public class TestRpcNettyServer {

    public static void main(String[] args) {
        RpcNettyServer rpcNettyServer = new RpcNettyServer("127.0.0.1",8081,new NacosServiceRegistry());
    }
}
