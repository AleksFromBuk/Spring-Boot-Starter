package com.t1.task3.httpstarter;

import com.t1.task3.apects.ControllerLogAspect;
import com.t1.task3.config.AopInterceptorAutoConfiguration;
import com.t1.task3.exception.LoggingProcessException;
import com.t1.task3.init.LoggingEnvironmentPostProcessor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(OutputCaptureExtension.class)
public class LoggingTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AopInterceptorAutoConfiguration.class));

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @InjectMocks
    private ControllerLogAspect aspect;

    @BeforeEach
    public void setUp() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        when(response.getStatus()).thenReturn(200);
        when(response.getHeaderNames()).thenReturn(Collections.emptyList());

        ServletRequestAttributes attributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @Test
    public void controllerLogAspect_shouldLogRequestAndResponse(CapturedOutput output) throws Throwable {
        when(joinPoint.proceed()).thenReturn(null);
        when(joinPoint.getSignature()).thenReturn(mock(MethodSignature.class));
        aspect.logHttpRequests(joinPoint);
        assertThat(output).contains("-- HTTP Log:");
        assertThat(output).contains("Method: GET");
        assertThat(output).contains("Request URL: http://localhost/test");
        assertThat(output).contains("Response Status: 200");
    }

    @Test
    void loggingInterceptorIsRegisteredWhenEnabledIsTrue() {
        contextRunner
                .withPropertyValues("http.aop.interceptor.enabled=true")
                .run(context -> assertThat(context).hasSingleBean(ControllerLogAspect.class));
    }

    @Test
    void loggingInterceptorIsNotRegisteredWhenEnabledIsFalse() {
        contextRunner
                .withPropertyValues("http.aop.interceptor.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(ControllerLogAspect.class));
    }

    @Test
    void whenInvalidProperty_thenThrowLoggingProcessException() {
        ConfigurableEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("test", Collections.singletonMap("http.aop.interceptor.enable", "invalid_value")));

        LoggingEnvironmentPostProcessor processor = new LoggingEnvironmentPostProcessor();

        assertThatExceptionOfType(LoggingProcessException.class)
                .isThrownBy(() -> processor.postProcessEnvironment(environment, new SpringApplication()))
                .withMessageContaining("Ошибка при проверке свойства 'http.aop.interceptor.enable':");
    }
}
