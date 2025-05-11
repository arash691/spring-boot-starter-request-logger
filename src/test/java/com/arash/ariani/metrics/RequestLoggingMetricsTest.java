package com.arash.ariani.metrics;

import com.arash.ariani.metrics.RequestLoggingMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLoggingMetricsTest {

    private MeterRegistry registry;
    private RequestLoggingMetrics metrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metrics = new RequestLoggingMetrics(registry);
    }

    @Test
    void shouldIncrementTotalRequests() {
        metrics.incrementTotalRequests();
        metrics.incrementTotalRequests();

        double count = registry.get("request.logger.total").counter().count();
        assertThat(count).isEqualTo(2.0);
    }

    @Test
    void shouldIncrementMaskedFields() {
        metrics.incrementMaskedFields();
        metrics.incrementMaskedFields();
        metrics.incrementMaskedFields();

        double count = registry.get("request.logger.masked.fields").counter().count();
        assertThat(count).isEqualTo(3.0);
    }

    @Test
    void shouldRecordProcessingTime() {
        var sample = metrics.startTimer();
        try {
            Thread.sleep(100); // Simulate processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        metrics.stopTimer(sample);

        double totalTime = registry.get("request.logger.processing.time").timer().totalTime(TimeUnit.MILLISECONDS);
        assertThat(totalTime).isGreaterThanOrEqualTo(100.0);
    }

    @Test
    void shouldIncrementErrors() {
        metrics.incrementErrors();

        double count = registry.get("request.logger.errors").counter().count();
        assertThat(count).isEqualTo(1.0);
    }

    @Test
    void shouldCreateAllMetrics() {
        assertThat(registry.get("request.logger.total").counter()).isNotNull();
        assertThat(registry.get("request.logger.masked.fields").counter()).isNotNull();
        assertThat(registry.get("request.logger.processing.time").timer()).isNotNull();
        assertThat(registry.get("request.logger.errors").counter()).isNotNull();
    }
} 