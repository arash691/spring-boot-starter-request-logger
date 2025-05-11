package com.arash.ariani.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogRequest {
    /**
     * Fields to mask in the request/response.
     * Each entry should be in the format "fieldName:maskingPattern"
     * Example: {"password:***", "creditCard:[0-9]{12}:****-****-****-$4"}
     */
    String[] maskPatterns() default {};

    /**
     * Whether to include request headers in the logs.
     */
    boolean includeHeaders() default true;

    /**
     * Whether to include request parameters in the logs.
     */
    boolean includeParameters() default true;

    /**
     * Whether to include request body in the logs.
     */
    boolean includeRequestBody() default true;

    /**
     * Whether to include response body in the logs.
     */
    boolean includeResponseBody() default true;

    /**
     * Whether to include timing information in the logs.
     */
    boolean includeTiming() default true;

    /**
     * List of headers to exclude from logging
     */
    String[] excludeHeaders() default {"Authorization", "Cookie"};

    /**
     * Maximum length of logged bodies. Bodies longer than this will be truncated.
     */
    int maxBodyLength() default 1000;

    /**
     * Custom message to be included in the log
     */
    String message() default "";
} 