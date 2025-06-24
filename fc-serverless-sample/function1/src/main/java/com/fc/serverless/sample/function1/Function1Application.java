package com.fc.serverless.sample.function1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

@SpringBootApplication
public class Function1Application {
    public static void main(String[] args) {
        SpringApplication.run(Function1Application.class, args);
    }

    @Bean("low")
    public Function<String, String> low() {
        return t -> t.toLowerCase(Locale.ROOT);
    }

    @Bean("sup")
    public Supplier<String> sup() {
        return () -> "Whats up";
    }
}
