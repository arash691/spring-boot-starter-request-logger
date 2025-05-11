package com.arash.ariani;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.arash.ariani.config.TestConfig;
import com.arash.ariani.controller.TestController;
import com.arash.ariani.interceptor.RequestLoggingInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.BeforeEach;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

@WebMvcTest(controllers = TestController.class)
@Import({TestConfig.class, TestApplication.class})
class RequestLoggingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(RequestLoggingInterceptor.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void whenPostUser_thenLogsRequestAndResponse() throws Exception {
        TestController.UserRequest request = new TestController.UserRequest(
            "john",
            "john@example.com",
            "secret123"
        );

        mockMvc.perform(post("/test/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anySatisfy(message -> {
                    assertThat(message).contains("Request Details");
                    assertThat(message).contains("Method: POST");
                    assertThat(message).contains("/test/user");
                    assertThat(message).contains("john@example.com");
                    assertThat(message).contains("\"password\":\"***\"");
                });
    }

    @Test
    void whenPostLogin_thenMasksSensitiveData() throws Exception {
        TestController.LoginRequest request = new TestController.LoginRequest(
            "jane",
            "password123"
        );

        mockMvc.perform(post("/test/login")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Secret-Header", "secret-value")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anySatisfy(message -> {
                    assertThat(message).contains("Request Details");
                    assertThat(message).contains("\"password\":\"***\"");
                    assertThat(message).doesNotContain("password123");
                    assertThat(message).doesNotContain("X-Secret-Header");
                })
                .anySatisfy(message -> {
                    assertThat(message).contains("Response Details");
                    assertThat(message).contains("\"token\":\"MASKED-TOKEN\"");
                    assertThat(message).doesNotContain("token-jane");
                });
    }

    @Test
    void whenGetPublicEndpoint_thenLogsWithGlobalConfig() throws Exception {
        mockMvc.perform(get("/test/public"))
                .andExpect(status().isOk());

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anySatisfy(message -> {
                    assertThat(message).contains("Request Details");
                    assertThat(message).contains("Method: GET");
                    assertThat(message).contains("/test/public");
                })
                .anySatisfy(message -> {
                    assertThat(message).contains("Response Details");
                    assertThat(message).contains("Status: 200");
                    assertThat(message).contains("public data");
                });
    }
} 