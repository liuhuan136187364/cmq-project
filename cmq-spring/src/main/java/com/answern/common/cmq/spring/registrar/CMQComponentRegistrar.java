package com.answern.common.cmq.spring.registrar;

import com.answern.common.cmq.spring.annotation.EnableCMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.*;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

public class CMQComponentRegistrar implements ImportBeanDefinitionRegistrar{

    public static final Logger logger = LoggerFactory.getLogger(CMQComponentRegistrar.class);

    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Set<String> packagesToScan = getPackagesToScan(annotationMetadata);

        registerQueueConsumerAnnotationBeanPostProcessor(packagesToScan, registry);
        registerTemplateBeanPostProcessor(packagesToScan, registry);
    }

    private void registerTemplateBeanPostProcessor(Set<String> packagesToScan, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = rootBeanDefinition(TemplateBeanPostProcessor.class);
        builder.addConstructorArgValue(packagesToScan);
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
    }

    private void registerQueueConsumerAnnotationBeanPostProcessor(Set<String> packagesToScan, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = rootBeanDefinition(QueueConsumerAnnotationBeanPostProcessor.class);
        builder.addConstructorArgValue(packagesToScan);
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
    }

    private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(EnableCMQ.class.getName()));
        String[] basePackages = attributes.getStringArray("scanBasePackages");
        Class<?>[] basePackageClasses = attributes.getClassArray("scanBasePackageClasses");
        Set<String> packagesToScan = new LinkedHashSet<String>();
        packagesToScan.addAll(Arrays.asList(basePackages));
        for (Class<?> basePackageClass : basePackageClasses) {
            packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
        }
        if (packagesToScan.isEmpty()) {
            return Collections.singleton(ClassUtils.getPackageName(metadata.getClassName()));
        }
        return packagesToScan;
    }



}
