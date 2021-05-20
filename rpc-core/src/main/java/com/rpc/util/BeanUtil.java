package com.rpc.util;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.rpc.registry.instance.RegistryInstance;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author slorui
 * data 2021/5/20
 */
public class BeanUtil {

    public static <T,V> T copyBean(T target,V source) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor pd = propertyDescriptors[i];
            if("class".equals(pd.getName())){
                continue;
            }
            try {
                PropertyDescriptor sourcePd = new PropertyDescriptor(pd.getName(), source.getClass());
                Method sourceMethod = sourcePd.getReadMethod();
                Object result = sourceMethod.invoke(source);
                Method pdMethod = pd.getWriteMethod();
                pdMethod.invoke(target, result);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return target;
    }
}
