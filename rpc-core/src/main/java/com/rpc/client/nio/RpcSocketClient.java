package com.rpc.client.nio;

import com.rpc.client.RpcClient;
import com.rpc.pojo.RpcRequest;
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
public class RpcSocketClient implements RpcClient {

    private String host;
    private int port;

    public RpcSocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

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
    public Object sendRequest(RpcRequest rpcRequest) {
        return sendRequest(rpcRequest,host,port);
    }
}
