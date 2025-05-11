package com.arash.ariani.config;

import com.arash.ariani.filter.RequestLoggingFilter;
import com.arash.ariani.interceptor.RequestLoggingInterceptor;
import com.arash.ariani.properties.RequestLoggingProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(RequestLoggingProperties.class)
@ConditionalOnProperty(prefix = "request.logging", name = "enabled", matchIfMissing = true)
public class RequestLoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RequestLoggingProperties requestLoggingProperties() {
        return new RequestLoggingProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestLoggingInterceptor requestLoggingInterceptor(RequestLoggingProperties properties) {
        return new RequestLoggingInterceptor(properties);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnMissingBean
    public RequestLoggingFilter requestLoggingFilter() {
        return new RequestLoggingFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestLoggingConfiguration requestLoggingConfiguration() {
        return new RequestLoggingConfiguration();
    }
} 