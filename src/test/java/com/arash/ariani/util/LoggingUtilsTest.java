package com.arash.ariani.util;

import com.arash.ariani.util.LoggingUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingUtilsTest {

    @Test
    void shouldGenerateUniqueCorrelationIds() {
        String id1 = LoggingUtils.generateCorrelationId();
        String id2 = LoggingUtils.generateCorrelationId();

        assertThat(id1).isNotNull().isNotEmpty();
        assertThat(id2).isNotNull().isNotEmpty();
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    void shouldFormatJsonContent() {
        String unformattedJson = "{\"name\":\"test\",\"value\":123}";
        String formattedJson = LoggingUtils.formatContent(unformattedJson, MediaType.APPLICATION_JSON_VALUE);

        assertThat(formattedJson)
            .contains("{\n")
            .contains("  \"name\" : \"test\",\n")
            .contains("  \"value\" : 123\n")
            .contains("}");
    }

    @Test
    void shouldFormatXmlContent() {
        String unformattedXml = "<root><child>value</child></root>";
        String formattedXml = LoggingUtils.formatContent(unformattedXml, MediaType.APPLICATION_XML_VALUE);

        assertThat(formattedXml)
            .contains("<?xml")
            .contains("<root>")
            .contains("  <child>value</child>")
            .contains("</root>");
    }

    @Test
    void shouldHandleInvalidJson() {
        String invalidJson = "{invalid:json}";
        String result = LoggingUtils.formatContent(invalidJson, MediaType.APPLICATION_JSON_VALUE);

        assertThat(result).isEqualTo(invalidJson);
    }

    @Test
    void shouldHandleInvalidXml() {
        String invalidXml = "<invalid>xml";
        String result = LoggingUtils.formatContent(invalidXml, MediaType.APPLICATION_XML_VALUE);

        assertThat(result).isEqualTo(invalidXml);
    }

    @Test
    void shouldHandleNullContent() {
        String result = LoggingUtils.formatContent(null, MediaType.APPLICATION_JSON_VALUE);
        assertThat(result).isNull();
    }

    @Test
    void shouldHandleEmptyContent() {
        String result = LoggingUtils.formatContent("", MediaType.APPLICATION_JSON_VALUE);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleUnsupportedContentType() {
        String content = "test content";
        String result = LoggingUtils.formatContent(content, MediaType.TEXT_PLAIN_VALUE);
        assertThat(result).isEqualTo(content);
    }
} 