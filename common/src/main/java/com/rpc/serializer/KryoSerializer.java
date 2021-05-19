package com.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.rpc.exception.SerializeException;
import com.rpc.pojo.RpcRequest;
import com.rpc.pojo.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author slorui
 * data 2021/5/19
 */
@Slf4j
public class KryoSerializer implements CommonSerializer{

    private static final ThreadLocal<Kryo> threadLocal = ThreadLocal.withInitial(() ->{
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (Output output = new Output(new ByteArrayOutputStream())) {
            Kryo kryo = threadLocal.get();
            kryo.writeObject(output, obj);
            threadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            log.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (Input input = new Input(new ByteArrayInputStream(bytes))) {
            Kryo kryo = threadLocal.get();
            Object obj = kryo.readObject(input, clazz);
            threadLocal.remove();
            return obj;
        }catch (Exception e){
            log.error("反序列化时有错误发生:", e);
            throw new SerializeException("反序列化时有错误发生");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
