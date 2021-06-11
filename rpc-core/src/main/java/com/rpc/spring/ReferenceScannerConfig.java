package com.rpc.spring;

import com.rpc.client.RpcClientProxy;
import com.rpc.client.netty.RpcNettyClient;
import com.rpc.consumer.DefaultServiceConsumer;
import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.loadbalancer.RandomLoadBalancer;
import com.rpc.registry.ServiceRegistry;
import com.rpc.registry.ZookeeperServiceRegistry;
import com.rpc.tolerant.FailBackInvoker;
import com.rpc.tolerant.FailFastInvoker;
import com.rpc.tolerant.Invoker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author slorui
 * data 2021/6/11
 */
public class ReferenceScannerConfig implements BeanDefinitionRegistryPostProcessor {

    private RpcClientProxy proxy;

    private String basePackage;

    private String registryIp;

    private Class registryClass;

    private Class invokerClass;

    private Class loadBalanceClass;

    public void setProxy(RpcClientProxy proxy) {
        this.proxy = proxy;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setRegistryIp(String registryIp) {
        this.registryIp = registryIp;
    }

    public void setRegistryClass(Class registryClass) {
        this.registryClass = registryClass;
    }

    public void setInvokerClass(Class invokerClass) {
        this.invokerClass = invokerClass;
    }

    public void setLoadBalanceClass(Class loadBalanceClass) {
        this.loadBalanceClass = loadBalanceClass;
    }

    public ReferenceScannerConfig() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

    }

    private void createRpcClient() {
        ServiceRegistry serviceRegistry = null;
        Invoker invoker = null;
        try {
            Constructor registryConstructor = registryClass.getConstructor(String.class);
             serviceRegistry = (ServiceRegistry) registryConstructor.newInstance(registryIp);

            LoadBalancer loadBalancer = (LoadBalancer) loadBalanceClass.newInstance();

            Constructor invokerConstructor = invokerClass.getConstructor(LoadBalancer.class);
            invoker = (Invoker) invokerConstructor.newInstance(loadBalancer);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        RpcNettyClient rpcNettyClient = new RpcNettyClient(serviceRegistry,
                new DefaultServiceConsumer(),invoker);
        proxy = new RpcClientProxy(rpcNettyClient);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        createRpcClient();
        ReferenceScanner rpcScanner = new ReferenceScanner(beanDefinitionRegistry);
        rpcScanner.setProxy(proxy);
        rpcScanner.registerDefaultFilters();
        rpcScanner.doScan(this.basePackage);
    }



    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
