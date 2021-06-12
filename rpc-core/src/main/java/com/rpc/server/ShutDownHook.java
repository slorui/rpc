package com.rpc.server;

import com.rpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * @author slorui
 * data 2021/5/19
 */
@Slf4j
public class ShutDownHook {


    private static final ShutDownHook shutdownHook = new ShutDownHook();

    public static ShutDownHook shutDownHook(){
        return shutdownHook;
    }

    public void addClearAllHook(ServiceRegistry serviceRegistry){
        log.info("注册服务关闭钩子函数");
        Runtime.getRuntime().addShutdownHook(new Thread(serviceRegistry::clearRegistry));
    }

}
