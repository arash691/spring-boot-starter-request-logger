package com.arash.ariani.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = TestPropertiesConfig.class)
@TestPropertySource(properties = {
    "request.logging.enabled=false",
    "request.logging.include-headers=false",
    "request.logging.include-parameters=false",
    "request.logging.include-request-body=false",
    "request.logging.include-response-body=false",
    "request.logging.include-timing=false",
    "request.logging.max-body-length=500",
    "request.logging.exclude-headers[0]=Authorization",
    "request.logging.exclude-headers[1]=Cookie",
    "request.logging.masking-patterns[0].field-name=password",
    "request.logging.masking-patterns[0].pattern=password\":\"[^\"]*\"",
    "request.logging.masking-patterns[0].replacement=password\":\"***\""
})
class RequestLoggingPropertiesTest {

    @Autowired
    private RequestLoggingProperties properties;

    @Test
    void whenPropertiesSet_thenLoadedCorrectly() {
        assertThat(properties.isEnabled()).isFalse();
        assertThat(properties.isIncludeHeaders()).isFalse();
        assertThat(properties.isIncludeParameters()).isFalse();
        assertThat(properties.isIncludeRequestBody()).isFalse();
        assertThat(properties.isIncludeResponseBody()).isFalse();
        assertThat(properties.isIncludeTiming()).isFalse();
        assertThat(properties.getMaxBodyLength()).isEqualTo(500);
        
        assertThat(properties.getExcludeHeaders())
            .containsExactlyInAnyOrder("Authorization", "Cookie");

        assertThat(properties.getMaskingPatterns())
            .hasSize(1)
            .first()
            .satisfies(pattern -> {
                assertThat(pattern.getFieldName()).isEqualTo("password");
                assertThat(pattern.getPattern()).isEqualTo("password\":\"[^\"]*\"");
                assertThat(pattern.getReplacement()).isEqualTo("password\":\"***\"");
            });
    }
} 