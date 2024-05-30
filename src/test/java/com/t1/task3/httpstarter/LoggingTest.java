package com.t1.task3.httpstarter;

import com.t1.task3.config.AopInterceptorAutoConfiguration;
import com.t1.task3.exception.LoggingProcessException;
import com.t1.task3.filter.LoggingFilter;
import com.t1.task3.init.LoggingEnvironmentPostProcessor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(OutputCaptureExtension.class)
public class LoggingTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AopInterceptorAutoConfiguration.class));


    @Test
    public void test_logs_http_request_method_correctly() throws IOException, ServletException {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Arrays.asList("header1", "header2"))); // Указываем конкретные заголовки
        when(response.getStatus()).thenReturn(200);
        LoggingFilter filter = new LoggingFilter();
        filter.doFilter(request, response, chain);
        verify(request).getMethod();
    }

    @Test
    public void test_handles_requests_with_no_headers_gracefully() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        when(response.getStatus()).thenReturn(200);
        LoggingFilter filter = new LoggingFilter();
        filter.doFilter(request, response, chain);
        verify(request).getMethod();
        verify(request).getHeaderNames();
    }

    @Test
    void loggingInterceptorIsRegisteredWhenEnabledIsTrue() {
        contextRunner
                .withPropertyValues("http.aop.interceptor.enabled=true")
                .run(context -> {
                    assertThat(context).hasBean("loggingFilter");
                    FilterRegistrationBean<?> filterRegistrationBean = context.getBean("loggingFilter", FilterRegistrationBean.class);
                    assertThat(filterRegistrationBean.getFilter()).isInstanceOf(LoggingFilter.class);
                });
    }

    @Test
    void loggingInterceptorIsNotRegisteredWhenEnabledIsFalse() {
        contextRunner
                .withPropertyValues("http.aop.interceptor.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean("loggingFilter"));
    }

    @Test
    void whenInvalidProperty_thenThrowLoggingProcessException() {
        ConfigurableEnvironment environment = new StandardEnvironment();
        environment.getPropertySources()
                .addFirst(new MapPropertySource("test", Collections.singletonMap("http.aop.interceptor.enabled", "invalid_value")));

        LoggingEnvironmentPostProcessor processor = new LoggingEnvironmentPostProcessor();

        assertThatExceptionOfType(LoggingProcessException.class)
                .isThrownBy(() -> processor.postProcessEnvironment(environment, new SpringApplication()))
                .withMessageContaining("Ошибка при проверке свойства 'http.aop.interceptor.enable':");
    }

}
