package com.rpc.pojo;

import lombok.*;

import java.io.Serializable;

/**
 * @author slorui
 * data 2021/5/19
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {

//    private static final long serialVersionUID = 1L;

    private String uuid;

    private String interfaceName;

    private String methodName;

    private Object[] parameters;

    private Class<?>[] paramTypes;

    private int retries;
}
