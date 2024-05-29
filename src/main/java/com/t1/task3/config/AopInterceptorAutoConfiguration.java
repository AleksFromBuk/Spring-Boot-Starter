package com.t1.task3.config;

import com.t1.task3.apects.ControllerLogAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(LoggingProperties.class)
@ConditionalOnProperty(prefix = "http.aop.interceptor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AopInterceptorAutoConfiguration {

    @Bean
    @ConditionalOnExpression("${http.aop.interceptor:false}")
    public ControllerLogAspect controllerLogAspect() {
        return new ControllerLogAspect();
    }

    @Bean
    @ConditionalOnProperty(prefix = "http.aop.interceptor", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ControllerLogAspect controllerLogAspect2() {
        return new ControllerLogAspect();
    }
}
