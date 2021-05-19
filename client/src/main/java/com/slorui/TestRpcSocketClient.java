package com.slorui;

import com.rpc.client.RpcClientProxy;
import com.rpc.pojo.HelloObject;
import com.rpc.pojo.HelloService;

/**
 * @author slorui
 * data 2021/5/19
 */
public class TestRpcSocketClient {

    public static void main(String[] args) {
        RpcSocketClient rpcSocketClient = new RpcSocketClient("127.0.0.1",8080);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcSocketClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);

        helloService.hello(new HelloObject(1,"this is a message"));
        helloService.hello(new HelloObject(1,"this is a message"));

    }
}
