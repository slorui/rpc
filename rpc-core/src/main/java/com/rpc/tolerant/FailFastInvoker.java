package com.rpc.tolerant;

import com.rpc.exception.RpcException;
import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.pojo.Result;
import com.rpc.pojo.RpcRequest;
import com.rpc.pojo.RpcResponse;
import com.rpc.registry.instance.RegistryInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author slorui
 * data 2021/5/25
 */
@Slf4j
public class FailFastInvoker extends AbstractInvoker {

    public FailFastInvoker(LoadBalancer loadBalancer) {
        super(loadBalancer);
    }

    @Override
    public Result invoke(RpcRequest rpcRequest, List<RegistryInstance> instances) {
        RegistryInstance select = loadBalancer.select(instances);
        try {
            return rpcClient.sendRequest(rpcRequest, select);
        }catch (RpcException e){
            log.error("",e);
        }
        return null;
    }
}
