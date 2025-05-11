package com.arash.ariani.util;

import com.arash.ariani.util.LoggingUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingUtilsTest {

    @Test
    void shouldGenerateCorrelationId() {
        String correlationId = LoggingUtils.generateCorrelationId();
        assertThat(correlationId).isNotNull().isNotEmpty();
    }

    @Test
    void shouldFormatTimestamp() {
        Instant now = Instant.now();
        String formatted = LoggingUtils.formatTimestamp(now);
        
        LocalDateTime localDateTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        String expected = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .format(localDateTime);
        
        assertThat(formatted).isEqualTo(expected);
    }

    @Test
    void shouldColorizeLogLevelsWhenEnabled() {
        assertThat(LoggingUtils.colorize("ERROR", true))
            .isEqualTo(LoggingUtils.ANSI_RED + "ERROR" + LoggingUtils.ANSI_RESET);
        
        assertThat(LoggingUtils.colorize("WARN", true))
            .isEqualTo(LoggingUtils.ANSI_YELLOW + "WARN" + LoggingUtils.ANSI_RESET);
        
        assertThat(LoggingUtils.colorize("INFO", true))
            .isEqualTo(LoggingUtils.ANSI_GREEN + "INFO" + LoggingUtils.ANSI_RESET);
        
        assertThat(LoggingUtils.colorize("DEBUG", true))
            .isEqualTo(LoggingUtils.ANSI_GREEN + "DEBUG" + LoggingUtils.ANSI_RESET);
        
        assertThat(LoggingUtils.colorize("TRACE", true))
            .isEqualTo(LoggingUtils.ANSI_GREEN + "TRACE" + LoggingUtils.ANSI_RESET);
    }

    @Test
    void shouldNotColorizeLogLevelsWhenDisabled() {
        assertThat(LoggingUtils.colorize("ERROR", false)).isEqualTo("ERROR");
        assertThat(LoggingUtils.colorize("WARN", false)).isEqualTo("WARN");
        assertThat(LoggingUtils.colorize("INFO", false)).isEqualTo("INFO");
        assertThat(LoggingUtils.colorize("DEBUG", false)).isEqualTo("DEBUG");
        assertThat(LoggingUtils.colorize("TRACE", false)).isEqualTo("TRACE");
    }

    @Test
    void shouldFormatJsonContent() {
        String json = "{\"name\":\"test\",\"value\":123}";
        String formatted = LoggingUtils.formatContent(json, "application/json");
        assertThat(formatted).contains("{\n");
        assertThat(formatted).contains("  \"name\" : \"test\"");
        assertThat(formatted).contains("  \"value\" : 123");
        assertThat(formatted).contains("\n}");
    }

    @Test
    void shouldFormatXmlContent() {
        String xml = "<root><name>test</name><value>123</value></root>";
        String formatted = LoggingUtils.formatContent(xml, "application/xml");
        assertThat(formatted).contains("<?xml");
        assertThat(formatted).contains("<root>");
        assertThat(formatted).contains("  <name>test</name>");
        assertThat(formatted).contains("  <value>123</value>");
        assertThat(formatted).contains("</root>");
    }

    @Test
    void shouldHandleNullContent() {
        assertThat(LoggingUtils.formatContent(null, "application/json")).isNull();
        assertThat(LoggingUtils.formatContent(null, "application/xml")).isNull();
    }

    @Test
    void shouldHandleEmptyContent() {
        assertThat(LoggingUtils.formatContent("", "application/json")).isEmpty();
        assertThat(LoggingUtils.formatContent("", "application/xml")).isEmpty();
    }

    @Test
    void shouldHandleInvalidContent() {
        String invalidJson = "{invalid json}";
        assertThat(LoggingUtils.formatContent(invalidJson, "application/json"))
            .isEqualTo(invalidJson);

        String invalidXml = "<invalid>xml<invalid>";
        assertThat(LoggingUtils.formatContent(invalidXml, "application/xml"))
            .isEqualTo(invalidXml);
    }
} 