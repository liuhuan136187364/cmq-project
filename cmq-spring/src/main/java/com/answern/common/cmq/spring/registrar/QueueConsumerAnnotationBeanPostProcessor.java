package com.answern.common.cmq.spring.registrar;

import com.answern.common.cmq.spring.annotation.Handler;
import com.answern.common.cmq.spring.annotation.QueueConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.*;

import java.lang.reflect.Method;
import java.util.*;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;
import static org.springframework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ClassUtils.resolveClassName;

public class QueueConsumerAnnotationBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware,
        ResourceLoaderAware, BeanClassLoaderAware {

    private static final String SEPARATOR = ":";
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Set<String> packagesToScan;

    private Environment environment;

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    public QueueConsumerAnnotationBeanPostProcessor(String... packagesToScan) {
        this(Arrays.asList(packagesToScan));
    }

    public QueueConsumerAnnotationBeanPostProcessor(Collection<String> packagesToScan) {
        this(new LinkedHashSet<String>(packagesToScan));
    }

    public QueueConsumerAnnotationBeanPostProcessor(Set<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<String> resolvedPackagesToScan = resolvePackagesToScan(packagesToScan);
        if (!CollectionUtils.isEmpty(resolvedPackagesToScan)) {
            registerQueueConsumerBeans(resolvedPackagesToScan, registry);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("packagesToScan is empty , QueueConsumerBean registry will be ignored!");
            }
        }

    }

    private void registerQueueConsumerBeans(Set<String> packagesToScan, BeanDefinitionRegistry registry) {
        CMQClassPathBeanDefinitionScanner scanner =
                new CMQClassPathBeanDefinitionScanner(registry, environment, resourceLoader);

        BeanNameGenerator beanNameGenerator = resolveBeanNameGenerator(registry);

        scanner.setBeanNameGenerator(beanNameGenerator);

        scanner.addIncludeFilter(new AnnotationTypeFilter(QueueConsumer.class));

        for (String packageToScan : packagesToScan) {

            // Registers @QueueConsumer Bean first
            scanner.scan(packageToScan);

            // Finds all BeanDefinitionHolders of @QueueConsumer whether @ComponentScan scans or not.
            Set<BeanDefinitionHolder> beanDefinitionHolders =
                    findQueueConsumerBeanDefinitionHolders(scanner, packageToScan, registry, beanNameGenerator);

            if (!CollectionUtils.isEmpty(beanDefinitionHolders)) {

                for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
                    registerQueueConsumerBean(beanDefinitionHolder, registry, scanner);
                }

                if (logger.isInfoEnabled()) {
                    logger.info(beanDefinitionHolders.size() + " annotated CMQ's @QUeueConsumer Components { " +
                            beanDefinitionHolders +
                            " } were scanned under package[" + packageToScan + "]");
                }

            } else {

                if (logger.isWarnEnabled()) {
                    logger.warn("No Spring Bean annotating CMQ's @QueueConsumer was found under package["
                            + packageToScan + "]");
                }

            }

        }
    }

    private void registerQueueConsumerBean(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry registry,
                                     CMQClassPathBeanDefinitionScanner scanner) {

        Class<?> beanClass = resolveClass(beanDefinitionHolder);

        QueueConsumer queueConsumer = findAnnotation(beanClass, QueueConsumer.class);

        String annotatedQueueConsumerBeanName = beanDefinitionHolder.getBeanName();

        AbstractBeanDefinition queueConsumerBeanDefinition =
                buildQueueConsumerBeanDefinition(queueConsumer, beanClass);
        queueConsumerBeanDefinition.setInitMethodName("afterSet");
        // QueueConsumerBean Bean name
        String beanName = generateQueueConsumerBeanName(queueConsumer, beanClass, annotatedQueueConsumerBeanName);

        if (scanner.checkCandidate(beanName, queueConsumerBeanDefinition)) { // check duplicated candidate bean
            registry.registerBeanDefinition(beanName, queueConsumerBeanDefinition);

            if (logger.isInfoEnabled()) {
                logger.warn("The BeanDefinition[" + queueConsumerBeanDefinition +
                        "] of QueueConsumerBean has been registered with name : " + beanName);
            }

        } else {

            if (logger.isWarnEnabled()) {
                logger.warn("The Duplicated BeanDefinition[" + queueConsumerBeanDefinition +
                        "] of QueueConsumerBean[ bean name : " + beanName +
                        "] was be found , Did @DubboComponentScan scan to same package in many times?");
            }

        }

    }

    private AbstractBeanDefinition buildQueueConsumerBeanDefinition(QueueConsumer queueConsumer, Class<?> beanClass) {

        BeanDefinitionBuilder builder = rootBeanDefinition(QueueConsumerBean.class);
        builder.addPropertyReference("executor",queueConsumer.executor());
        builder.addPropertyValue("method",getHandlerMethod(beanClass));
        builder.addPropertyValue("queueConsumer",queueConsumer);
        builder.addPropertyReference("config",queueConsumer.config());
        return builder.getBeanDefinition();

    }


    private String generateQueueConsumerBeanName(QueueConsumer queueConsumer, Class<?> beanClass, String annotatedQueueConsumerBeanName) {

        StringBuilder beanNameBuilder = new StringBuilder(QueueConsumerBean.class.getSimpleName());

        beanNameBuilder.append(SEPARATOR).append(annotatedQueueConsumerBeanName);

        String interfaceClassName = beanClass.getName();

        beanNameBuilder.append(SEPARATOR).append(interfaceClassName);
        String queueName = queueConsumer.value();
        if(!StringUtils.isEmpty(queueName)){
            queueName = queueConsumer.queueName();
        }
        Assert.notNull(queueConsumer,annotatedQueueConsumerBeanName+"[queueName] or [value] must be not null");
        if (StringUtils.hasText(queueName)) {
            beanNameBuilder.append(SEPARATOR).append(queueName);
        }
        return beanNameBuilder.toString();

    }
    
    private Set<BeanDefinitionHolder> findQueueConsumerBeanDefinitionHolders(
            ClassPathBeanDefinitionScanner scanner, String packageToScan, BeanDefinitionRegistry registry,
            BeanNameGenerator beanNameGenerator) {

        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(packageToScan);

        Set<BeanDefinitionHolder> beanDefinitionHolders = new LinkedHashSet<BeanDefinitionHolder>(beanDefinitions.size());

        for (BeanDefinition beanDefinition : beanDefinitions) {

            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
            BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
            beanDefinitionHolders.add(beanDefinitionHolder);

        }

        return beanDefinitionHolders;

    }
    

    private BeanNameGenerator resolveBeanNameGenerator(BeanDefinitionRegistry registry) {

        BeanNameGenerator beanNameGenerator = null;

        if (registry instanceof SingletonBeanRegistry) {
            SingletonBeanRegistry singletonBeanRegistry = SingletonBeanRegistry.class.cast(registry);
            beanNameGenerator = (BeanNameGenerator) singletonBeanRegistry.getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
        }

        if (beanNameGenerator == null) {

            if (logger.isInfoEnabled()) {

                logger.info("BeanNameGenerator bean can't be found in BeanFactory with name ["
                        + CONFIGURATION_BEAN_NAME_GENERATOR + "]");
                logger.info("BeanNameGenerator will be a instance of " +
                        AnnotationBeanNameGenerator.class.getName() +
                        " , it maybe a potential problem on bean name generation.");
            }

            beanNameGenerator = new AnnotationBeanNameGenerator();

        }

        return beanNameGenerator;

    }

    private Class<?> resolveClass(BeanDefinitionHolder beanDefinitionHolder) {

        BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();

        return resolveClass(beanDefinition);

    }

    private Class<?> resolveClass(BeanDefinition beanDefinition) {

        String beanClassName = beanDefinition.getBeanClassName();

        return resolveClassName(beanClassName, classLoader);

    }

    private Set<String> resolvePackagesToScan(Set<String> packagesToScan) {
        Set<String> resolvedPackagesToScan = new LinkedHashSet<String>(packagesToScan.size());
        for (String packageToScan : packagesToScan) {
            if (StringUtils.hasText(packageToScan)) {
                String resolvedPackageToScan = environment.resolvePlaceholders(packageToScan.trim());
                resolvedPackagesToScan.add(resolvedPackageToScan);
            }
        }
        return resolvedPackagesToScan;
    }

    private Method getHandlerMethod(Class<?> targetClass){
        Method handlerMethod = Arrays.stream(ReflectionUtils.getAllDeclaredMethods(targetClass))
                .filter(method -> AnnotationUtils.findAnnotation(method, Handler.class)!=null)
                .findFirst().orElse(null);;
        Assert.notNull(handlerMethod," @Handler method not found in " +targetClass.getName());
        return handlerMethod;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
