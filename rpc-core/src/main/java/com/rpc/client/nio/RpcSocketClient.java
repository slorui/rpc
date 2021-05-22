package com.rpc.client.nio;

import com.rpc.client.AbstractRpcClient;
import com.rpc.client.RpcClient;
import com.rpc.consumer.DefaultServiceConsumer;
import com.rpc.consumer.ServiceConsumer;
import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.loadbalancer.RandomLoadBalancer;
import com.rpc.pojo.RpcRequest;
import com.rpc.registry.ServiceRegistry;
import com.rpc.registry.instance.RegistryInstance;
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
        this(host, port, null);
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
        super(serviceRegistry, loadBalancer, serviceProvider);
    }

    /**
     * 需要添加服务注册和发现
     * @param rpcRequest
     * @param host
     * @param port
     * @return
     */
    public Object sendRequest(RpcRequest rpcRequest, String host, int port){
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("调用时发送错误",e);
            return null;
        }
    }


    @Override
    public Object sendRequest(RpcRequest rpcRequest, RegistryInstance instance) {
        return sendRequest(rpcRequest,host,port);
    }
}
