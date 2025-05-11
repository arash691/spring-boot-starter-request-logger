package com.arash.ariani.actuator;

import com.arash.ariani.properties.RequestLoggingProperties;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Endpoint(id = "requestlogging")
public class RequestLoggingEndpoint {
    private final RequestLoggingProperties properties;

    public RequestLoggingEndpoint(RequestLoggingProperties properties) {
        this.properties = properties;
    }

    @ReadOperation
    public Map<String, Object> getConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("enabled", properties.isEnabled());
        config.put("includeHeaders", properties.isIncludeHeaders());
        config.put("includeParameters", properties.isIncludeParameters());
        config.put("includeRequestBody", properties.isIncludeRequestBody());
        config.put("includeResponseBody", properties.isIncludeResponseBody());
        config.put("includeTiming", properties.isIncludeTiming());
        config.put("maxBodyLength", properties.getMaxBodyLength());
        config.put("excludeHeaders", properties.getExcludeHeaders());
        config.put("maskingPatterns", properties.getMaskingPatterns());
        config.put("maskFields", properties.getMaskFields());
        config.put("enableAnsiColor", properties.isEnableAnsiColor());
        return config;
    }

    @WriteOperation
    public Map<String, Object> updateConfiguration(Boolean enabled, String maskFields) {
        if (enabled != null) {
            properties.setEnabled(enabled);
        }
        if (maskFields != null) {
            properties.setMaskFields(maskFields);
        }
        return getConfiguration();
    }
} 