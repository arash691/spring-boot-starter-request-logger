package com.arash.ariani.actuator;

import com.arash.ariani.TestApplication;
import com.arash.ariani.properties.RequestLoggingProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = TestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class RequestLoggingEndpointTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RequestLoggingProperties properties;

    @Test
    void shouldGetConfiguration() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/actuator/requestLogger",
                Map.class
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("enabled")).isEqualTo(properties.isEnabled());
        assertThat(response.getBody().get("maskFields")).isEqualTo(properties.getMaskFields());
    }

    @Test
    void shouldUpdateConfiguration() {
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> body = Map.of(
                "enabled", false,
                "maskFields", "newField1,newField2"
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "http://localhost:" + port + "/actuator/requestLogger",
                HttpMethod.POST,
                request,
                Map.class
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("enabled")).isEqualTo(false);
        assertThat(response.getBody().get("maskFields")).isEqualTo("newField1,newField2");
        assertThat(properties.isEnabled()).isFalse();
        assertThat(properties.getMaskFields()).isEqualTo("newField1,newField2");
    }
} 