package com.arash.ariani.template;

import com.arash.ariani.template.LoggingTemplate;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingTemplateTest {

    @Test
    void shouldFormatTemplateWithValues() {
        LoggingTemplate template = new LoggingTemplate("Method: {{method}}, URI: {{uri}}");
        Map<String, Object> values = new HashMap<>();
        values.put("method", "GET");
        values.put("uri", "/api/test");

        String result = template.format(values);

        assertThat(result).isEqualTo("Method: GET, URI: /api/test");
    }

    @Test
    void shouldHandleNullTemplate() {
        LoggingTemplate template = new LoggingTemplate(null);
        Map<String, Object> values = new HashMap<>();
        values.put("method", "GET");

        String result = template.format(values);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleNullValues() {
        LoggingTemplate template = new LoggingTemplate("Value: {{value}}");
        Map<String, Object> values = new HashMap<>();
        values.put("value", null);

        String result = template.format(values);

        assertThat(result).isEqualTo("Value: ");
    }

    @Test
    void shouldBuildDefaultTemplates() {
        LoggingTemplate[] templates = new LoggingTemplate.Builder().build();

        assertThat(templates).hasSize(2);
        
        Map<String, Object> requestValues = new HashMap<>();
        requestValues.put("correlationId", "123");
        requestValues.put("method", "POST");
        requestValues.put("uri", "/api/test");
        requestValues.put("headers", "Content-Type: application/json");
        requestValues.put("parameters", "param1=value1");
        requestValues.put("body", "{\"test\":\"value\"}");

        String requestResult = templates[0].format(requestValues);
        assertThat(requestResult)
            .contains("Correlation ID: 123")
            .contains("Method: POST")
            .contains("URI: /api/test")
            .contains("Headers: Content-Type: application/json")
            .contains("Parameters: param1=value1")
            .contains("Body: {\"test\":\"value\"}");

        Map<String, Object> responseValues = new HashMap<>();
        responseValues.put("correlationId", "123");
        responseValues.put("status", "200 OK");
        responseValues.put("duration", "100");
        responseValues.put("headers", "Content-Type: application/json");
        responseValues.put("body", "{\"result\":\"success\"}");

        String responseResult = templates[1].format(responseValues);
        assertThat(responseResult)
            .contains("Correlation ID: 123")
            .contains("Status: 200 OK")
            .contains("Duration: 100ms")
            .contains("Headers: Content-Type: application/json")
            .contains("Body: {\"result\":\"success\"}");
    }

    @Test
    void shouldBuildCustomTemplates() {
        String customRequestTemplate = "REQ[{{method}}]{{uri}}";
        String customResponseTemplate = "RES[{{status}}]{{duration}}ms";

        LoggingTemplate[] templates = new LoggingTemplate.Builder()
            .requestTemplate(customRequestTemplate)
            .responseTemplate(customResponseTemplate)
            .build();

        Map<String, Object> requestValues = new HashMap<>();
        requestValues.put("method", "GET");
        requestValues.put("uri", "/test");

        Map<String, Object> responseValues = new HashMap<>();
        responseValues.put("status", "200");
        responseValues.put("duration", "50");

        assertThat(templates[0].format(requestValues)).isEqualTo("REQ[GET]/test");
        assertThat(templates[1].format(responseValues)).isEqualTo("RES[200]50ms");
    }
} 