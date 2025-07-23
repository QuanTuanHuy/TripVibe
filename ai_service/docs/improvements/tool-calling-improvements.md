# Cải Tiến Hệ Thống Tool Calling - Spring AI

## Tổng Quan

Dựa trên tài liệu Spring AI về Tool Calling, đây là các đề xuất cải tiến để nâng cấp `ToolManager` hiện tại thành hệ thống tool calling mạnh mẽ và linh hoạt hơn.

## 1. Current System Analysis

### 1.1 Issues với ToolManager hiện tại
- Hardcoded tool registration
- Không support dynamic tool discovery
- Thiếu tool validation và error handling
- Không có tool versioning
- Limited observability

## 2. Enhanced Tool Architecture

### 2.1 Modern Tool Registration System

```java
@Component
public class AdvancedToolManager {
    
    private final Map<String, ToolCallback> toolRegistry = new ConcurrentHashMap<>();
    private final ToolCallbackResolver toolResolver;
    private final ToolExecutionExceptionProcessor exceptionProcessor;
    private final MeterRegistry meterRegistry;
    
    public AdvancedToolManager(ApplicationContext context, MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.toolResolver = createToolResolver(context);
        this.exceptionProcessor = new CustomToolExecutionExceptionProcessor();
        
        // Auto-discover tools
        discoverAndRegisterTools(context);
    }
    
    private void discoverAndRegisterTools(ApplicationContext context) {
        // Discover @Tool annotated methods
        Map<String, Object> toolBeans = context.getBeansWithAnnotation(ToolProvider.class);
        toolBeans.values().forEach(this::registerToolFromBean);
        
        // Discover Function beans
        Map<String, Function> functionBeans = context.getBeansOfType(Function.class);
        functionBeans.forEach(this::registerFunctionTool);
        
        // Register built-in hotel system tools
        registerHotelSystemTools();
    }
    
    private void registerHotelSystemTools() {
        // Hotel search tool
        registerTool("search_hotels", new HotelSearchTool());
        
        // Booking management tool
        registerTool("manage_booking", new BookingManagementTool());
        
        // Price calculation tool
        registerTool("calculate_price", new PriceCalculationTool());
        
        // Availability check tool
        registerTool("check_availability", new AvailabilityCheckTool());
    }
    
    public void registerTool(String name, ToolCallback tool) {
        toolRegistry.put(name, new InstrumentedToolCallback(tool, meterRegistry));
    }
    
    public Collection<ToolCallback> getAllTools() {
        return toolRegistry.values();
    }
    
    public ToolCallback getTool(String name) {
        return toolRegistry.get(name);
    }
}
```

### 2.2 Hotel Business Logic Tools

