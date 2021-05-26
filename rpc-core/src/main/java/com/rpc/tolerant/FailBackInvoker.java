package com.rpc.tolerant;


import com.rpc.exception.RpcException;
import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.pojo.AsyncRpcResponse;
import com.rpc.pojo.Result;
import com.rpc.pojo.RpcRequest;
import com.rpc.registry.instance.RegistryInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author slorui
 * data 2021/5/25
 */
@Slf4j
public class FailBackInvoker extends AbstractInvoker{

    private static final long RETRY_FAILED_PERIOD = 500;

    private final int retries;

    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> retryFuture = null;

    private final ConcurrentHashMap<RpcRequest,List<RegistryInstance>> failed = new ConcurrentHashMap<>();

    public FailBackInvoker(LoadBalancer loadBalancer) {
        super(loadBalancer);
        this.retries = 3;

    }

    @Override
    public Result invoke(RpcRequest rpcRequest, List<RegistryInstance> instances) {
        try {
            RegistryInstance select = loadBalancer.select(instances);
            return rpcClient.sendRequest(rpcRequest, select);
        } catch (RpcException e) {
            log.error("Failback to invoke method {}, wait for retry in background. Ignored exception: {}"
                    , rpcRequest.getInterfaceName(), e.getMessage());
            addFailed(rpcRequest, instances);
            return new AsyncRpcResponse(rpcContext, rpcRequest);
        }
    }

    private void addFailed(RpcRequest rpcRequest, List<RegistryInstance> instances) {
        if(retryFuture == null){
            synchronized (this){
                if(retryFuture == null){
                    retryFuture = service.scheduleWithFixedDelay(this::retryFailed,
                            0, RETRY_FAILED_PERIOD, TimeUnit.MILLISECONDS);
                }
            }
        }
        failed.put(rpcRequest, instances);
    }

    private void retryFailed() {
        if(failed.size() == 0){
            return;
        }
        for (Map.Entry<RpcRequest, List<RegistryInstance>> entry :
                new HashMap<>(failed).entrySet()){
            RpcRequest rpcRequest = entry.getKey();
            List<RegistryInstance> instances = entry.getValue();
            try {
                this.invoke(rpcRequest, instances);
                failed.remove(rpcRequest);
            }catch (Exception e){
                int retries = rpcRequest.getRetries();
                if(retries++ < this.retries){
                    rpcRequest.setRetries(retries);
                }else{
                    failed.remove(rpcRequest);
                    log.error("Failed retry to invoke method by {} times, give up the task",retries);
                }
                log.error("Failed retry to invoke method {}, waiting again.",
                        rpcRequest.getInterfaceName(),e);
            }
        }
    }

}
