package com.answern.common.cmq.spring.registrar;

import com.answern.common.cmq.queue.CMQQueueProducer;
import com.answern.common.cmq.spring.template.CMQQueueTemplate;
import com.answern.common.cmq.spring.template.CMQTopicTemplate;
import com.answern.common.cmq.spring.utils.CMQSpringUtils;
import com.answern.common.cmq.topic.CMQTopicPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

public class TemplateBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware,
        ResourceLoaderAware, BeanClassLoaderAware {

    public TemplateBeanPostProcessor(String... packagesToScan) {
        this(Arrays.asList(packagesToScan));
    }

    public TemplateBeanPostProcessor(Collection<String> packagesToScan) {
        this(new LinkedHashSet<String>(packagesToScan));
    }

    public TemplateBeanPostProcessor(Set<String> packagesToScan) {
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private void registerTemplate(BeanDefinitionRegistry registry) {
        registerQueueTemplate(registry);
        registerTopicTemplate(registry);
    }

    private void registerQueueTemplate(BeanDefinitionRegistry registry) {
        registerQueueProducer(registry);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CMQQueueTemplate.class);
        builder.addPropertyReference("producer", CMQSpringUtils.QUEUE_PRODUCER_BEAN_NAME);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition(CMQSpringUtils.QUEUE_TEMPLATE_BEAN_NAME,beanDefinition);
    }

    private void registerTopicTemplate(BeanDefinitionRegistry registry) {
        registerTopicPublisher(registry);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CMQTopicTemplate.class);
        builder.addPropertyReference("publisher", CMQSpringUtils.TOPIC_PUBLISHER_BEAN_NAME);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition(CMQSpringUtils.TOPIC_TEMPLATE_BEAN_NAME,beanDefinition);
    }


    public void registerQueueProducer(BeanDefinitionRegistry registry){
        BeanDefinitionBuilder builder = rootBeanDefinition(CMQQueueProducer.class);
        builder.setInitMethodName("afterSet");
        builder.addPropertyReference("config",CMQSpringUtils.CONFIG_BEAN_NAME);
        registry.registerBeanDefinition(CMQSpringUtils.QUEUE_PRODUCER_BEAN_NAME,builder.getBeanDefinition());
    }

    public void registerTopicPublisher(BeanDefinitionRegistry registry){
        BeanDefinitionBuilder builder = rootBeanDefinition(CMQTopicPublisher.class);
        builder.setInitMethodName("afterSet");
        builder.addPropertyReference("config",CMQSpringUtils.CONFIG_BEAN_NAME);
        registry.registerBeanDefinition(CMQSpringUtils.TOPIC_PUBLISHER_BEAN_NAME,builder.getBeanDefinition());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {}

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader){}

    @Override
    public void setBeanClassLoader(ClassLoader classLoader){}

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        registerTemplate(registry);
    }
}
