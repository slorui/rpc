package com.slorui;

import com.rpc.annotation.ServiceScan;
import com.rpc.registry.NacosServiceRegistry;
import com.rpc.registry.RedisServiceRegistry;
import com.rpc.registry.ZookeeperServiceRegistry;
import com.rpc.server.netty.RpcNettyServer;


import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * @author slorui
 * data 2021/5/19
 */
@ServiceScan
public class TestRpcNettyServer {

    public static void main(String[] args) {

        LinkedHashMap<Object, Object> map = new LinkedHashMap<>();

        RpcNettyServer rpcNettyServer = new RpcNettyServer("127.0.0.1",8081,
                new ZookeeperServiceRegistry());
    }
}
