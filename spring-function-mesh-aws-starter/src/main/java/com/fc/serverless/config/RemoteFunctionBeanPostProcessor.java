package com.fc.serverless.config;

import com.fc.serverless.core.annotation.RemoteFunction;
import com.fc.serverless.proxy.RemoteFunctionProxyFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Simplified BeanPostProcessor for injecting remote function proxies
 */
    public class RemoteFunctionBeanPostProcessor implements BeanPostProcessor {

    private static final Log log = LogFactory.getLog(RemoteFunctionBeanPostProcessor.class);

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
                    Class<?> returnType = extractReturnType(field);

                    Object proxy = proxyFactory.createProxy(field.getType(), annotation, environment, returnType);
                    field.set(bean, proxy);

                    log.info("🔗 Injected remote function proxy: " + annotation.name() +
                            " into " + beanName + " [returnType: " + returnType.getSimpleName() + "]");

                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject remote function proxy: " + field.getName(), e);
                }
            }
        }
        return bean;
    }

    private Class<?> extractReturnType(Field field) {
        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] typeArgs = paramType.getActualTypeArguments();

            // For Function<Input, Output>, we want the second type argument (Output)
            // For Supplier<Output>, we want the first type argument (Output)
            // For Consumer<Input>, there's no return type (use Void)
            if (typeArgs.length >= 1) {
                Type returnTypeArg = typeArgs.length == 2 ? typeArgs[1] : typeArgs[0];
                if (returnTypeArg instanceof Class) {
                    return (Class<?>) returnTypeArg;
                }
            }
        }

        // Fallback to Object if we can't determine the type
        return Object.class;
    }
}