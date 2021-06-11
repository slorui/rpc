package com.rpc.coder;

import com.rpc.pojo.RpcRequest;
import com.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author slorui
 * data 2021/5/19
 */
public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf byteBuf) throws Exception {

        byteBuf.writeInt(MAGIC_NUMBER);
        if(msg instanceof RpcRequest){
            byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
        }else{
            byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        byteBuf.writeInt(serializer.getCode());
        byte[] bytes =  serializer.serialize(msg);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
