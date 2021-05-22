package com.rpc.server.netty;


import com.rpc.coder.CommonDecoder;
import com.rpc.coder.CommonEncoder;
import com.rpc.provider.ServiceProvider;
import com.rpc.registry.NacosServiceRegistry;
import com.rpc.registry.ServiceRegistry;
import com.rpc.registry.instance.RegistryInstance;
import com.rpc.serializer.KryoSerializer;
import com.rpc.server.AbstractRpcServer;
import com.rpc.server.ShutDownHook;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author slorui
 * data 2021/5/19
 */
@Slf4j
public class RpcNettyServer extends AbstractRpcServer {

    private final ServiceRegistry serviceRegistry;
    private final String host;
    private final int port;


    public RpcNettyServer(String host, int port) {
        this(host, port, new NacosServiceRegistry());
    }

    public RpcNettyServer(String host, int port, ServiceRegistry serviceRegistry) {
        this.host = host;
        this.port = port;
        this.serviceRegistry = serviceRegistry;
        scanService();
    }

    @Override
    public void setServiceProvider(ServiceProvider serviceProvider) {
        serviceRegistry.setServiceProvider(serviceProvider);
    }

    @Override
    public void start() {
        log.info("正在启动服务器");
        EventLoopGroup boos = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boos, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
//                            pipeline.addLast(new CommonEncoder(new JsonSerializer()));
                            pipeline.addLast(new CommonEncoder(new KryoSerializer()));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler(serviceProvider));
                        }
                    });
            ChannelFuture future = bootstrap.bind(port).sync();
            ShutDownHook.shutDownHook().addClearAllHook(serviceRegistry);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动服务器时有错误发生: ", e);
        }finally {
            boos.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    @Override
    public <T> void publishService(String serviceName, RegistryInstance instance) {
        instance.setIp(host);
        instance.setPort(port);
        serviceRegistry.register(serviceName,instance);
    }
}
