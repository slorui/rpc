package com.rpc.server.netty;


import com.rpc.pojo.RpcRequest;
import com.rpc.provider.ServiceProvider;
import com.rpc.server.RequestHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author slorui
 * data 2021/5/19
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final RequestHandler requestHandler;
    private final ServiceProvider serviceProvider;

    public NettyServerHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
        this.requestHandler = new RequestHandler();

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest){
        try {
            log.info("服务器接收到请求: {}", rpcRequest);
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceProvider.getService(interfaceName);
            Object result = requestHandler.handler(rpcRequest, service);
            ChannelFuture future = ctx.writeAndFlush(result);
            // 发生成功后关闭channel
            future.addListener(ChannelFutureListener.CLOSE);
        }finally {
            ReferenceCountUtil.release(rpcRequest);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
