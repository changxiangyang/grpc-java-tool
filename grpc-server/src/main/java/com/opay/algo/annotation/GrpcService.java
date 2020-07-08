package com.opay.algo.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @author cxy
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Service
public @interface GrpcService {
    @AliasFor(
            annotation = Service.class
    )
    String value() default "";

    Class classValue() default Object.class;
}
