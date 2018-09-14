package com.answern.common.cmq.spring.annotation;

import com.answern.common.cmq.spring.registrar.CMQComponentRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(CMQComponentRegistrar.class)
@Documented
public @interface EnableCMQ {

    String[] scanBasePackages() default {};

    Class<?>[] scanBasePackageClasses() default {};

}
