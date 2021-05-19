package com.rpc.server;

import com.rpc.annotation.Service;
import com.rpc.annotation.ServiceScan;
import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
import com.rpc.provider.DefaultServiceProvider;
import com.rpc.provider.ServiceProvider;
import com.rpc.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

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
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();

                } catch (IllegalAccessException | InstantiationException e) {
                    log.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if ("".equals(serviceName)){
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> klass : interfaces){
                        publishService(klass.getCanonicalName(), obj);
                        serviceProvider.register(klass.getCanonicalName(),obj);
                    }
                }else{
                    publishService(serviceName, obj);
                    serviceProvider.register(serviceName,obj);
                }
            }
        }
        synchronized (this){
            if(!isStart){
                start();
                isStart = !isStart;
            }
        }
    }
}
