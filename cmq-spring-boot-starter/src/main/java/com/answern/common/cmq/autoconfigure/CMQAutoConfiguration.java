package com.answern.common.cmq.autoconfigure;

import com.answern.common.cmq.config.CMQConfig;
import com.answern.common.cmq.spring.utils.CMQSpringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties(CMQProperties.class)
public class CMQAutoConfiguration {

    @Bean(CMQSpringUtils.CONFIG_BEAN_NAME)
    public CMQConfig cmqConfig(CMQProperties cmqProperties){
        CMQConfig cmqConfig = new CMQConfig();
        BeanUtils.copyProperties(cmqProperties,cmqConfig);
        return cmqConfig;
    }

    @Bean(CMQSpringUtils.EXECUTOR_BEAN_NAME)
    public ExecutorService cmqExecutor(){
        return Executors.newFixedThreadPool(CMQSpringUtils.THREADPOOL_DEFAULT_MAX_SIZE);
    }
}
