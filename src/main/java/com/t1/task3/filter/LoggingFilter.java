package com.t1.task3.filter;

import com.t1.task3.model.HttpLog;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        if (requestURI.equals("/favicon.ico")) {
            chain.doFilter(request, response);
            return;
        }

        HttpLog httpLog = new HttpLog();
        httpLog.setMethod(httpRequest.getMethod());
        httpLog.setUriEndpoint(httpRequest.getRequestURL().toString());
        httpLog.setRequestHeaders(getRequestHeaders(httpRequest));

        long start = System.currentTimeMillis();

        chain.doFilter(request, response);

        long elapsedTime = System.currentTimeMillis() - start;
        httpLog.setExecutionTime(elapsedTime);

        httpLog.setStatusCode(httpResponse.getStatus());
        httpLog.setResponseHeaders(getResponseHeaders(httpResponse));

        logger.info("HTTP Log:\nMethod: {}\nRequest URL: {}\nRequest Headers: {}\nResponse Status: {}\nResponse Headers: {}\nExecution Time: {} ms",
                httpLog.getMethod(),
                httpLog.getUriEndpoint(),
                httpLog.getRequestHeaders(),
                httpLog.getStatusCode(),
                httpLog.getResponseHeaders(),
                httpLog.getExecutionTime());
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
