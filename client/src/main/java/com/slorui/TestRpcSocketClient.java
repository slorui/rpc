package com.slorui;

import com.rpc.client.RpcClientProxy;
import com.rpc.client.nio.RpcSocketClient;
import com.rpc.pojo.HelloObject;
import com.rpc.pojo.HelloService;

/**
 * @author slorui
 * data 2021/5/19
 */
public class TestRpcSocketClient {

    public static void main(String[] args) {
        RpcSocketClient rpcSocketClient = new RpcSocketClient("127.0.0.1",8081);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcSocketClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);

        System.out.println(helloService.hello(new HelloObject(1, "this is a message")));
        System.out.println(helloService.hello(new HelloObject(1, "this is a message")));

    }
}
