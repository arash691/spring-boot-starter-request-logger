# Spring Boot Request Logger Starter

A Spring Boot starter for comprehensive HTTP request/response logging with powerful features like correlation IDs, sensitive data masking, and customizable templates.

## Features

- 🔍 Detailed request/response logging
- 🎨 Spring Boot style logging format with ANSI colors
- 🔐 Sensitive data masking
- 🎯 Correlation ID tracking
- ⚡ Performance timing
- 📝 Customizable logging templates
- 🎛️ Actuator endpoint for runtime configuration
- 📊 Micrometer metrics integration
- 🔧 Annotation-based configuration

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.wallex</groupId>
    <artifactId>spring-boot-starter-request-logger</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Quick Start

1. Enable request logging by adding `@EnableRequestLogging` to your Spring Boot application:

```java
@SpringBootApplication
@EnableRequestLogging
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

2. Configure logging properties in `application.properties` or `application.yml`:

```properties
# Enable/disable request logging globally
request.logging.enabled=true

# Configure what to include in logs
request.logging.include-headers=true
request.logging.include-parameters=true
request.logging.include-request-body=true
request.logging.include-response-body=true
request.logging.include-timing=true

# Enable/disable ANSI colors in console output
request.logging.enable-ansi-color=true

# Set maximum body length to log
request.logging.max-body-length=1000

# Configure sensitive data masking
request.logging.masking-patterns[0].pattern=password":"[^"]*"
request.logging.masking-patterns[0].replacement=password":"***"
```

## Usage Examples

### Basic Usage

All endpoints will be logged automatically when request logging is enabled:

```java
@RestController
@RequestMapping("/api")
public class UserController {
    
    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        // Method implementation
        return user;
    }
}
```

Example log output:
```
2024-03-21 10:15:30.123 INFO 12345 --- [main] com.example.UserController : Request Details:
Correlation ID: a7591a00-b601-4c70-b7ab-1b3ae6d460a4
Method: POST
URI: /api/users
Headers: {Content-Type=application/json}
Parameters: {}
Body: {"name":"john","email":"john@example.com","password":"***"}

2024-03-21 10:15:30.456 INFO 12345 --- [main] com.example.UserController : Response Details:
Correlation ID: a7591a00-b601-4c70-b7ab-1b3ae6d460a4
Status: 200
Duration: 42ms
Headers: {Content-Type=application/json}
Body: {"id":1,"name":"john","email":"john@example.com"}
```

### Method-Level Configuration

Use `@LogRequest` annotation to configure logging for specific endpoints:

```java
@RestController
@RequestMapping("/api")
public class UserController {
    
    @PostMapping("/login")
    @LogRequest(
        maskPatterns = {
            "password:.*:***",
            "token:.*:MASKED-TOKEN"
        },
        excludeHeaders = {"Authorization"},
        includeParameters = false
    )
    public LoginResponse login(@RequestBody LoginRequest request) {
        // Method implementation
        return new LoginResponse("token123", "Login successful");
    }
}
```

### Actuator Endpoint

Enable the actuator endpoint to view and modify logging configuration at runtime:

```properties
management.endpoints.web.exposure.include=requestlogging
management.endpoint.requestlogging.enabled=true
```

Access the endpoint:
- GET `/actuator/requestlogging` - View current configuration
- POST `/actuator/requestlogging` - Update configuration

### Metrics

Request logging metrics are automatically available through Micrometer:

```java
@Autowired
MeterRegistry meterRegistry;

public void checkMetrics() {
    // Total requests
    Counter requests = meterRegistry.get("request.logger.total").counter();
    
    // Response times
    Timer timer = meterRegistry.get("request.logger.duration").timer();
}
```

## Advanced Configuration

### Custom Masking Patterns

Configure global masking patterns in your configuration:

```java
@Configuration
public class RequestLoggingConfig {
    
    @Bean
    public RequestLoggingProperties requestLoggingProperties() {
        RequestLoggingProperties properties = new RequestLoggingProperties();
        properties.getMaskingPatterns().add(
            new RequestLoggingProperties.MaskingPattern(
                "\"ssn\":\"\\d{3}-\\d{2}-\\d{4}\"",
                "\"ssn\":\"***-**-****\""
            )
        );
        return properties;
    }
}
```

### Custom Logging Templates

Create custom templates for request and response logging:

```java
@Configuration
public class LoggingConfig {
    
    @Bean
    public LoggingTemplate[] loggingTemplates() {
        return new LoggingTemplate.Builder()
            .requestTemplate("API Request - Method: {{method}}, URI: {{uri}}, CorrelationId: {{correlationId}}")
            .responseTemplate("API Response - Status: {{status}}, Duration: {{duration}}ms, CorrelationId: {{correlationId}}")
            .build();
    }
}
```

### Logging Format

The default logging format follows Spring Boot's standard format:

```
TIMESTAMP LEVEL PID --- [THREAD] LOGGER : MESSAGE
```

For example:
```
2024-03-21 10:15:30.123 INFO 12345 --- [main] com.example.Controller : Request received
```

When ANSI colors are enabled (`request.logging.enable-ansi-color=true`), log levels are colored:
- ERROR - Red
- WARN - Yellow
- INFO - Green
- DEBUG - Green
- TRACE - Green

## Spring Boot Version Compatibility

This starter supports both Spring Boot 2 and 3:

- Spring Boot 3.x: Uses Jakarta EE dependencies
- Spring Boot 2.x: Uses Java EE dependencies

The appropriate dependencies are automatically selected based on your Spring Boot version.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details. 