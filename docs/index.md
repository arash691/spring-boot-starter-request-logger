# Spring Boot Request Logger Starter

A powerful and flexible Spring Boot starter for comprehensive HTTP request/response logging with advanced features like correlation IDs, sensitive data masking, and customizable templates.

## Features

- 🔍 **Detailed Request/Response Logging**
  - Headers, parameters, and body content
  - Configurable inclusion/exclusion of components
  - Support for both JSON and XML content formatting
  - Spring Boot style logging format
  - ANSI color support for log levels

- 🔐 **Sensitive Data Protection**
  - Pattern-based masking of sensitive information
  - Configurable masking patterns globally or per-endpoint
  - Default masking for common sensitive headers

- 🎯 **Request Tracing**
  - Automatic correlation ID generation
  - Request/response pairing
  - Timing information

- ⚡ **Performance Optimized**
  - Configurable body length limits
  - Efficient content caching
  - Minimal overhead

- 📝 **Customizable Logging**
  - Template-based log formatting
  - Support for custom templates
  - Flexible log level configuration

- 🎛️ **Runtime Configuration**
  - Actuator endpoint for dynamic configuration
  - Enable/disable logging at runtime
  - Modify masking patterns on the fly

- 📊 **Metrics Integration**
  - Micrometer metrics support
  - Request count and timing statistics
  - Masked field tracking

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.arash.ariani</groupId>
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

2. Configure logging properties in `application.yml`:

```yaml
request:
  logging:
    enabled: true
    include-headers: true
    include-parameters: true
    include-request-body: true
    include-response-body: true
    include-timing: true
    max-body-length: 1000
    exclude-headers:
      - Authorization
      - Cookie
    masking-patterns:
      - field-name: password
        pattern: "password\":\"[^\"]*\""
        replacement: "password\":\"***\""
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
        return userService.createUser(user);
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
        return authService.login(request);
    }
}
```

### Global Configuration

Configure global masking patterns in your configuration class:

```java
@Configuration
public class RequestLoggingConfig {
    
    @Bean
    public RequestLoggingProperties requestLoggingProperties() {
        RequestLoggingProperties properties = new RequestLoggingProperties();
        properties.getMaskingPatterns().add(
            new RequestLoggingProperties.MaskingPattern(
                "ssn",
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

### Actuator Integration

Enable the actuator endpoint to manage logging configuration at runtime:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: requestlogging
  endpoint:
    requestlogging:
      enabled: true
```

Access the endpoint:
- GET `/actuator/requestlogging` - View current configuration
- POST `/actuator/requestlogging` - Update configuration

Example POST request:
```json
{
  "enabled": false,
  "maskFields": "creditCard,ssn,password"
}
```

### Metrics

Request logging metrics are automatically available through Micrometer:

```java
@Service
public class MetricsService {
    
    private final MeterRegistry meterRegistry;
    
    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    public void checkMetrics() {
        // Total requests
        Counter requests = meterRegistry.get("request.logger.total").counter();
        
        // Response times
        Timer timer = meterRegistry.get("request.logger.duration").timer();
        
        // Masked fields count
        Counter maskedFields = meterRegistry.get("request.logger.masked.fields").counter();
    }
}
```

## Advanced Configuration

### Logging Format and Colors

The default logging format follows Spring Boot's standard format:

```
TIMESTAMP LEVEL PID --- [THREAD] LOGGER : MESSAGE
```

For example:
```
2024-03-21 10:15:30.123 INFO 12345 --- [main] com.example.Controller : Request received
```

Enable ANSI colors in console output:

```yaml
request:
  logging:
    enable-ansi-color: true
```

When ANSI colors are enabled, log levels are colored:
- ERROR - Red
- WARN - Yellow
- INFO - Green
- DEBUG - Green
- TRACE - Green

### Logging Levels

Configure logging levels in your `application.properties`:

```properties
logging.level.com.arash.ariani.interceptor.RequestLoggingInterceptor=DEBUG
```

### Custom Masking Patterns

Create complex masking patterns:

```yaml
request:
  logging:
    masking-patterns:
      - field-name: creditCard
        pattern: "\"creditCard\":\"\\d{4}-\\d{4}-\\d{4}-\\d{4}\""
        replacement: "\"creditCard\":\"****-****-****-$4\""
      - field-name: email
        pattern: "\"email\":\"[^\"@]+@[^\"]+\""
        replacement: "\"email\":\"***@$1\""
```

### Performance Tuning

Optimize logging performance:

```yaml
request:
  logging:
    max-body-length: 1000
    include-timing: true
    include-parameters: false
    include-headers: false
```

## Spring Boot Version Compatibility

This starter supports both Spring Boot 2 and 3:

- Spring Boot 3.x: Uses Jakarta EE dependencies
- Spring Boot 2.x: Uses Java EE dependencies

The appropriate dependencies are automatically selected based on your Spring Boot version.

## Common Issues and Solutions

### Issue: Sensitive Data Leakage

**Problem**: Sensitive data appears in logs despite masking configuration.

**Solution**: 
1. Verify masking patterns are correct
2. Use more specific patterns
3. Add default masking for common patterns

```yaml
request:
  logging:
    masking-patterns:
      - field-name: password
        pattern: "(?i)\"password\":\\s*\"[^\"]*\""
        replacement: "\"password\":\"***\""
```

### Issue: High Memory Usage

**Problem**: Large request/response bodies consume excessive memory.

**Solution**: 
1. Configure appropriate body length limits
2. Disable body logging for specific endpoints
3. Use streaming for large payloads

```yaml
request:
  logging:
    max-body-length: 500
    include-request-body: false
    include-response-body: false
```

### Issue: Missing Correlation IDs

**Problem**: Correlation IDs not appearing in logs.

**Solution**: 
1. Ensure `RequestLoggingFilter` is registered
2. Configure proper order for filters
3. Enable correlation ID logging in templates

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.