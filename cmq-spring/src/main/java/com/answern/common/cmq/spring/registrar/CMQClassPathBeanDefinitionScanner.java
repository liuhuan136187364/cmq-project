package com.answern.common.cmq.spring.registrar;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import java.util.Set;

import static org.springframework.context.annotation.AnnotationConfigUtils.registerAnnotationConfigProcessors;

public class CMQClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {
    public CMQClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment,
                                               ResourceLoader resourceLoader) {

        super(registry, useDefaultFilters);

        setEnvironment(environment);

        setResourceLoader(resourceLoader);

        registerAnnotationConfigProcessors(registry);

    }

    public CMQClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, Environment environment,
                                               ResourceLoader resourceLoader) {

        this(registry, false, environment, resourceLoader);

    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }

    @Override
    public boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
        return super.checkCandidate(beanName, beanDefinition);
    }
}
