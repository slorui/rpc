package com.rpc.client.netty;


import com.rpc.client.AbstractRpcClient;
import com.rpc.coder.CommonDecoder;
import com.rpc.coder.CommonEncoder;
import com.rpc.consumer.DefaultServiceConsumer;
import com.rpc.consumer.ServiceConsumer;
import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.loadbalancer.RandomLoadBalancer;
import com.rpc.pojo.*;
import com.rpc.registry.ServiceRegistry;
import com.rpc.registry.instance.RegistryInstance;
import com.rpc.serializer.KryoSerializer;
import com.rpc.cluster.FailFastInvoker;
import com.rpc.cluster.Invoker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author slorui
 * data 2021/5/19
 */
@Slf4j
public class RpcNettyClient extends AbstractRpcClient {

    private static final Bootstrap BOOTSTRAP;


    static {
        EventLoopGroup group = new NioEventLoopGroup();
        BOOTSTRAP = new Bootstrap();
        BOOTSTRAP.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new CommonDecoder());
//                        pipeline.addLast(new CommonEncoder(new JsonSerializer()));
                        pipeline.addLast(new CommonEncoder(new KryoSerializer()));
                        pipeline.addLast(new NettyClientHandler());
                    }
                });
    }

    public RpcNettyClient(ServiceRegistry serviceRegistry) {
        this(serviceRegistry, new RandomLoadBalancer());

    }

    public RpcNettyClient(ServiceRegistry serviceRegistry, LoadBalancer loadBalancer) {
        this(serviceRegistry, loadBalancer, new DefaultServiceConsumer());
    }

    public RpcNettyClient(ServiceRegistry serviceRegistry, LoadBalancer loadBalancer, ServiceConsumer serviceProvider){
        this(serviceRegistry, serviceProvider, new FailFastInvoker(loadBalancer));
    }

    public RpcNettyClient(ServiceRegistry serviceRegistry, LoadBalancer loadBalancer, ServiceConsumer serviceProvider,
                          Invoker invoker){
        this(serviceRegistry, serviceProvider, invoker);
        invoker.setLoadBalancer(loadBalancer);
    }

    public RpcNettyClient(ServiceRegistry serviceRegistry,ServiceConsumer serviceConsumer, Invoker invoker) {
        super(serviceRegistry, serviceConsumer, invoker);
        if(invoker.getLoadBalancer() == null){
            invoker.setLoadBalancer(new RandomLoadBalancer());
        }
        invoker.setRpcClient(this);

    }

    @Override
    public Result sendRequest(RpcRequest rpcRequest, RegistryInstance instance) {
        ChannelFuture connectFuture = null;
        try {
            connectFuture = BOOTSTRAP.connect(instance.getIp(), instance.getPort());
            boolean connectAwait = connectFuture.await(3, TimeUnit.SECONDS);
            if(!connectAwait){
                throw new RpcException(RpcError.CONNECT_TIME_OUT);
            }
            log.info("客户端连接到服务器 {}:{}", instance.getIp(), instance.getPort());
            Channel channel = connectFuture.channel();
            if(channel != null){
                // 将请求写出
                channel.writeAndFlush(rpcRequest).addListener(result -> {
                    if (result.isSuccess()) {
                        log.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        log.error("发送消息时有错误发生: ", result.cause());
                        throw new RpcException(RpcError.SEND_MESSAGE_ERROR);
                    }
                });
//                ChannelFuture future = channel.closeFuture();
//                rpcContext.put(rpcRequest.getUuid(), future);
//                rpcContext.put(rpcRequest, future);
//                channel.closeFuture().sync();
                // 等待服务器端关闭channel
                ChannelFuture closeFuture = channel.closeFuture();
                boolean closeAwait = closeFuture.await(3, TimeUnit.SECONDS);
                if(!closeAwait){
                    throw new RpcException(RpcError.REQUEST_TIME_OUT);
                }
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse response = channel.attr(key).get();
                rpcContext.put(rpcRequest.getUuid(),response);
                return new SyncRpcResponse(rpcRequest, response);
            }
        } catch (Exception e) {
            log.error("发送消息时有错误发生: ", e);
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return null;
    }
}
