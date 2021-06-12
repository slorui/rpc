package com.slorui.service.impl;

import com.rpc.annotation.Service;
import com.rpc.pojo.HelloObject;
import com.rpc.pojo.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author slorui
 * data 2021/5/19
 */
@Slf4j
@Service
public class HelloServiceImpl1 implements HelloService {

    @Override
    public String hello(HelloObject obj) {
        log.info("接收到：{}",obj.getMessage());
        return "这是调用的返回值11111,id="+obj.getId();
    }
}
