package com.rpc.pojo;

import com.rpc.enumeration.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author slorui
 * data 2021/5/19
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> implements Serializable,Result {

//    private static final long serialVersionUID = 1L;

    private Integer statusCode;

    private String message;

    private T data;

    @Override
    public T getData() {
        return data;
    }

    public static <T> RpcResponse<T> success(T data){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
