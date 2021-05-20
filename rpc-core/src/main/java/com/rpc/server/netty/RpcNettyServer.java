package com.rpc.server.netty;


import com.rpc.coder.CommonDecoder;
import com.rpc.coder.CommonEncoder;
import com.rpc.registry.NacosServiceRegistry;
import com.rpc.registry.ServerRegistry;
import com.rpc.serializer.KryoSerializer;
import com.rpc.server.AbstractRpcServer;
import com.rpc.server.ShutDownHook;
import com.rpc.server.netty.NettyServerHandler;
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

    private final ServerRegistry serverRegistry;
    private final String host;
    private final int port;


    public RpcNettyServer(String host, int port,ServerRegistry serverRegistry) {
        this.host = host;
        this.port = port;
        this.serverRegistry = serverRegistry;
        scanService();
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
            ShutDownHook.shutDownHook().addClearAllHook();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            log.error("启动服务器时有错误发生: ", e);
        }finally {
            boos.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    @Override
    public <T> void publishService(String serviceName, Object service) {
        serverRegistry.register(serviceName,new InetSocketAddress(host, port));
    }
}
