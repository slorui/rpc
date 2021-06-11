package com.rpc.spring;

import com.rpc.annotation.ReferenceScan;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author slorui
 * data 2021/6/11
 */
public class ReferenceBeanRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes mapperScanAttrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(ReferenceScan.class.getName()));
        String basePackage = mapperScanAttrs.getString("basePackage");
        String registryIp = mapperScanAttrs.getString("registryIp");
        Class registryClass = mapperScanAttrs.getClass("registryClass");
        Class invokerClass = mapperScanAttrs.getClass("invokerClass");
        Class loadBalanceClass = mapperScanAttrs.getClass("loadBalanceClass");
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ReferenceScannerConfig.class);
        beanDefinitionBuilder.addPropertyValue("basePackage",basePackage);
        beanDefinitionBuilder.addPropertyValue("registryIp", registryIp);
        beanDefinitionBuilder.addPropertyValue("registryClass", registryClass);
        beanDefinitionBuilder.addPropertyValue("invokerClass", invokerClass);
        beanDefinitionBuilder.addPropertyValue("loadBalanceClass", loadBalanceClass);
        registry.registerBeanDefinition("referenceScannerConfig",beanDefinitionBuilder.getBeanDefinition());
    }
}
