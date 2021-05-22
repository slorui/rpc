package com.rpc.client;


import com.rpc.pojo.RpcRequest;
import com.rpc.pojo.RpcResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author slorui
 * data 2021/5/19
 */
@AllArgsConstructor
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private String host;
    private int port;
    private RpcClient rpcClient;

    public RpcClientProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
//        SocketClient rpcClient = new SocketClient(host,port);
        RpcResponse response = (RpcResponse) rpcClient.doRequest(rpcRequest);
        log.info("调用结果:{}",response);
        return response.getData();
    }
}
