package com.fc.serverless.config;

import com.fc.serverless.config.RemoteFunctionBeanPostProcessor;
import com.fc.serverless.core.annotation.RemoteFunction;
import com.fc.serverless.proxy.RemoteFunctionProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RemoteFunctionAutoConfiguration {

    @Bean
    public BeanPostProcessor remoteFunctionPostProcessor(RemoteFunctionProxyFactory proxyFactory, Environment environment) {
        return new RemoteFunctionBeanPostProcessor(proxyFactory, environment);
    }

    @Bean
    public RemoteFunctionProxyFactory proxyFactory() {
        return new RemoteFunctionProxyFactory();
    }
}
