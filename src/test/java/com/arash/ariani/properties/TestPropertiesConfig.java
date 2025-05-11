package com.arash.ariani.properties;

import com.arash.ariani.properties.RequestLoggingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RequestLoggingProperties.class)
public class TestPropertiesConfig {
} 