```java
@ToolProvider
@Component
public class HotelBookingTools {
    
    private final HotelService hotelService;
    private final BookingService bookingService;
    private final PricingService pricingService;
    
    @Tool(
        name = "search_hotels",
        description = """
            Tìm kiếm khách sạn theo tiêu chí của khách hàng.
            Có thể tìm theo vị trí, giá cả, tiện ích, và rating.
            """,
        returnDirect = false
    )
    public HotelSearchResult searchHotels(
            @ToolParam(description = "Thành phố hoặc khu vực cần tìm") String location,
            @ToolParam(description = "Ngày check-in (yyyy-mm-dd)") String checkIn,
            @ToolParam(description = "Ngày check-out (yyyy-mm-dd)") String checkOut,
            @ToolParam(description = "Số lượng khách", required = false) Integer guests,
            @ToolParam(description = "Ngân sách tối đa (VNĐ)", required = false) Long maxBudget,
            @ToolParam(description = "Tiện ích yêu cầu", required = false) List<String> amenities,
            ToolContext context) {
        
        try {
            SearchCriteria criteria = SearchCriteria.builder()
                .location(location)
                .checkIn(LocalDate.parse(checkIn))
                .checkOut(LocalDate.parse(checkOut))
                .guests(guests != null ? guests : 2)
                .maxBudget(maxBudget)
                .amenities(amenities)
                .userId((String) context.get("userId"))
                .build();
                
            List<Hotel> hotels = hotelService.searchHotels(criteria);
            
            return HotelSearchResult.builder()
                .hotels(hotels)
                .totalFound(hotels.size())
                .searchCriteria(criteria)
                .searchId(UUID.randomUUID().toString())
                .build();
                
        } catch (Exception e) {
            throw new ToolExecutionException("Error searching hotels", e);
        }
    }
    
    @Tool(
        name = "check_availability",
        description = "Kiểm tra tình trạng có phòng của khách sạn cụ thể"
    )
    public AvailabilityResult checkAvailability(
            @ToolParam(description = "ID của khách sạn") String hotelId,
            @ToolParam(description = "Ngày check-in (yyyy-mm-dd)") String checkIn,
            @ToolParam(description = "Ngày check-out (yyyy-mm-dd)") String checkOut,
            @ToolParam(description = "Loại phòng", required = false) String roomType) {
        
        try {
            AvailabilityRequest request = AvailabilityRequest.builder()
                .hotelId(hotelId)
                .checkIn(LocalDate.parse(checkIn))
                .checkOut(LocalDate.parse(checkOut))
                .roomType(roomType)
                .build();
                
            return hotelService.checkAvailability(request);
            
        } catch (Exception e) {
            throw new ToolExecutionException("Error checking availability", e);
        }
    }
    
    @Tool(
        name = "calculate_price",
        description = """
            Tính toán giá phòng bao gồm thuế, phí dịch vụ và các ưu đãi.
            Hỗ trợ multiple room types và promotion codes.
            """,
        resultConverter = PriceCalculationResultConverter.class
    )
    public PriceCalculation calculatePrice(
            @ToolParam(description = "ID của khách sạn") String hotelId,
            @ToolParam(description = "Loại phòng") String roomType,
            @ToolParam(description = "Ngày check-in (yyyy-mm-dd)") String checkIn,
            @ToolParam(description = "Ngày check-out (yyyy-mm-dd)") String checkOut,
            @ToolParam(description = "Số lượng phòng", required = false) Integer roomCount,
            @ToolParam(description = "Mã khuyến mãi", required = false) String promoCode) {
        
        try {
            PriceRequest request = PriceRequest.builder()
                .hotelId(hotelId)
                .roomType(roomType)
                .checkIn(LocalDate.parse(checkIn))
                .checkOut(LocalDate.parse(checkOut))
                .roomCount(roomCount != null ? roomCount : 1)
                .promoCode(promoCode)
                .build();
                
            return pricingService.calculatePrice(request);
            
        } catch (Exception e) {
            throw new ToolExecutionException("Error calculating price", e);
        }
    }
    
    @Tool(
        name = "create_booking",
        description = """
            Tạo đặt phòng mới cho khách hàng.
            Yêu cầu thông tin khách hàng và chi tiết thanh toán.
            """
    )
    public BookingResult createBooking(
            @ToolParam(description = "Thông tin đặt phòng") BookingRequest bookingRequest,
            ToolContext context) {
        
        try {
            String userId = (String) context.get("userId");
            if (userId == null) {
                throw new ToolExecutionException("User authentication required for booking");
            }
            
            bookingRequest.setUserId(userId);
            return bookingService.createBooking(bookingRequest);
            
        } catch (Exception e) {
            throw new ToolExecutionException("Error creating booking", e);
        }
    }
    
    @Tool(
        name = "get_booking_details",
        description = "Lấy thông tin chi tiết đặt phòng theo mã booking"
    )
    public BookingDetails getBookingDetails(
            @ToolParam(description = "Mã đặt phòng") String bookingId,
            ToolContext context) {
        
        try {
            String userId = (String) context.get("userId");
            return bookingService.getBookingDetails(bookingId, userId);
            
        } catch (Exception e) {
            throw new ToolExecutionException("Error retrieving booking details", e);
        }
    }
    
    @Tool(
        name = "cancel_booking",
        description = """
            Hủy đặt phòng và tính toán phí hủy (nếu có).
            Trả về thông tin về refund và cancellation policy.
            """
    )
    public CancellationResult cancelBooking(
            @ToolParam(description = "Mã đặt phòng cần hủy") String bookingId,
            @ToolParam(description = "Lý do hủy", required = false) String reason,
            ToolContext context) {
        
        try {
            String userId = (String) context.get("userId");
            
            CancellationRequest request = CancellationRequest.builder()
                .bookingId(bookingId)
                .userId(userId)
                .reason(reason)
                .requestTime(LocalDateTime.now())
                .build();
                
            return bookingService.cancelBooking(request);
            
        } catch (Exception e) {
            throw new ToolExecutionException("Error canceling booking", e);
        }
    }
}
```

