package com.fc.serverless.sample.function2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Locale;
import java.util.function.Function;

@SpringBootApplication
public class Function2Application {
    public static void main(String[] args) {
        SpringApplication.run(Function2Application.class, args);
    }

    @Bean
    public Function<String, String> low2() {
        return t -> t.toLowerCase(Locale.ROOT);
    }
}
