package com.arash.ariani.template;

import java.util.Map;
import java.util.regex.Pattern;

public class LoggingTemplate {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(.*?)}}");
    private final String template;

    public LoggingTemplate(String template) {
        this.template = template;
    }

    public String format(Map<String, Object> values) {
        if (template == null) {
            return "";
        }

        String result = template;
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }

    public static class Builder {
        private static final String DEFAULT_REQUEST_TEMPLATE = """
            {{timestamp}} {{level}} {{pid}} --- [{{thread}}] {{logger}} : Request Details:
            Method: {{method}}
            URI: {{uri}}
            Headers: {{headers}}
            Parameters: {{parameters}}
            Body: {{body}}
            """;

        private static final String DEFAULT_RESPONSE_TEMPLATE = """
            {{timestamp}} {{level}} {{pid}} --- [{{thread}}] {{logger}} : Response Details:
            Status: {{status}}
            Duration: {{duration}}ms
            Headers: {{headers}}
            Body: {{body}}
            """;

        private String requestTemplate = DEFAULT_REQUEST_TEMPLATE;
        private String responseTemplate = DEFAULT_RESPONSE_TEMPLATE;

        public Builder requestTemplate(String template) {
            this.requestTemplate = template;
            return this;
        }

        public Builder responseTemplate(String template) {
            this.responseTemplate = template;
            return this;
        }

        public LoggingTemplate[] build() {
            return new LoggingTemplate[] {
                new LoggingTemplate(requestTemplate),
                new LoggingTemplate(responseTemplate)
            };
        }
    }
} 