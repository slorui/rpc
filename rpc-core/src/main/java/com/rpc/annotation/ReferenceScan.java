package com.rpc.annotation;

import com.rpc.loadbalancer.LoadBalancer;
import com.rpc.loadbalancer.RandomLoadBalancer;
import com.rpc.registry.ZookeeperServiceRegistry;
import com.rpc.spring.ReferenceBeanRegister;
import com.rpc.tolerant.FailFastInvoker;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author slorui
 * data 2021/6/11
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ReferenceBeanRegister.class)
public @interface ReferenceScan {

    @AliasFor("basePackage")
    String value() default "";

    @AliasFor("value")
    String basePackage() default "";

    String registryIp() default "";

    Class registryClass() default ZookeeperServiceRegistry.class;

    Class invokerClass() default FailFastInvoker.class;

    Class loadBalanceClass() default RandomLoadBalancer.class;

}
