package com.rpc.spring;

import com.rpc.annotation.Reference;
import com.rpc.client.RpcClientProxy;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

import java.util.Set;

/**
 * @author slorui
 * data 2021/6/11
 */
public class ReferenceScanner extends ClassPathBeanDefinitionScanner {


    public ReferenceScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    private RpcClientProxy proxy;

    public void setProxy(RpcClientProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    protected void registerDefaultFilters() {
        super.registerDefaultFilters();
        addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition){
        return beanDefinition.getMetadata().hasAnnotation(Reference.class.getName());
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        postProcessBeanDefinition(beanDefinitions);
        return beanDefinitions;
    }

    private void postProcessBeanDefinition(Set<BeanDefinitionHolder> beanDefinitions){
        ScannedGenericBeanDefinition beanDefinition = null;
        for(BeanDefinitionHolder holder: beanDefinitions){
            if(holder.getBeanDefinition() instanceof AnnotatedBeanDefinition){
                beanDefinition = (ScannedGenericBeanDefinition) holder.getBeanDefinition();
                if (beanDefinition.getMetadata().hasAnnotation(Reference.class.getName())) {
                    String beanClassName = beanDefinition.getBeanClassName();
                    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
                    beanDefinition.setBeanClass(ReferenceFactoryBean.class);
                    beanDefinition.getPropertyValues().add("proxy",this.proxy);
                }
            }
        }
    }
}
