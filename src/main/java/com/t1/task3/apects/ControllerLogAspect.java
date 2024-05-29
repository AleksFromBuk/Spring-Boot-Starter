package com.t1.task3.apects;

import com.t1.task3.model.HttpLog;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class ControllerLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ControllerLogAspect.class);

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object logHttpRequests(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        HttpServletResponse response = attributes != null ? attributes.getResponse() : null;

        HttpLog httpLog = new HttpLog();
        if (request != null) {
            httpLog.setMethod(request.getMethod());
            httpLog.setUriEndpoint(request.getRequestURL().toString());
            httpLog.setRequestHeaders(getRequestHeaders(request));
        }

        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long elapsedTime = System.currentTimeMillis() - start;
        httpLog.setExecutionTime(elapsedTime);

        if (response != null) {
            httpLog.setStatusCode(response.getStatus());
            httpLog.setResponseHeaders(getResponseHeaders(response));
        }

        logger.info("HTTP Log:\nMethod: {}\nRequest URL: {}\nResponse Status: {}\nExecution Time: {} ms",
                httpLog.getMethod(), httpLog.getUriEndpoint(), httpLog.getStatusCode(), httpLog.getExecutionTime());

        return result;
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }

    private Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        for (String headerName : response.getHeaderNames()) {
            headers.put(headerName, response.getHeader(headerName));
        }
        return headers;
    }
}

