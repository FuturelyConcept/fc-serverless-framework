package com.fc.serverless.config;

import com.fc.serverless.proxy.RemoteFunctionProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@Configuration
@Import(JacksonAutoConfiguration.class)
public class RemoteFunctionAutoConfiguration {

    @Bean
    public static RemoteFunctionProxyFactory proxyFactory() {
        return new RemoteFunctionProxyFactory();
    }

    @Bean
    public static BeanPostProcessor remoteFunctionPostProcessor(RemoteFunctionProxyFactory proxyFactory, Environment environment) {
        return new RemoteFunctionBeanPostProcessor(proxyFactory, environment);
    }
}