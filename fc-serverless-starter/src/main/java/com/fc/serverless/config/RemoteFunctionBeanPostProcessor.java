package com.fc.serverless.config;

import com.fc.serverless.core.annotation.RemoteFunction;
import com.fc.serverless.proxy.RemoteFunctionProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

                    // Determine the functional interface type and create appropriate proxy
                    FunctionalInterfaceInfo interfaceInfo = analyzeFunctionalInterface(field);

                    Object proxy = proxyFactory.createProxy(
                            field.getType(),
                            annotation,
                            environment,
                            interfaceInfo
                    );

                    field.set(bean, proxy);
                    System.out.println("🔗 Injected remote " + interfaceInfo.interfaceType +
                            " proxy for: " + annotation.name() +
                            " into " + beanName +
                            " with types: " + interfaceInfo.getTypeDescription());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject remote function proxy for field: " + field.getName(), e);
                }
            }
        }
        return bean;
    }

    /**
     * Analyzes the functional interface to determine its type and generic parameters
     */
    private FunctionalInterfaceInfo analyzeFunctionalInterface(Field field) {
        Class<?> fieldType = field.getType();
        Type genericType = field.getGenericType();

        if (Function.class.isAssignableFrom(fieldType)) {
            return analyzeFunctionInterface(genericType);
        } else if (Supplier.class.isAssignableFrom(fieldType)) {
            return analyzeSupplierInterface(genericType);
        } else if (Consumer.class.isAssignableFrom(fieldType)) {
            return analyzeConsumerInterface(genericType);
        }

        // Fallback for unknown functional interfaces
        return new FunctionalInterfaceInfo("UNKNOWN", null, null, null);
    }

    private FunctionalInterfaceInfo analyzeFunctionInterface(Type genericType) {
        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] typeArgs = paramType.getActualTypeArguments();

            if (typeArgs.length == 2) {
                Class<?> inputType = typeArgs[0] instanceof Class ? (Class<?>) typeArgs[0] : Object.class;
                Class<?> outputType = typeArgs[1] instanceof Class ? (Class<?>) typeArgs[1] : Object.class;
                return new FunctionalInterfaceInfo("FUNCTION", inputType, outputType, null);
            }
        }
        return new FunctionalInterfaceInfo("FUNCTION", Object.class, Object.class, null);
    }

    private FunctionalInterfaceInfo analyzeSupplierInterface(Type genericType) {
        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] typeArgs = paramType.getActualTypeArguments();

            if (typeArgs.length == 1) {
                Class<?> outputType = typeArgs[0] instanceof Class ? (Class<?>) typeArgs[0] : Object.class;
                return new FunctionalInterfaceInfo("SUPPLIER", null, outputType, null);
            }
        }
        return new FunctionalInterfaceInfo("SUPPLIER", null, Object.class, null);
    }

    private FunctionalInterfaceInfo analyzeConsumerInterface(Type genericType) {
        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] typeArgs = paramType.getActualTypeArguments();

            if (typeArgs.length == 1) {
                Class<?> inputType = typeArgs[0] instanceof Class ? (Class<?>) typeArgs[0] : Object.class;
                return new FunctionalInterfaceInfo("CONSUMER", inputType, null, Void.class);
            }
        }
        return new FunctionalInterfaceInfo("CONSUMER", Object.class, null, Void.class);
    }

    /**
     * Data class to hold functional interface analysis results
     */
    public static class FunctionalInterfaceInfo {
        public final String interfaceType;
        public final Class<?> inputType;
        public final Class<?> outputType;
        public final Class<?> returnType; // For determining HTTP response handling

        public FunctionalInterfaceInfo(String interfaceType, Class<?> inputType, Class<?> outputType, Class<?> returnType) {
            this.interfaceType = interfaceType;
            this.inputType = inputType;
            this.outputType = outputType;
            this.returnType = returnType != null ? returnType : outputType;
        }

        public String getTypeDescription() {
            switch (interfaceType) {
                case "FUNCTION":
                    return "Function<" + (inputType != null ? inputType.getSimpleName() : "?") +
                            ", " + (outputType != null ? outputType.getSimpleName() : "?") + ">";
                case "SUPPLIER":
                    return "Supplier<" + (outputType != null ? outputType.getSimpleName() : "?") + ">";
                case "CONSUMER":
                    return "Consumer<" + (inputType != null ? inputType.getSimpleName() : "?") + ">";
                default:
                    return "Unknown";
            }
        }

        public boolean hasInput() {
            return inputType != null && !"SUPPLIER".equals(interfaceType);
        }

        public boolean hasOutput() {
            return outputType != null && !"CONSUMER".equals(interfaceType);
        }

        public boolean isVoidReturn() {
            return returnType == Void.class || "CONSUMER".equals(interfaceType);
        }
    }
}