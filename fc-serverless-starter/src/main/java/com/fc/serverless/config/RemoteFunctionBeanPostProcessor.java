package com.fc.serverless.config;

import com.fc.serverless.core.annotation.RemoteFunction;
import com.fc.serverless.proxy.RemoteFunctionProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

                    // Extract the return type from the field's generic type
                    Class<?> returnType = extractReturnTypeFromField(field);

                    Object proxy = proxyFactory.createProxy(field.getType(), annotation, environment, returnType);
                    field.set(bean, proxy);
                    System.out.println("🔗 Injected remote function proxy for: " + annotation.name() +
                            " into " + beanName + " with return type: " + returnType.getName());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject remote function proxy for field: " + field.getName(), e);
                }
            }
        }
        return bean;
    }

    private Class<?> extractReturnTypeFromField(Field field) {
        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] typeArgs = paramType.getActualTypeArguments();

            // For Function<Input, Output>, we want the second type argument (Output)
            if (typeArgs.length == 2 && typeArgs[1] instanceof Class) {
                return (Class<?>) typeArgs[1];
            }
        }

        // Fallback to Object if we can't determine the type
        return Object.class;
    }
}