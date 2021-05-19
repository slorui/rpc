package com.rpc.provider;

/**
 * @author slorui
 * data 2021/5/19
 */
public interface ServiceProvider {

    <T> void register(String interfaceName, T service);

    Object getService(String serviceName);
}
