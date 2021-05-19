package com.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @author slorui
 * data 2021/5/19
 */
public interface ServerRegistry {

    void register(String serviceName, InetSocketAddress inetSocketAddress);

    InetSocketAddress loopUpService(String serviceName);
}
