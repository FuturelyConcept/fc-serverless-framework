package com.fc.serverless.core.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RemoteFunction {
    String name() default "";
}
