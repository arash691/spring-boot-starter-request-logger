package com.arash.ariani.config;

import com.arash.ariani.annotation.EnableRequestLogging;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@EnableRequestLogging(enableGlobalLogging = true)
@ComponentScan("com.arash.ariani")
public class TestConfig {

    @Bean
    public Class<?> sourceClass() {
        return TestConfig.class;
    }

    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
} 