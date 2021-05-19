package com.rpc.exception;

import lombok.AllArgsConstructor;

/**
 * @author slorui
 * data 2021/5/19
 */
@AllArgsConstructor
public class SerializeException extends RuntimeException{

    private String message;

}
