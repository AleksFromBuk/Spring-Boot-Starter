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
        String checkProperty = environment.getProperty("http.aop.interceptor.enable");
        if (checkProperty != null
                && !checkProperty.equalsIgnoreCase("true")
                && !checkProperty.equalsIgnoreCase("false")) {

            throw new LoggingProcessException("Ошибка при проверке свойства 'http.aop.interceptor.enable':"
           + System.lineSeparator() + "свойство принимает значения true/false");
        }
    }
}