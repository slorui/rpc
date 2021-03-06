package com.rpc.server.nio;

import com.rpc.consumer.ServiceConsumer;
import com.rpc.provider.ServiceProvider;
import com.rpc.registry.NacosServiceRegistry;
import com.rpc.registry.ServiceRegistry;
import com.rpc.registry.instance.RegistryInstance;
import com.rpc.server.AbstractRpcServer;
import com.rpc.server.RequestHandler;
import com.rpc.server.ShutDownHook;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author slorui
 * data 2021/5/19
 */
@Slf4j
public class RpcSocketServer extends AbstractRpcServer {


    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private final ExecutorService threadPoll;
    private RequestHandler requestHandler = new RequestHandler();
    private final String host;
    private final int port;
    private final ServiceRegistry serviceRegistry;

    public RpcSocketServer(String host,int port){
        this.host = host;
        this.port = port;
        BlockingQueue<Runnable> worker = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory factory = Executors.defaultThreadFactory();
        threadPoll = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, worker, factory);
        this.serviceRegistry = new NacosServiceRegistry();
        scanService();
    }

    @Override
    public void setServiceProvider(ServiceProvider serviceProvider) {
        serviceRegistry.setServiceProvider(serviceProvider);
    }

    @Override
    public void serServiceConsumer(ServiceConsumer serviceConsumer) {
        serviceRegistry.setServiceConsumer(serviceConsumer);
    }

    @Override
    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("服务正在启动");
            ShutDownHook.shutDownHook().addClearAllHook(serviceRegistry);
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                log.info("消费者连接：{}：{}", socket.getInetAddress(),socket.getPort());
                threadPoll.execute(new RequestHandlerThread(socket, requestHandler, serviceProvider));
            }
            threadPoll.shutdown();
        } catch (IOException e) {
            log.error("服务器启动时有错误发生:", e);
        }
    }

    @Override
    public <T> void publishService(String serviceName, RegistryInstance instance) {
        instance.setIp(host);
        instance.setPort(port);
        serviceRegistry.register(serviceName,instance);
    }
}