## 3. Advanced Tool Features

### 3.1 Dynamic Tool Discovery

```java
@Component
public class DynamicToolRegistry {
    
    private final Map<String, ToolMetadata> toolMetadata = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher eventPublisher;
    
    @EventListener
    public void handleToolRegistration(ToolRegistrationEvent event) {
        ToolCallback tool = event.getTool();
        ToolDefinition definition = tool.getToolDefinition();
        
        // Validate tool
        validateTool(tool);
        
        // Register with metadata
        ToolMetadata metadata = ToolMetadata.builder()
            .name(definition.name())
            .description(definition.description())
            .version("1.0")
            .category(extractCategory(tool))
            .registrationTime(Instant.now())
            .build();
            
        toolMetadata.put(definition.name(), metadata);
        
        log.info("Tool registered: {} ({})", definition.name(), metadata.getCategory());
    }
    
    public List<ToolMetadata> getAvailableTools() {
        return new ArrayList<>(toolMetadata.values());
    }
    
    public List<ToolMetadata> getToolsByCategory(String category) {
        return toolMetadata.values().stream()
            .filter(meta -> category.equals(meta.getCategory()))
            .collect(Collectors.toList());
    }
    
    private void validateTool(ToolCallback tool) {
        ToolDefinition definition = tool.getToolDefinition();
        
        // Validate name
        if (definition.name() == null || definition.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Tool name cannot be empty");
        }
        
        // Validate description
        if (definition.description() == null || definition.description().length() < 20) {
            throw new IllegalArgumentException("Tool description must be at least 20 characters");
        }
        
        // Validate JSON schema
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(definition.inputSchema());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON schema for tool: " + definition.name());
        }
    }
}
```

### 3.2 Tool Execution with Context

