package com.fc.serverless.config;

import com.fc.serverless.core.annotation.RemoteFunction;
import com.fc.serverless.proxy.RemoteFunctionProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;

public class RemoteFunctionBeanPostProcessor implements BeanPostProcessor {

    private final RemoteFunctionProxyFactory proxyFactory;
    private final Environment environment;

    public RemoteFunctionBeanPostProcessor(RemoteFunctionProxyFactory proxyFactory, Environment environment) {
        this.proxyFactory = proxyFactory;
        this.environment = environment;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();

        // Handle CGLIB proxies
        if (clazz.getName().contains("$$")) {
            clazz = clazz.getSuperclass();
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(RemoteFunction.class)) {
                field.setAccessible(true);
                try {
                    RemoteFunction annotation = field.getAnnotation(RemoteFunction.class);
                    Object proxy = proxyFactory.createProxy(field.getType(), annotation, environment);
                    field.set(bean, proxy);
                    System.out.println("🔗 Injected remote function proxy for: " + annotation.name() + " into " + beanName);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject remote function proxy for field: " + field.getName(), e);
                }
            }
        }
        return bean;
    }
}