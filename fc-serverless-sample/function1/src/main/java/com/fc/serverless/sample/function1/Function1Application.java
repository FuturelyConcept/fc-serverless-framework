package com.fc.serverless.sample.function1;

import com.fc.serverless.core.annotation.RemoteFunction;
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

    @Bean("function1")  // Added explicit name to match application.yml
    public Function<String, String> function1() {
        return t -> "Function1 processed: " + t.toLowerCase(Locale.ROOT);
    }

    @Bean("low")
    public Function<String, String> low(@RemoteFunction(name="function2") Function<String, String> f2 ){
        return t -> t.toLowerCase(Locale.ROOT) + f2.apply(t);
    }

    @Bean("sup")
    public Supplier<String> sup() {
        return () -> "Whats up";
    }
}