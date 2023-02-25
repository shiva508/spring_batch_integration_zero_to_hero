package com.pool.receiver.annotation;

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
public @interface ReceiverInboundChunkChannel {
    @AliasFor(annotation = Qualifier.class)
    String value() default "";
}
