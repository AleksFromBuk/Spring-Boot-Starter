package com.t1.task3.init;

import com.t1.task3.exception.LoggingProcessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
public class LoggingEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        log.info("Post processing environment");
        String checkProperty = environment.getProperty("http.aop.interceptor.enabled");
        boolean isBoolValue = Boolean.TRUE.toString().equals(checkProperty) || Boolean.FALSE.toString().equals(checkProperty);

        if (!isBoolValue) {
            throw new LoggingProcessException("Ошибка при проверке свойства 'http.aop.interceptor.enable'");
        }
    }
}