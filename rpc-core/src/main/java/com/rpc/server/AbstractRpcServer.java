package com.rpc.server;

import com.rpc.annotation.Service;
import com.rpc.annotation.ServiceScan;
import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
import com.rpc.provider.DefaultServiceProvider;
import com.rpc.provider.ServiceProvider;
import com.rpc.registry.instance.RegistryInstance;
import com.rpc.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author slorui
 * data 2021/5/19
 */
@Slf4j
public abstract class AbstractRpcServer implements RpcServer{

    protected final ServiceProvider serviceProvider;
    private boolean isStart = false;

    public AbstractRpcServer(){
        this.serviceProvider = new DefaultServiceProvider();
    }

    public void scanService(){
        setServiceProvider(serviceProvider);

        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(ServiceScan.class)){
                log.error("启动类缺少@ServiceScan注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            log.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        String baskPackage = startClass.getAnnotation(ServiceScan.class).value();
        if ("".equals(baskPackage)){
            baskPackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(baskPackage);
        for (Class<?> clazz: classSet){
            if(clazz.isAnnotationPresent(Service.class)){
                Service annotation = clazz.getAnnotation(Service.class);
                String serviceName = annotation.name();
                double weight = annotation.weight();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (IllegalAccessException | InstantiationException e) {
                    log.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                RegistryInstance registryInstance = new RegistryInstance();
                registryInstance.setServiceName(serviceName);
                registryInstance.setWeight(weight);
                if ("".equals(serviceName)){
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> klass : interfaces){
                        publishService(klass.getCanonicalName(), registryInstance);
                        serviceProvider.register(klass.getCanonicalName(),obj, registryInstance);
                    }
                }else{
                    publishService(serviceName, registryInstance);
                    serviceProvider.register(serviceName,obj, registryInstance);
                }
            }
        }
        synchronized (this){
            if(!isStart){
                new ThreadPoolExecutor(1, 1, 0 , TimeUnit.SECONDS,
                        new LinkedBlockingDeque<>()).submit(this::start);
                isStart = !isStart;
            }
        }
    }

    public abstract void setServiceProvider(ServiceProvider serviceProvider);
}
