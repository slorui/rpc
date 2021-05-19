package com.slorui;

import com.rpc.annotation.Service;
import com.rpc.annotation.ServiceScan;
import com.rpc.server.nio.RpcSocketServer;

/**
 * @author slorui
 * data 2021/5/19
 */
@ServiceScan
public class TestRpcSocketServer {

    public static void main(String[] args) {
        RpcSocketServer rpcSocketServer = new RpcSocketServer("127.0.0.1",8080);
    }
}
