package com.t1.task3.init;

import com.t1.task3.exception.LoggingProcessException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class LoggingFailureAnalyzer extends AbstractFailureAnalyzer<LoggingProcessException> {


    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, LoggingProcessException cause) {
        return new FailureAnalysis(cause.getMessage(), "Укажите корректные значения для свойства 'http.aop.interceptor'"
                + " в файле конфигурации: true/false", rootFailure);
    }
}
