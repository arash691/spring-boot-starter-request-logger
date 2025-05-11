package com.arash.ariani.annotation;

import com.arash.ariani.config.RequestLoggingConfiguration;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RequestLoggingConfiguration.class)
public @interface EnableRequestLogging {
    /**
     * Whether to enable request logging globally for all endpoints.
     * If false, only endpoints annotated with @LogRequest will be logged.
     */
    boolean enableGlobalLogging() default false;

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
     * Maximum length of logged bodies. Bodies longer than this will be truncated.
     */
    int maxBodyLength() default 1000;
} 