```java
@Component
public class ContextAwareToolExecutor {
    
    private final ToolCallbackResolver toolResolver;
    private final ToolContextFactory contextFactory;
    
    public ToolExecutionResult executeTools(List<ToolCall> toolCalls, ChatRequest originalRequest) {
        List<Message> conversationHistory = new ArrayList<>();
        
        for (ToolCall toolCall : toolCalls) {
            // Create rich tool context
            ToolContext context = contextFactory.createContext(originalRequest, toolCall);
            
            // Execute tool with context
            ToolCallback tool = toolResolver.resolve(toolCall.getName());
            if (tool == null) {
                throw new ToolNotFoundException("Tool not found: " + toolCall.getName());
            }
            
            try {
                String result = tool.call(toolCall.getArguments(), context);
                
                // Add to conversation history
                conversationHistory.add(new ToolResponseMessage(
                    toolCall.getId(),
                    result
                ));
                
            } catch (Exception e) {
                log.error("Tool execution failed: {}", toolCall.getName(), e);
                
                // Add error response
                conversationHistory.add(new ToolResponseMessage(
                    toolCall.getId(),
                    "Error executing tool: " + e.getMessage()
                ));
            }
        }
        
        return ToolExecutionResult.builder()
            .conversationHistory(conversationHistory)
            .executedTools(toolCalls.size())
            .build();
    }
}

@Component
public class ToolContextFactory {
    
    private final UserService userService;
    private final SessionManager sessionManager;
    
    public ToolContext createContext(ChatRequest request, ToolCall toolCall) {
        Map<String, Object> context = new HashMap<>();
        
        // Add user information
        String userId = extractUserId(request);
        if (userId != null) {
            User user = userService.getUser(userId);
            context.put("userId", userId);
            context.put("userPreferences", user.getPreferences());
            context.put("userLocation", user.getLocation());
            context.put("userTier", user.getTier());
        }
        
        // Add session information
        String sessionId = extractSessionId(request);
        if (sessionId != null) {
            Session session = sessionManager.getSession(sessionId);
            context.put("sessionId", sessionId);
            context.put("sessionStartTime", session.getStartTime());
            context.put("previousRequests", session.getPreviousRequests());
        }
        
        // Add request metadata
        context.put("requestTime", Instant.now());
        context.put("requestId", UUID.randomUUID().toString());
        context.put("toolName", toolCall.getName());
        
        // Add business context
        context.put("systemMode", getSystemMode());
        context.put("availableInventory", getAvailableInventory());
        
        return new ToolContext(context);
    }
}
```

## 4. Tool Validation và Security

### 4.1 Input Validation

```java
@Component
public class ToolInputValidator {
    
    private final JsonSchemaValidator schemaValidator;
    private final Map<String, InputSanitizer> sanitizers;
    
    public void validateAndSanitizeInput(ToolCall toolCall) {
        String toolName = toolCall.getName();
        String input = toolCall.getArguments();
        
        // Schema validation
        ToolDefinition definition = getToolDefinition(toolName);
        ValidationResult result = schemaValidator.validate(input, definition.inputSchema());
        
        if (!result.isValid()) {
            throw new ToolValidationException(
                "Invalid input for tool " + toolName + ": " + result.getErrors());
        }
        
        // Content sanitization
        InputSanitizer sanitizer = sanitizers.get(toolName);
        if (sanitizer != null) {
            String sanitizedInput = sanitizer.sanitize(input);
            toolCall.setArguments(sanitizedInput);
        }
        
        // Business rule validation
        validateBusinessRules(toolCall);
    }
    
    private void validateBusinessRules(ToolCall toolCall) {
        switch (toolCall.getName()) {
            case "search_hotels":
                validateHotelSearch(toolCall);
                break;
            case "create_booking":
                validateBookingCreation(toolCall);
                break;
            case "calculate_price":
                validatePriceCalculation(toolCall);
                break;
        }
    }
    
    private void validateHotelSearch(ToolCall toolCall) {
        try {
            JsonNode args = parseArguments(toolCall.getArguments());
            
            // Validate dates
            String checkIn = args.get("checkIn").asText();
            String checkOut = args.get("checkOut").asText();
            
            LocalDate checkInDate = LocalDate.parse(checkIn);
            LocalDate checkOutDate = LocalDate.parse(checkOut);
            
            if (checkInDate.isBefore(LocalDate.now())) {
                throw new ToolValidationException("Check-in date cannot be in the past");
            }
            
            if (checkOutDate.isBefore(checkInDate)) {
                throw new ToolValidationException("Check-out date must be after check-in date");
            }
            
            // Validate guest count
            int guests = args.has("guests") ? args.get("guests").asInt() : 2;
            if (guests < 1 || guests > 20) {
                throw new ToolValidationException("Guest count must be between 1 and 20");
            }
            
        } catch (Exception e) {
            throw new ToolValidationException("Invalid hotel search parameters", e);
        }
    }
}
```

### 4.2 Security Layer

