package com.rpc.server.nio;

import com.rpc.pojo.RpcRequest;
import com.rpc.pojo.RpcResponse;
import com.rpc.provider.ServiceProvider;
import com.rpc.server.RequestHandler;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class RequestHandlerThread implements Runnable{

    private final Socket socket;
    private final RequestHandler requestHandler;
    private final ServiceProvider serviceProvider;

    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceProvider.getService(interfaceName);
            Object result = requestHandler.handler(rpcRequest, service);
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("方法调用或返回时发生异常");
        }
    }
}
