package com.rpc.spring;

import com.rpc.client.RpcClientProxy;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author slorui
 * data 2021/6/11
 */
public class ReferenceFactoryBean<T> implements FactoryBean<T> {

    Class<T> referenceInterface;

    RpcClientProxy proxy;

    public ReferenceFactoryBean(Class<T> referenceInterface) {
        this.referenceInterface = referenceInterface;
    }

    public void setProxy(RpcClientProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public T getObject() throws Exception {
        return proxy.getProxy(referenceInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.referenceInterface;
    }
}
