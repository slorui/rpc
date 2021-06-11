package com.slorui;

import com.rpc.annotation.ReferenceScan;
import com.rpc.pojo.HelloObject;
import com.rpc.pojo.HelloService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author slorui
 * data 2021/6/11
 */
@Configuration
@ReferenceScan(registryIp = "127.0.0.1:2181",basePackage = "com.rpc.pojo")
public class TestSpringClient {

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext ac
                = new AnnotationConfigApplicationContext();
        ac.register(TestSpringClient.class);
        ac.refresh();
        HelloService helloService  = (HelloService) ac.getBean("helloService");
        helloService.hello(new HelloObject(1,"this is a message"));
    }
}
