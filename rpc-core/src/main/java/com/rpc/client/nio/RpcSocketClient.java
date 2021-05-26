package com.rpc.client.nio;

import com.rpc.client.AbstractRpcClient;
import com.rpc.client.RpcClient;
import com.rpc.consumer.DefaultServiceConsumer;
import com.rpc.consumer.ServiceConsumer;
import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.loadbalancer.RandomLoadBalancer;
import com.rpc.pojo.Result;
import com.rpc.pojo.RpcRequest;
import com.rpc.pojo.RpcResponse;
import com.rpc.registry.NacosServiceRegistry;
import com.rpc.registry.ServiceRegistry;
import com.rpc.registry.instance.RegistryInstance;
import com.rpc.tolerant.FailFastInvoker;
import com.rpc.tolerant.Invoker;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author slorui
 * data 2021/5/19
 */
@Slf4j
public class RpcSocketClient extends AbstractRpcClient {

    private String host;
    private int port;

    public RpcSocketClient(String host, int port){
        this(host, port, new NacosServiceRegistry());
    }

    public RpcSocketClient(String host, int port,ServiceRegistry serviceRegistry) {
        this(serviceRegistry, new RandomLoadBalancer());
        this.host = host;
        this.port = port;
    }

    public RpcSocketClient(ServiceRegistry serviceRegistry, LoadBalancer loadBalancer) {
        this(serviceRegistry, loadBalancer, new DefaultServiceConsumer());
    }

    public RpcSocketClient(ServiceRegistry serviceRegistry, LoadBalancer loadBalancer, ServiceConsumer serviceProvider){
        this(serviceRegistry, serviceProvider, new FailFastInvoker(loadBalancer));
    }

    public RpcSocketClient(ServiceRegistry serviceRegistry, LoadBalancer loadBalancer, ServiceConsumer serviceProvider,
                           Invoker invoker){
        this(serviceRegistry, serviceProvider, invoker);
        invoker.setLoadBalancer(loadBalancer);
    }

    public RpcSocketClient(ServiceRegistry serviceRegistry,ServiceConsumer serviceConsumer, Invoker invoker) {
        super(serviceRegistry, serviceConsumer, invoker);
        if(invoker.getLoadBalancer() == null){
            invoker.setLoadBalancer(new RandomLoadBalancer());
        }
        invoker.setRpcClient(this);
    }

    /**
     * 需要添加服务注册和发现
     * @param rpcRequest
     * @param host
     * @param port
     * @return
     */
    public Result sendRequest(RpcRequest rpcRequest, String host, int port){
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return (Result) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("调用时发送错误",e);
            throw new RuntimeException();
        }
    }


    @Override
    public Result sendRequest(RpcRequest rpcRequest, RegistryInstance instance) {
        return sendRequest(rpcRequest,host,port);
    }
}
