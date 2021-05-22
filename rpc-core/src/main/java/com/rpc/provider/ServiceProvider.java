package com.rpc.provider;

import java.util.Iterator;

/**
 * @author slorui
 * data 2021/5/19
 */
public interface ServiceProvider {

    <T> void register(String interfaceName, T service, T instance);

    Object getService(String serviceName);

    Object getInstance(String serviceName);

    boolean isEmpty();

    Iterator<String> iterator();
}
