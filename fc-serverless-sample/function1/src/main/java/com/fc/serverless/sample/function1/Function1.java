package com.fc.serverless.sample.function1;

import com.fc.serverless.core.annotation.RemoteFunction;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component("function1")
public class Function1 implements  Function<String, String> {

    @RemoteFunction(name="function2")
    private Function<String, String> function2;

    @Override
    public String apply(String s) {
        return "func1 called" + function2.apply(s);
    }
}
