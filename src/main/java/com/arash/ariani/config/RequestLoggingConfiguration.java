package com.arash.ariani.config;

import com.arash.ariani.annotation.EnableRequestLogging;
import com.arash.ariani.interceptor.RequestLoggingInterceptor;
import com.arash.ariani.properties.RequestLoggingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(RequestLoggingProperties.class)
public class RequestLoggingConfiguration implements WebMvcConfigurer {

    private RequestLoggingProperties properties;
    private RequestLoggingInterceptor interceptor;
    private Class<?> sourceClass;

    public RequestLoggingConfiguration() {
        // Default constructor for Spring
    }

    @Autowired(required = false)
    public void setSourceClass(Class<?> sourceClass) {
        this.sourceClass = sourceClass;
        if (this.properties != null) {
            configureFromAnnotation();
        }
    }

    @Autowired
    public void setProperties(RequestLoggingProperties properties) {
        this.properties = properties;
        if (this.interceptor == null) {
            this.interceptor = new RequestLoggingInterceptor(properties);
        }
        if (this.sourceClass != null) {
            configureFromAnnotation();
        }
    }

    @Autowired
    public void setInterceptor(RequestLoggingInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    private void configureFromAnnotation() {
        EnableRequestLogging annotation = AnnotationUtils.findAnnotation(sourceClass, EnableRequestLogging.class);
        if (annotation != null && annotation.enableGlobalLogging()) {
            properties.setEnabled(true);
            properties.setIncludeHeaders(annotation.includeHeaders());
            properties.setIncludeParameters(annotation.includeParameters());
            properties.setIncludeRequestBody(annotation.includeRequestBody());
            properties.setIncludeResponseBody(annotation.includeResponseBody());
            properties.setIncludeTiming(annotation.includeTiming());
            properties.setMaxBodyLength(annotation.maxBodyLength());
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (interceptor != null) {
            registry.addInterceptor(interceptor);
        }
    }
} 