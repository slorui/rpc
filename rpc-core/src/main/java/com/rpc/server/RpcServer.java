package com.rpc.server;

/**
 * @author slorui
 * data 2021/5/19
 */
public interface RpcServer {

    public void start();

    <T> void publishService(String serviceName, Object service);
}
