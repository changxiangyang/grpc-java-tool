package com.opay.algo.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author cxy
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface GrpcServiceProxy {
    @AliasFor(
            annotation = Component.class
    )
    String value() default "";
}
