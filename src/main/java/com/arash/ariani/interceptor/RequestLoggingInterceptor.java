package com.arash.ariani.interceptor;

import com.arash.ariani.annotation.LogRequest;
import com.arash.ariani.properties.RequestLoggingProperties;
import com.arash.ariani.template.LoggingTemplate;
import com.arash.ariani.util.LoggingUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private final RequestLoggingProperties properties;
    private static final String START_TIME = "requestStartTime";
    private final LoggingTemplate[] templates;

    public RequestLoggingInterceptor(RequestLoggingProperties properties) {
        this.properties = properties;
        this.templates = new LoggingTemplate.Builder().build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        request.setAttribute(START_TIME, System.currentTimeMillis());
        String correlationId = LoggingUtils.generateCorrelationId();
        request.setAttribute("correlationId", correlationId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!(handler instanceof HandlerMethod)) {
            return;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LogRequest logRequest = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), LogRequest.class);

        if (logRequest == null && !properties.isEnabled()) {
            return;
        }

        logRequest(request, logRequest);
        logResponse(request, response, logRequest, ex);
    }

    private void logRequest(HttpServletRequest request, LogRequest logRequest) {
        Map<String, Object> values = new HashMap<>();
        values.put("correlationId", request.getAttribute("correlationId"));
        values.put("method", request.getMethod());
        values.put("uri", request.getRequestURI());

        boolean shouldIncludeHeaders = logRequest != null ? logRequest.includeHeaders() : properties.isIncludeHeaders();
        if (shouldIncludeHeaders) {
            String headers = getHeaders(request, logRequest);
            values.put("headers", headers.isEmpty() ? "-" : headers);
        }

        boolean shouldIncludeParams = logRequest != null ? logRequest.includeParameters() : properties.isIncludeParameters();
        if (shouldIncludeParams) {
            String parameters = getParameters(request);
            values.put("parameters", parameters.isEmpty() ? "-" : parameters);
        }

        boolean shouldIncludeBody = logRequest != null ? logRequest.includeRequestBody() : properties.isIncludeRequestBody();
        if (shouldIncludeBody && request instanceof ContentCachingRequestWrapper) {
            String body = getRequestBody((ContentCachingRequestWrapper) request);
            values.put("body", body != null && !body.isEmpty() ? maskSensitiveData(body, logRequest) : "-");
        } else {
            values.put("body", "-");
        }

        log.info(templates[0].format(values));
    }

    private void logResponse(HttpServletRequest request, HttpServletResponse response, LogRequest logRequest, Exception ex) {
        Map<String, Object> values = new HashMap<>();
        values.put("correlationId", request.getAttribute("correlationId"));
        values.put("status", response.getStatus() + (ex != null ? " (Error: " + ex.getMessage() + ")" : ""));

        boolean shouldIncludeTiming = logRequest != null ? logRequest.includeTiming() : properties.isIncludeTiming();
        if (shouldIncludeTiming) {
            Long startTime = (Long) request.getAttribute(START_TIME);
            if (startTime != null) {
                values.put("duration", String.valueOf(System.currentTimeMillis() - startTime));
            }
        }

        values.put("headers", "-"); // Default value for headers

        boolean shouldIncludeBody = logRequest != null ? logRequest.includeResponseBody() : properties.isIncludeResponseBody();
        if (shouldIncludeBody && response instanceof ContentCachingResponseWrapper) {
            String body = getResponseBody((ContentCachingResponseWrapper) response);
            values.put("body", !body.isEmpty() ? maskSensitiveData(body, logRequest) : "-");
        } else {
            values.put("body", "-");
        }

        log.info(templates[1].format(values));
    }

    private String getHeaders(HttpServletRequest request, LogRequest logRequest) {
        return Collections.list(request.getHeaderNames()).stream()
                .filter(header -> !isExcludedHeader(header, logRequest))
                .collect(Collectors.toMap(
                        header -> header,
                        request::getHeader,
                        (v1, v2) -> v1
                )).toString();
    }

    private boolean isExcludedHeader(String header, LogRequest logRequest) {
        if (logRequest != null && logRequest.excludeHeaders().length > 0) {
            return Arrays.asList(logRequest.excludeHeaders()).contains(header);
        }
        return properties.getExcludeHeaders().contains(header);
    }

    private String getParameters(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> maskSensitiveData(entry.getKey(), null),
                        entry -> Arrays.stream(entry.getValue())
                                .map(value -> maskSensitiveData(value, null))
                                .toArray(String[]::new)
                )).toString();
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) return "";
        
        int length = Math.min(content.length, properties.getMaxBodyLength());
        try {
            return new String(content, 0, length, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Failed to read request body", e);
            return "";
        }
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length == 0) return "";
        
        int length = Math.min(content.length, properties.getMaxBodyLength());
        try {
            String body = new String(content, 0, length, StandardCharsets.UTF_8);
            response.copyBodyToResponse();
            return body;
        } catch (Exception e) {
            log.warn("Failed to read response body", e);
            return "";
        }
    }

    private String maskSensitiveData(String content, LogRequest logRequest) {
        if (content == null) return null;
        
        String maskedContent = content;
        
        // Apply annotation-level masking patterns
        if (logRequest != null) {
            for (String maskPattern : logRequest.maskPatterns()) {
                String[] parts = maskPattern.split(":");
                if (parts.length >= 2) {
                    String pattern = "\"" + parts[0] + "\":\"[^\"]*\"";
                    String replacement = "\"" + parts[0] + "\":\"" + (parts.length > 2 ? parts[2] : "***") + "\"";
                    maskedContent = maskedContent.replaceAll(pattern, replacement);
                }
            }
        }
        
        // Apply global masking patterns
        for (RequestLoggingProperties.MaskingPattern pattern : properties.getMaskingPatterns()) {
            if (pattern.getPattern() != null && pattern.getReplacement() != null) {
                maskedContent = maskedContent.replaceAll(pattern.getPattern(), pattern.getReplacement());
            }
        }
        
        return maskedContent;
    }
} 