package com.fc.serverless.config;

import com.fc.serverless.core.annotation.RemoteFunction;
import com.fc.serverless.proxy.RemoteFunctionProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

public class RemoteFunctionBeanPostProcessor implements BeanPostProcessor {

    private final RemoteFunctionProxyFactory proxyFactory;

    public RemoteFunctionBeanPostProcessor(RemoteFunctionProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(RemoteFunction.class)) {
                field.setAccessible(true);
                try {
                    Object proxy = proxyFactory.createProxy(field.getType(), field.getAnnotation(RemoteFunction.class));
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }
}
