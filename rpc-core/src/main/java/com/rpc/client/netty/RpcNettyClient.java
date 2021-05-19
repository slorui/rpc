package com.rpc.client.netty;


import com.rpc.client.RpcClient;
import com.rpc.coder.CommonDecoder;
import com.rpc.coder.CommonEncoder;
import com.rpc.exception.RpcError;
import com.rpc.exception.RpcException;
import com.rpc.pojo.RpcRequest;
import com.rpc.pojo.RpcResponse;
import com.rpc.provider.DefaultServiceProvider;
import com.rpc.provider.ServiceProvider;
import com.rpc.registry.NacosServiceRegistry;
import com.rpc.serializer.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author slorui
 * data 2021/5/19
 */
@Slf4j
public class RpcNettyClient implements RpcClient {

    private static final Bootstrap bootstrap;

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
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

    private NacosServiceRegistry serviceRegistry;
    private ServiceProvider serviceProvider;

    public RpcNettyClient() {
        this.serviceProvider = new DefaultServiceProvider();
        this.serviceRegistry = new NacosServiceRegistry();
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        InetSocketAddress service;
        ChannelFuture future;
        try {
            service = (InetSocketAddress) serviceProvider.getService(rpcRequest.getInterfaceName());
            if (service == null) {
                service = serviceRegistry.loopUpService(rpcRequest.getInterfaceName());
                if (service == null) {
                    throw new RpcException(RpcError.SERVICE_NOT_FOUND);
                }
                serviceProvider.register(rpcRequest.getInterfaceName(), service);
            }
            future = bootstrap.connect(service.getAddress(), service.getPort()).sync();
            log.info("客户端连接到服务器 {}:{}", service.getAddress(), service.getPort());
            Channel channel = future.channel();
            if(channel != null){
                // 将请求写出
                channel.writeAndFlush(rpcRequest).addListener(result -> {
                    if(result.isSuccess()){
                        log.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    }else {
                        log.error("发送消息时有错误发生: ", result.cause());
                    }
                });
                channel.closeFuture().sync();
                // 阻塞等待结果
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse response = channel.attr(key).get();
                return response;
            }

        } catch (InterruptedException e) {
            log.error("发送消息时有错误发生: ", e);
        }
        return null;
    }
}
