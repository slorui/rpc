package com.rpc.server;


import com.rpc.enumeration.ResponseCode;
import com.rpc.pojo.RpcRequest;
import com.rpc.pojo.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author slorui
 * data 2021/5/19
 */
@Slf4j
public class RequestHandler {

    public Object handler(RpcRequest rpcRequest, Object service){
        Object result = null;
        try {
            result = invokeTargetMethod(rpcRequest, service);
            log.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
            return RpcResponse.success(result);
        } catch (IllegalAccessException |  InvocationTargetException e) {
            log.error("调用或发送时有错误发生：", e);;
        }
        return RpcResponse.fail(ResponseCode.FAIL);
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException {
        Method method = null;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}