```java
@Component
public class ToolSecurityManager {
    
    private final Map<String, Set<String>> toolPermissions;
    private final UserRoleService roleService;
    
    public void checkPermission(String toolName, String userId) {
        if (userId == null) {
            // Public tools that don't require authentication
            Set<String> publicTools = Set.of("search_hotels", "check_availability", "calculate_price");
            if (!publicTools.contains(toolName)) {
                throw new ToolSecurityException("Authentication required for tool: " + toolName);
            }
            return;
        }
        
        User user = roleService.getUser(userId);
        Set<String> userRoles = user.getRoles();
        Set<String> requiredRoles = toolPermissions.get(toolName);
        
        if (requiredRoles != null && Collections.disjoint(userRoles, requiredRoles)) {
            throw new ToolSecurityException(
                "Insufficient permissions for tool: " + toolName + 
                ". Required: " + requiredRoles + ", User has: " + userRoles);
        }
    }
    
    @PostConstruct
    public void initializePermissions() {
        toolPermissions.put("create_booking", Set.of("USER", "PREMIUM", "ADMIN"));
        toolPermissions.put("cancel_booking", Set.of("USER", "PREMIUM", "ADMIN"));
        toolPermissions.put("get_booking_details", Set.of("USER", "PREMIUM", "ADMIN"));
        toolPermissions.put("admin_override", Set.of("ADMIN"));
        toolPermissions.put("bulk_operations", Set.of("ADMIN", "MANAGER"));
    }
}
```

## 5. Tool Performance và Monitoring

### 5.1 Tool Metrics Collection

```java
@Component
public class ToolMetricsCollector {
    
    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> toolCounters;
    private final Map<String, Timer> toolTimers;
    
    public ToolMetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.toolCounters = new ConcurrentHashMap<>();
        this.toolTimers = new ConcurrentHashMap<>();
    }
    
    public void recordToolExecution(String toolName, Duration duration, boolean success) {
        // Count executions
        Counter counter = toolCounters.computeIfAbsent(toolName, name ->
            Counter.builder("tool.execution.count")
                .tag("tool", name)
                .tag("success", String.valueOf(success))
                .description("Number of tool executions")
                .register(meterRegistry));
        counter.increment();
        
        // Track execution time
        Timer timer = toolTimers.computeIfAbsent(toolName, name ->
            Timer.builder("tool.execution.duration")
                .tag("tool", name)
                .description("Tool execution duration")
                .register(meterRegistry));
        timer.record(duration);
        
        // Record success rate
        Gauge.builder("tool.success.rate")
            .tag("tool", toolName)
            .description("Tool success rate")
            .register(meterRegistry, this, collector -> collector.calculateSuccessRate(toolName));
    }
    
    private double calculateSuccessRate(String toolName) {
        // Calculate success rate based on counters
        return 0.95; // Placeholder
    }
}
```

### 5.2 Tool Circuit Breaker

```java
@Component
public class ToolCircuitBreakerManager {
    
    private final Map<String, CircuitBreaker> circuitBreakers;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    
    public ToolCircuitBreakerManager() {
        this.circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();
        this.circuitBreakers = new ConcurrentHashMap<>();
    }
    
    public <T> T executeWithCircuitBreaker(String toolName, Supplier<T> supplier) {
        CircuitBreaker circuitBreaker = getOrCreateCircuitBreaker(toolName);
        
        Supplier<T> decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, supplier);
        
        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            log.error("Tool {} failed with circuit breaker", toolName, e);
            throw new ToolExecutionException("Tool temporarily unavailable: " + toolName, e);
        }
    }
    
    private CircuitBreaker getOrCreateCircuitBreaker(String toolName) {
        return circuitBreakers.computeIfAbsent(toolName, name -> {
            CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .build();
                
            return circuitBreakerRegistry.circuitBreaker(name, config);
        });
    }
}
```

## 6. Tool Testing Framework

### 6.1 Tool Integration Testing

