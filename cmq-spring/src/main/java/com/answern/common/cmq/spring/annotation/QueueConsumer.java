package com.answern.common.cmq.spring.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface QueueConsumer {
    String value() default "";

    String queueName() default "";

    int pollingWaitSeconds() default 10;

    int pollingIntervalSeconds() default 1;

    String executor() default "CMQExecutor";

    String config() default "CMQConfig";

}
