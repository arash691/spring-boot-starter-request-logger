package com.arash.ariani.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class RequestLoggingMetrics {
    private final Counter totalRequestsCounter;
    private final Counter maskedFieldsCounter;
    private final Timer requestProcessingTimer;
    private final Counter errorCounter;

    public RequestLoggingMetrics(MeterRegistry registry) {
        this.totalRequestsCounter = Counter.builder("request.logger.total")
                .description("Total number of requests logged")
                .register(registry);

        this.maskedFieldsCounter = Counter.builder("request.logger.masked.fields")
                .description("Number of fields masked in requests/responses")
                .register(registry);

        this.requestProcessingTimer = Timer.builder("request.logger.processing.time")
                .description("Time spent processing requests")
                .register(registry);

        this.errorCounter = Counter.builder("request.logger.errors")
                .description("Number of errors during request logging")
                .register(registry);
    }

    public void incrementTotalRequests() {
        totalRequestsCounter.increment();
    }

    public void incrementMaskedFields() {
        maskedFieldsCounter.increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start();
    }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(requestProcessingTimer);
    }

    public void incrementErrors() {
        errorCounter.increment();
    }
} 