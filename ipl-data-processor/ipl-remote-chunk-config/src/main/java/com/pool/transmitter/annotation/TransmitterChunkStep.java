package com.pool.transmitter.annotation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.*;

@Target({ElementType.
        FIELD,ElementType.METHOD,
        ElementType.TYPE,
        ElementType.PARAMETER,
        ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier
public @interface TransmitterChunkStep {
    @AliasFor(annotation = Qualifier.class)
    String value() default "";
}