```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.ai.tools.test.enabled=true"
})
class HotelBookingToolsIntegrationTest {
    
    @Autowired
    private AdvancedToolManager toolManager;
    
    @MockBean
    private HotelService hotelService;
    
    @MockBean
    private BookingService bookingService;
    
    @Test
    void should_search_hotels_successfully() {
        // Given
        List<Hotel> mockHotels = createMockHotels();
        when(hotelService.searchHotels(any())).thenReturn(mockHotels);
        
        ToolContext context = new ToolContext(Map.of("userId", "test-user"));
        
        // When
        ToolCallback searchTool = toolManager.getTool("search_hotels");
        String result = searchTool.call("""
            {
                "location": "Da Nang",
                "checkIn": "2024-12-01",
                "checkOut": "2024-12-03",
                "guests": 2,
                "maxBudget": 2000000
            }
            """, context);
        
        // Then
        assertThat(result).isNotNull();
        HotelSearchResult searchResult = parseResult(result, HotelSearchResult.class);
        assertThat(searchResult.getHotels()).hasSize(2);
        assertThat(searchResult.getTotalFound()).isEqualTo(2);
    }
    
    @Test
    void should_handle_tool_validation_errors() {
        // Given
        ToolContext context = new ToolContext(Map.of("userId", "test-user"));
        
        // When & Then
        ToolCallback searchTool = toolManager.getTool("search_hotels");
        assertThatThrownBy(() -> searchTool.call("""
            {
                "location": "Da Nang",
                "checkIn": "2023-12-01",
                "checkOut": "2024-12-03"
            }
            """, context))
            .isInstanceOf(ToolExecutionException.class)
            .hasMessageContaining("Check-in date cannot be in the past");
    }
    
    @Test
    void should_require_authentication_for_booking() {
        // Given - No user context
        ToolContext context = new ToolContext(Map.of());
        
        // When & Then
        ToolCallback bookingTool = toolManager.getTool("create_booking");
        assertThatThrownBy(() -> bookingTool.call("{}", context))
            .isInstanceOf(ToolExecutionException.class)
            .hasMessageContaining("User authentication required");
    }
}
```

## 7. Configuration

### 7.1 Tool Configuration Properties

```yaml
spring:
  ai:
    tools:
      hotel-booking:
        enabled: true
        timeout: 30s
        retry:
          max-attempts: 3
          backoff-delay: 1s
        circuit-breaker:
          failure-rate-threshold: 50
          wait-duration: 30s
          sliding-window-size: 10
        security:
          require-auth: 
            - create_booking
            - cancel_booking
            - get_booking_details
          public-tools:
            - search_hotels
            - check_availability
            - calculate_price
      validation:
        strict-mode: true
        max-input-size: 10KB
        sanitize-input: true
      monitoring:
        metrics-enabled: true
        detailed-logging: true
        trace-execution: true
```

## 8. Migration Plan

### Phase 1: Foundation (Week 1-2)
- [ ] Implement AdvancedToolManager
- [ ] Create basic hotel tools
- [ ] Add tool validation framework

### Phase 2: Security & Context (Week 3-4)
- [ ] Implement security manager
- [ ] Add context-aware execution
- [ ] Create tool registry

### Phase 3: Advanced Features (Week 5-6)
- [ ] Add circuit breakers
- [ ] Implement metrics collection
- [ ] Create dynamic tool discovery

### Phase 4: Testing & Documentation (Week 7-8)
- [ ] Comprehensive testing framework
- [ ] Performance optimization
- [ ] Documentation và examples

## 9. Expected Benefits

1. **Modularity**: Tools dễ maintain và extend
2. **Security**: Robust authentication và authorization
3. **Performance**: Circuit breakers và caching
4. **Observability**: Comprehensive metrics và tracing
5. **Reliability**: Input validation và error handling
6. **Scalability**: Dynamic tool discovery và registration

Với system mới này, tool calling sẽ trở nên professional, secure và scalable hơn đáng kể.
