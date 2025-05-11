package com.arash.ariani.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "request.logging")
public class RequestLoggingProperties {
    /**
     * Whether to enable request logging globally
     */
    private boolean enabled = true;

    /**
     * Whether to enable ANSI color output in console logs
     */
    private boolean enableAnsiColor = true;

    /**
     * Whether to include request headers in logs
     */
    private boolean includeHeaders = true;

    /**
     * Whether to include request parameters in logs
     */
    private boolean includeParameters = true;

    /**
     * Whether to include request body in logs
     */
    private boolean includeRequestBody = true;

    /**
     * Whether to include response body in logs
     */
    private boolean includeResponseBody = true;

    /**
     * Whether to include timing information in logs
     */
    private boolean includeTiming = true;

    /**
     * Maximum length of logged bodies
     */
    private int maxBodyLength = 1000;

    /**
     * Headers to exclude from logging
     */
    private List<String> excludeHeaders = new ArrayList<>();

    /**
     * Masking patterns for sensitive data
     */
    private List<MaskingPattern> maskingPatterns = new ArrayList<>();

    /**
     * Masking fields
     */
    private String maskFields = "";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnableAnsiColor() {
        return enableAnsiColor;
    }

    public void setEnableAnsiColor(boolean enableAnsiColor) {
        this.enableAnsiColor = enableAnsiColor;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public boolean isIncludeParameters() {
        return includeParameters;
    }

    public void setIncludeParameters(boolean includeParameters) {
        this.includeParameters = includeParameters;
    }

    public boolean isIncludeRequestBody() {
        return includeRequestBody;
    }

    public void setIncludeRequestBody(boolean includeRequestBody) {
        this.includeRequestBody = includeRequestBody;
    }

    public boolean isIncludeResponseBody() {
        return includeResponseBody;
    }

    public void setIncludeResponseBody(boolean includeResponseBody) {
        this.includeResponseBody = includeResponseBody;
    }

    public boolean isIncludeTiming() {
        return includeTiming;
    }

    public void setIncludeTiming(boolean includeTiming) {
        this.includeTiming = includeTiming;
    }

    public int getMaxBodyLength() {
        return maxBodyLength;
    }

    public void setMaxBodyLength(int maxBodyLength) {
        this.maxBodyLength = maxBodyLength;
    }

    public List<String> getExcludeHeaders() {
        return excludeHeaders;
    }

    public void setExcludeHeaders(List<String> excludeHeaders) {
        this.excludeHeaders = excludeHeaders;
    }

    public List<MaskingPattern> getMaskingPatterns() {
        return maskingPatterns;
    }

    public void setMaskingPatterns(List<MaskingPattern> maskingPatterns) {
        this.maskingPatterns = maskingPatterns;
    }

    public String getMaskFields() {
        return maskFields;
    }

    public void setMaskFields(String maskFields) {
        this.maskFields = maskFields;
    }

    public static class MaskingPattern {
        /**
         * Field name to mask
         */
        private String fieldName;

        /**
         * Regular expression pattern to match
         */
        private String pattern;

        /**
         * Replacement string for matched pattern
         */
        private String replacement;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getReplacement() {
            return replacement;
        }

        public void setReplacement(String replacement) {
            this.replacement = replacement;
        }
    }
} 