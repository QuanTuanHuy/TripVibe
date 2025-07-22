# Cải Tiến Document Processing và ETL Pipeline - Spring AI

## Tổng Quan

Dựa trên tài liệu Spring AI về ETL Pipeline, đây là các đề xuất cải tiến toàn diện cho việc xử lý và ingestion documents trong hệ thống AI service.

## 1. Current System Analysis

### 1.1 Issues với DocumentIngestionService hiện tại
- Simple file processing không scalable
- Không support multiple document formats
- Thiếu advanced text processing
- Limited metadata extraction
- Không có document versioning
- Manual chunking strategy

### 1.2 Proposed Enhanced Architecture

```java
@Service
@Slf4j
public class AdvancedDocumentIngestionService implements IDocumentIngestionService {
    
    private final DocumentProcessorFactory processorFactory;
    private final DocumentTransformerPipeline transformerPipeline;
    private final VectorStore vectorStore;
    private final DocumentMetadataEnricher metadataEnricher;
    private final DocumentVersionManager versionManager;
    
    @Override
    public DocumentIngestionResponse ingestDocuments(DocumentIngestionRequest request) {
        try {
            // 1. Create processing pipeline based on document types
            ETLPipeline pipeline = createPipeline(request);
            
            // 2. Process documents through pipeline
            List<Document> processedDocs = pipeline.process(request.getFiles());
            
            // 3. Store in vector database
            vectorStore.write(processedDocs);
            
            // 4. Update document registry
            DocumentRegistry registry = updateDocumentRegistry(processedDocs);
            
            return DocumentIngestionResponse.builder()
                .processedDocuments(processedDocs.size())
                .registry(registry)
                .processingTime(System.currentTimeMillis())
                .build();
                
        } catch (Exception e) {
            log.error("Error in document ingestion", e);
            throw new DocumentIngestionException("Failed to ingest documents", e);
        }
    }
}
```

## 2. Advanced Document Readers

### 2.1 Multi-Format Document Reader Factory

```java
@Component
public class DocumentReaderFactory {
    
    private final Map<String, DocumentReader> readers;
    
    public DocumentReaderFactory() {
        this.readers = new HashMap<>();
        registerReaders();
    }
    
    private void registerReaders() {
        // Text formats
        readers.put("txt", new TextDocumentReader());
        readers.put("md", new MarkdownDocumentReader());
        
        // Office formats
        readers.put("pdf", new ParagraphPdfDocumentReader());
        readers.put("docx", new TikaDocumentReader());
        readers.put("pptx", new TikaDocumentReader());
        readers.put("xlsx", new ExcelDocumentReader());
        
        // Web formats
        readers.put("html", new JsoupDocumentReader());
        readers.put("xml", new XMLDocumentReader());
        
        // Data formats
        readers.put("json", new JsonDocumentReader());
        readers.put("csv", new CSVDocumentReader());
        readers.put("yaml", new YAMLDocumentReader());
        
        // Hotel-specific formats
        readers.put("booking", new BookingDataReader());
        readers.put("hotel", new HotelDataReader());
    }
    
    public DocumentReader getReader(String fileExtension) {
        DocumentReader reader = readers.get(fileExtension.toLowerCase());
        if (reader == null) {
            throw new UnsupportedDocumentFormatException("Unsupported format: " + fileExtension);
        }
        return reader;
    }
    
    public List<Document> readDocument(MultipartFile file) {
        String extension = getFileExtension(file.getOriginalFilename());
        DocumentReader reader = getReader(extension);
        
        try {
            // Convert MultipartFile to Resource
            Resource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            
            // Configure reader with metadata
            if (reader instanceof ConfigurableDocumentReader) {
                ((ConfigurableDocumentReader) reader).configure(
                    DocumentReaderConfig.builder()
                        .filename(file.getOriginalFilename())
                        .fileSize(file.getSize())
                        .contentType(file.getContentType())
                        .uploadTime(Instant.now())
                        .build()
                );
            }
            
            return reader.read();
            
        } catch (Exception e) {
            throw new DocumentProcessingException("Error reading document: " + file.getOriginalFilename(), e);
        }
    }
}
```

### 2.2 Hotel-Specific Document Readers

```java
@Component
public class HotelDataReader implements DocumentReader {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public List<Document> read() {
        // Implementation for reading hotel data from various sources
        return Collections.emptyList();
    }
    
    public List<Document> readHotelCatalog(Resource resource) {
        try {
            HotelCatalog catalog = objectMapper.readValue(resource.getInputStream(), HotelCatalog.class);
            
            return catalog.getHotels().stream()
                .map(this::convertHotelToDocument)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            throw new DocumentProcessingException("Error reading hotel catalog", e);
        }
    }
    
    private Document convertHotelToDocument(Hotel hotel) {
        StringBuilder content = new StringBuilder();
        
        // Hotel basic information
        content.append("Tên khách sạn: ").append(hotel.getName()).append("\n");
        content.append("Địa chỉ: ").append(hotel.getAddress()).append("\n");
        content.append("Thành phố: ").append(hotel.getCity()).append("\n");
        content.append("Rating: ").append(hotel.getRating()).append(" sao\n");
        
        // Description
        if (hotel.getDescription() != null) {
            content.append("Mô tả: ").append(hotel.getDescription()).append("\n");
        }
        
        // Amenities
        if (hotel.getAmenities() != null && !hotel.getAmenities().isEmpty()) {
            content.append("Tiện ích: ").append(String.join(", ", hotel.getAmenities())).append("\n");
        }
        
        // Room types and prices
        if (hotel.getRooms() != null) {
            content.append("Loại phòng:\n");
            for (Room room : hotel.getRooms()) {
                content.append("- ").append(room.getType())
                      .append(": ").append(room.getPrice()).append(" VNĐ/đêm\n");
            }
        }
        
        // Reviews summary
        if (hotel.getReviews() != null) {
            content.append("Đánh giá: ").append(hotel.getAverageRating())
                  .append("/5 (").append(hotel.getReviews().size()).append(" đánh giá)\n");
        }
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("hotel_id", hotel.getId());
        metadata.put("hotel_name", hotel.getName());
        metadata.put("city", hotel.getCity());
        metadata.put("rating", hotel.getRating());
        metadata.put("price_range", calculatePriceRange(hotel));
        metadata.put("amenities", hotel.getAmenities());
        metadata.put("document_type", "hotel_profile");
        metadata.put("last_updated", hotel.getLastUpdated());
        
        return Document.builder()
            .text(content.toString())
            .metadata(metadata)
            .build();
    }
}
```

### 2.3 Booking Data Reader

```java
@Component
public class BookingDataReader implements DocumentReader {
    
    @Override
    public List<Document> read() {
        return Collections.emptyList();
    }
    
    public List<Document> readBookingPolicies(Resource resource) {
        try {
            BookingPolicies policies = objectMapper.readValue(resource.getInputStream(), BookingPolicies.class);
            
            List<Document> documents = new ArrayList<>();
            
            // Cancellation policies
            for (CancellationPolicy policy : policies.getCancellationPolicies()) {
                documents.add(createPolicyDocument(policy));
            }
            
            // Payment policies
            for (PaymentPolicy policy : policies.getPaymentPolicies()) {
                documents.add(createPaymentPolicyDocument(policy));
            }
            
            // Terms and conditions
            documents.add(createTermsDocument(policies.getTermsAndConditions()));
            
            return documents;
            
        } catch (Exception e) {
            throw new DocumentProcessingException("Error reading booking policies", e);
        }
    }
    
    private Document createPolicyDocument(CancellationPolicy policy) {
        StringBuilder content = new StringBuilder();
        
        content.append("Chính sách hủy đặt phòng\n");
        content.append("Loại: ").append(policy.getType()).append("\n");
        content.append("Thời hạn hủy: ").append(policy.getCancellationDeadline()).append("\n");
        content.append("Phí hủy: ").append(policy.getCancellationFee()).append("\n");
        content.append("Điều kiện: ").append(policy.getConditions()).append("\n");
        
        if (policy.getExceptions() != null) {
            content.append("Ngoại lệ:\n");
            for (String exception : policy.getExceptions()) {
                content.append("- ").append(exception).append("\n");
            }
        }
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("document_type", "cancellation_policy");
        metadata.put("policy_type", policy.getType());
        metadata.put("hotel_category", policy.getApplicableHotelCategories());
        
        return Document.builder()
            .text(content.toString())
            .metadata(metadata)
            .build();
    }
}
```

## 3. Advanced Document Transformers

### 3.1 Smart Text Splitter

```java
@Component
public class SmartTextSplitter implements DocumentTransformer {
    
    private final ChatClient splitterClient;
    private final TokenCounter tokenCounter;
    
    public SmartTextSplitter(ChatModel chatModel) {
        this.splitterClient = ChatClient.builder(chatModel)
            .defaultSystem("""
                Bạn là chuyên gia chia nhỏ văn bản thông minh.
                Chia văn bản thành các đoạn logic, mỗi đoạn tập trung vào một chủ đề cụ thể.
                Đảm bảo mỗi đoạn có đủ context để hiểu được khi đọc riêng lẻ.
                
                Quy tắc:
                1. Chia theo đoạn văn tự nhiên
                2. Giữ nguyên thông tin quan trọng như tên, giá, địa chỉ
                3. Mỗi chunk từ 200-800 tokens
                4. Overlap 50 tokens giữa các chunk liên tiếp
                """)
            .build();
    }
    
    @Override
    public List<Document> apply(List<Document> documents) {
        return documents.stream()
            .flatMap(this::splitDocument)
            .collect(Collectors.toList());
    }
    
    private Stream<Document> splitDocument(Document document) {
        int tokenCount = tokenCounter.count(document.getText());
        
        if (tokenCount <= 800) {
            return Stream.of(document); // No need to split
        }
        
        return splitIntelligently(document).stream();
    }
    
    private List<Document> splitIntelligently(Document document) {
        String text = document.getText();
        
        // For hotel documents, split by logical sections
        if ("hotel_profile".equals(document.getMetadata().get("document_type"))) {
            return splitHotelDocument(document);
        }
        
        // For policy documents, split by policy sections
        if (document.getMetadata().get("document_type").toString().contains("policy")) {
            return splitPolicyDocument(document);
        }
        
        // Default: Use semantic splitting
        return splitSemanically(document);
    }
    
    private List<Document> splitHotelDocument(Document document) {
        List<Document> chunks = new ArrayList<>();
        String text = document.getText();
        Map<String, Object> baseMetadata = new HashMap<>(document.getMetadata());
        
        // Split by sections
        String[] sections = text.split("\\n(?=\\w+:)"); // Split on lines that start with "Word:"
        
        for (int i = 0; i < sections.length; i++) {
            String section = sections[i].trim();
            if (section.isEmpty()) continue;
            
            // Add overlap with previous section
            if (i > 0) {
                String previousSection = sections[i-1];
                String[] previousLines = previousSection.split("\\n");
                if (previousLines.length > 2) {
                    section = previousLines[previousLines.length-1] + "\\n" + section;
                }
            }
            
            Map<String, Object> chunkMetadata = new HashMap<>(baseMetadata);
            chunkMetadata.put("chunk_index", i);
            chunkMetadata.put("chunk_type", determineSectionType(section));
            
            chunks.add(Document.builder()
                .text(section)
                .metadata(chunkMetadata)
                .build());
        }
        
        return chunks;
    }
    
    private String determineSectionType(String section) {
        String lowerSection = section.toLowerCase();
        
        if (lowerSection.contains("tên khách sạn") || lowerSection.contains("địa chỉ")) {
            return "basic_info";
        } else if (lowerSection.contains("tiện ích")) {
            return "amenities";
        } else if (lowerSection.contains("loại phòng") || lowerSection.contains("giá")) {
            return "rooms_pricing";
        } else if (lowerSection.contains("đánh giá") || lowerSection.contains("review")) {
            return "reviews";
        } else {
            return "description";
        }
    }
}
```

### 3.2 Metadata Enhancement Pipeline

```java
@Component
public class MetadataEnhancementPipeline implements DocumentTransformer {
    
    private final List<MetadataEnricher> enrichers;
    
    public MetadataEnhancementPipeline(
            KeywordMetadataEnricher keywordEnricher,
            SummaryMetadataEnricher summaryEnricher,
            LocationMetadataEnricher locationEnricher,
            PriceMetadataEnricher priceEnricher,
            SentimentMetadataEnricher sentimentEnricher) {
        
        this.enrichers = Arrays.asList(
            keywordEnricher,
            summaryEnricher,
            locationEnricher,
            priceEnricher,
            sentimentEnricher
        );
    }
    
    @Override
    public List<Document> apply(List<Document> documents) {
        List<Document> enriched = documents;
        
        // Apply each enricher in sequence
        for (MetadataEnricher enricher : enrichers) {
            enriched = enricher.apply(enriched);
        }
        
        return enriched;
    }
}

@Component
public class LocationMetadataEnricher implements DocumentTransformer {
    
    private final ChatClient locationExtractor;
    private final GeocodingService geocodingService;
    
    public LocationMetadataEnricher(ChatModel chatModel, GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
        this.locationExtractor = ChatClient.builder(chatModel)
            .defaultSystem("""
                Trích xuất thông tin địa điểm từ văn bản về khách sạn.
                
                Trả về JSON format:
                {
                    "city": "tên thành phố",
                    "district": "quận/huyện", 
                    "address": "địa chỉ cụ thể",
                    "landmarks": ["địa danh gần đó"],
                    "coordinates": "tọa độ nếu có"
                }
                
                Nếu không tìm thấy thông tin, trả về null cho field đó.
                """)
            .build();
    }
    
    @Override
    public List<Document> apply(List<Document> documents) {
        return documents.stream()
            .map(this::enrichWithLocation)
            .collect(Collectors.toList());
    }
    
    private Document enrichWithLocation(Document document) {
        try {
            String locationInfo = locationExtractor.prompt()
                .user("Trích xuất thông tin địa điểm:\n\n" + document.getText())
                .call()
                .content();
                
            LocationData location = parseLocationData(locationInfo);
            
            Map<String, Object> enrichedMetadata = new HashMap<>(document.getMetadata());
            
            if (location != null) {
                enrichedMetadata.put("city", location.getCity());
                enrichedMetadata.put("district", location.getDistrict());
                enrichedMetadata.put("address", location.getAddress());
                enrichedMetadata.put("landmarks", location.getLandmarks());
                
                // Geocoding for coordinates
                if (location.getAddress() != null) {
                    Coordinates coords = geocodingService.geocode(location.getAddress());
                    if (coords != null) {
                        enrichedMetadata.put("latitude", coords.getLatitude());
                        enrichedMetadata.put("longitude", coords.getLongitude());
                    }
                }
            }
            
            return Document.builder()
                .text(document.getText())
                .metadata(enrichedMetadata)
                .contentFormatter(document.getContentFormatter())
                .build();
                
        } catch (Exception e) {
            log.warn("Error enriching document with location metadata", e);
            return document; // Return original document on error
        }
    }
}

@Component  
public class PriceMetadataEnricher implements DocumentTransformer {
    
    private final Pattern pricePattern = Pattern.compile(
        "(\\d{1,3}(?:[,.\\s]\\d{3})*(?:[,.\\s]\\d{3})*)\\s*(?:VNĐ|VND|đồng|USD|\\$)",
        Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public List<Document> apply(List<Document> documents) {
        return documents.stream()
            .map(this::enrichWithPricing)
            .collect(Collectors.toList());
    }
    
    private Document enrichWithPricing(Document document) {
        String text = document.getText();
        Map<String, Object> enrichedMetadata = new HashMap<>(document.getMetadata());
        
        // Extract prices
        Matcher matcher = pricePattern.matcher(text);
        List<Long> prices = new ArrayList<>();
        
        while (matcher.find()) {
            try {
                String priceStr = matcher.group(1).replaceAll("[,.\\s]", "");
                long price = Long.parseLong(priceStr);
                prices.add(price);
            } catch (NumberFormatException e) {
                // Skip invalid prices
            }
        }
        
        if (!prices.isEmpty()) {
            enrichedMetadata.put("prices", prices);
            enrichedMetadata.put("min_price", prices.stream().min(Long::compareTo).orElse(0L));
            enrichedMetadata.put("max_price", prices.stream().max(Long::compareTo).orElse(0L));
            enrichedMetadata.put("avg_price", prices.stream().mapToLong(Long::longValue).average().orElse(0.0));
            
            // Categorize price range
            long minPrice = prices.stream().min(Long::compareTo).orElse(0L);
            enrichedMetadata.put("price_category", categorizePriceRange(minPrice));
        }
        
        return Document.builder()
            .text(document.getText())
            .metadata(enrichedMetadata)
            .contentFormatter(document.getContentFormatter())
            .build();
    }
    
    private String categorizePriceRange(long price) {
        if (price < 500000) {
            return "budget";
        } else if (price < 1500000) {
            return "mid-range";
        } else if (price < 3000000) {
            return "luxury";
        } else {
            return "ultra-luxury";
        }
    }
}
```

## 4. Document Processing Pipeline

### 4.1 Configurable ETL Pipeline

```java
@Component
public class ConfigurableETLPipeline {
    
    private final DocumentReaderFactory readerFactory;
    private final Map<String, DocumentTransformer> transformers;
    private final Map<String, DocumentWriter> writers;
    
    public ConfigurableETLPipeline(
            DocumentReaderFactory readerFactory,
            List<DocumentTransformer> transformerList,
            List<DocumentWriter> writerList) {
        
        this.readerFactory = readerFactory;
        this.transformers = transformerList.stream()
            .collect(Collectors.toMap(
                transformer -> transformer.getClass().getSimpleName(),
                Function.identity()
            ));
        this.writers = writerList.stream()
            .collect(Collectors.toMap(
                writer -> writer.getClass().getSimpleName(),
                Function.identity()
            ));
    }
    
    public ETLPipelineBuilder builder() {
        return new ETLPipelineBuilder(this);
    }
    
    public static class ETLPipelineBuilder {
        private final ConfigurableETLPipeline parent;
        private final List<DocumentTransformer> pipelineTransformers = new ArrayList<>();
        private DocumentWriter writer;
        
        public ETLPipelineBuilder(ConfigurableETLPipeline parent) {
            this.parent = parent;
        }
        
        public ETLPipelineBuilder addTransformer(String name) {
            DocumentTransformer transformer = parent.transformers.get(name);
            if (transformer != null) {
                pipelineTransformers.add(transformer);
            }
            return this;
        }
        
        public ETLPipelineBuilder addCustomTransformer(DocumentTransformer transformer) {
            pipelineTransformers.add(transformer);
            return this;
        }
        
        public ETLPipelineBuilder setWriter(String name) {
            this.writer = parent.writers.get(name);
            return this;
        }
        
        public ETLPipeline build() {
            return new ETLPipeline(parent.readerFactory, pipelineTransformers, writer);
        }
    }
}

public class ETLPipeline {
    
    private final DocumentReaderFactory readerFactory;
    private final List<DocumentTransformer> transformers;
    private final DocumentWriter writer;
    
    public List<Document> process(List<MultipartFile> files) {
        List<Document> allDocuments = new ArrayList<>();
        
        // 1. Read documents
        for (MultipartFile file : files) {
            try {
                List<Document> documents = readerFactory.readDocument(file);
                allDocuments.addAll(documents);
            } catch (Exception e) {
                log.error("Error reading file: {}", file.getOriginalFilename(), e);
                // Continue processing other files
            }
        }
        
        // 2. Apply transformers sequentially
        List<Document> processed = allDocuments;
        for (DocumentTransformer transformer : transformers) {
            try {
                processed = transformer.apply(processed);
            } catch (Exception e) {
                log.error("Error in transformer: {}", transformer.getClass().getSimpleName(), e);
                // Continue with previous result
            }
        }
        
        // 3. Write to destination
        if (writer != null) {
            try {
                writer.write(processed);
            } catch (Exception e) {
                log.error("Error writing documents", e);
                throw new DocumentProcessingException("Failed to write documents", e);
            }
        }
        
        return processed;
    }
}
```

### 4.2 Document Processing Service

```java
@Service
public class DocumentProcessingService {
    
    private final ConfigurableETLPipeline pipelineFactory;
    private final DocumentValidationService validationService;
    private final DocumentIndexingService indexingService;
    private final MeterRegistry meterRegistry;
    
    public DocumentProcessingResult processHotelDocuments(List<MultipartFile> files) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            // 1. Validate documents
            ValidationResult validation = validationService.validateFiles(files);
            if (!validation.isValid()) {
                throw new DocumentValidationException(validation.getErrors());
            }
            
            // 2. Create hotel-specific pipeline
            ETLPipeline pipeline = pipelineFactory.builder()
                .addTransformer("SmartTextSplitter")
                .addTransformer("MetadataEnhancementPipeline")  
                .addCustomTransformer(new HotelDocumentNormalizer())
                .addTransformer("KeywordMetadataEnricher")
                .addTransformer("SummaryMetadataEnricher")
                .setWriter("VectorStoreDocumentWriter")
                .build();
            
            // 3. Process documents
            List<Document> processedDocs = pipeline.process(files);
            
            // 4. Index for search
            DocumentIndex index = indexingService.indexDocuments(processedDocs);
            
            // 5. Record metrics
            sample.stop(Timer.builder("document.processing.duration")
                .tag("document.type", "hotel")
                .register(meterRegistry));
                
            return DocumentProcessingResult.builder()
                .processedDocuments(processedDocs.size())
                .documentIndex(index)
                .processingTime(sample.stop())
                .build();
                
        } catch (Exception e) {
            meterRegistry.counter("document.processing.errors").increment();
            throw new DocumentProcessingException("Error processing hotel documents", e);
        }
    }
    
    public DocumentProcessingResult processBookingPolicies(List<MultipartFile> files) {
        ETLPipeline pipeline = pipelineFactory.builder()
            .addTransformer("PolicyDocumentSplitter")
            .addTransformer("LegalTermsExtractor")
            .addTransformer("KeywordMetadataEnricher")
            .setWriter("VectorStoreDocumentWriter")
            .build();
            
        List<Document> processedDocs = pipeline.process(files);
        
        return DocumentProcessingResult.builder()
            .processedDocuments(processedDocs.size())
            .build();
    }
}
```

## 5. Document Validation và Quality Control

### 5.1 Document Validation Service

```java
@Service
public class DocumentValidationService {
    
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final Set<String> SUPPORTED_FORMATS = Set.of(
        "pdf", "docx", "txt", "md", "html", "json", "csv", "xlsx"
    );
    
    public ValidationResult validateFiles(List<MultipartFile> files) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        for (MultipartFile file : files) {
            validateSingleFile(file, errors, warnings);
        }
        
        return ValidationResult.builder()
            .valid(errors.isEmpty())
            .errors(errors)
            .warnings(warnings)
            .build();
    }
    
    private void validateSingleFile(MultipartFile file, List<String> errors, List<String> warnings) {
        String filename = file.getOriginalFilename();
        
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            errors.add(String.format("File %s exceeds maximum size limit (50MB)", filename));
        }
        
        // Check file format
        String extension = getFileExtension(filename);
        if (!SUPPORTED_FORMATS.contains(extension.toLowerCase())) {
            errors.add(String.format("Unsupported file format: %s", extension));
        }
        
        // Check file content
        if (file.isEmpty()) {
            errors.add(String.format("File %s is empty", filename));
        }
        
        // Content-specific validation
        try {
            validateFileContent(file, warnings);
        } catch (Exception e) {
            warnings.add(String.format("Could not fully validate content of %s: %s", filename, e.getMessage()));
        }
    }
    
    private void validateFileContent(MultipartFile file, List<String> warnings) throws IOException {
        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        
        switch (extension) {
            case "json":
                validateJsonContent(file, warnings);
                break;
            case "csv":
                validateCsvContent(file, warnings);
                break;
            case "txt":
            case "md":
                validateTextContent(file, warnings);
                break;
        }
    }
    
    private void validateJsonContent(MultipartFile file, List<String> warnings) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(file.getInputStream());
        } catch (JsonProcessingException e) {
            warnings.add(String.format("Invalid JSON format in %s", file.getOriginalFilename()));
        }
    }
    
    private void validateTextContent(MultipartFile file, List<String> warnings) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        
        if (content.trim().length() < 100) {
            warnings.add(String.format("File %s has very little content (< 100 characters)", 
                                     file.getOriginalFilename()));
        }
        
        // Check encoding
        if (content.contains("�")) {
            warnings.add(String.format("File %s may have encoding issues", 
                                     file.getOriginalFilename()));
        }
    }
}
```

### 5.2 Document Quality Scorer

```java
@Component
public class DocumentQualityScorer implements DocumentTransformer {
    
    private final ChatClient qualityEvaluator;
    
    public DocumentQualityScorer(ChatModel chatModel) {
        this.qualityEvaluator = ChatClient.builder(chatModel)
            .defaultSystem("""
                Đánh giá chất lượng văn bản cho hệ thống tìm kiếm khách sạn.
                
                Tiêu chí (1-10 điểm):
                1. Tính đầy đủ của thông tin (tên, địa chỉ, giá, tiện ích)
                2. Độ rõ ràng và dễ hiểu
                3. Tính chính xác của thông tin
                4. Độ hữu ích cho người dùng tìm kiếm
                5. Định dạng và cấu trúc
                
                Trả về JSON:
                {
                    "overall_score": 8.5,
                    "completeness": 9,
                    "clarity": 8,
                    "accuracy": 9,
                    "usefulness": 8,
                    "formatting": 7,
                    "issues": ["thiếu thông tin giá phòng"],
                    "recommendations": ["thêm thông tin về parking"]
                }
                """)
            .build();
    }
    
    @Override
    public List<Document> apply(List<Document> documents) {
        return documents.stream()
            .map(this::scoreDocument)
            .collect(Collectors.toList());
    }
    
    private Document scoreDocument(Document document) {
        try {
            String qualityAnalysis = qualityEvaluator.prompt()
                .user("Đánh giá chất lượng văn bản:\n\n" + document.getText())
                .call()
                .content();
                
            QualityScore score = parseQualityScore(qualityAnalysis);
            
            Map<String, Object> enrichedMetadata = new HashMap<>(document.getMetadata());
            enrichedMetadata.put("quality_score", score.getOverallScore());
            enrichedMetadata.put("quality_details", score);
            enrichedMetadata.put("quality_issues", score.getIssues());
            enrichedMetadata.put("quality_recommendations", score.getRecommendations());
            
            return Document.builder()
                .text(document.getText())
                .metadata(enrichedMetadata)
                .contentFormatter(document.getContentFormatter())
                .build();
                
        } catch (Exception e) {
            log.warn("Error scoring document quality", e);
            
            // Add default quality score
            Map<String, Object> enrichedMetadata = new HashMap<>(document.getMetadata());
            enrichedMetadata.put("quality_score", 5.0);
            enrichedMetadata.put("quality_error", e.getMessage());
            
            return Document.builder()
                .text(document.getText())
                .metadata(enrichedMetadata)
                .contentFormatter(document.getContentFormatter())
                .build();
        }
    }
}
```

## 6. Document Writers và Storage

### 6.1 Multi-Destination Document Writer

```java
@Component
public class MultiDestinationDocumentWriter implements DocumentWriter {
    
    private final VectorStore vectorStore;
    private final SearchIndexService searchIndex;
    private final DocumentRepository documentRepository;
    private final CacheManager cacheManager;
    
    @Override
    public void write(List<Document> documents) {
        accept(documents);
    }
    
    @Override
    public void accept(List<Document> documents) {
        try {
            // 1. Store in vector database for semantic search
            vectorStore.write(documents);
            
            // 2. Index in search engine for keyword search
            searchIndex.indexDocuments(documents);
            
            // 3. Store metadata in relational database
            List<DocumentMetadata> metadata = documents.stream()
                .map(this::extractMetadata)
                .collect(Collectors.toList());
            documentRepository.saveAll(metadata);
            
            // 4. Update cache
            updateCache(documents);
            
            log.info("Successfully stored {} documents to multiple destinations", documents.size());
            
        } catch (Exception e) {
            log.error("Error writing documents to destinations", e);
            throw new DocumentWriteException("Failed to write documents", e);
        }
    }
    
    private DocumentMetadata extractMetadata(Document document) {
        Map<String, Object> metadata = document.getMetadata();
        
        return DocumentMetadata.builder()
            .id(UUID.randomUUID().toString())
            .filename((String) metadata.get("filename"))
            .documentType((String) metadata.get("document_type"))
            .contentLength(document.getText().length())
            .keywords((List<String>) metadata.get("keywords"))
            .summary((String) metadata.get("summary"))
            .qualityScore((Double) metadata.get("quality_score"))
            .ingestionTime(Instant.now())
            .build();
    }
    
    private void updateCache(List<Document> documents) {
        Cache cache = cacheManager.getCache("documents");
        if (cache != null) {
            for (Document doc : documents) {
                String key = generateCacheKey(doc);
                cache.put(key, doc);
            }
        }
    }
    
    private String generateCacheKey(Document doc) {
        return String.format("%s_%s", 
            doc.getMetadata().get("document_type"),
            doc.getMetadata().get("filename"));
    }
}
```

## 7. Document Monitoring và Analytics

### 7.1 Processing Metrics Collector

```java
@Component
public class DocumentProcessingMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter documentsProcessed;
    private final Counter processingErrors;
    private final Timer processingTime;
    private final Gauge documentQuality;
    
    public DocumentProcessingMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        this.documentsProcessed = Counter.builder("documents.processed.total")
            .description("Total number of documents processed")
            .register(meterRegistry);
            
        this.processingErrors = Counter.builder("documents.processing.errors")
            .description("Number of document processing errors")
            .register(meterRegistry);
            
        this.processingTime = Timer.builder("documents.processing.duration")
            .description("Document processing duration")
            .register(meterRegistry);
            
        this.documentQuality = Gauge.builder("documents.average.quality")
            .description("Average quality score of processed documents")
            .register(meterRegistry, this, DocumentProcessingMetrics::calculateAverageQuality);
    }
    
    public void recordProcessing(int documentCount, Duration duration) {
        documentsProcessed.increment(documentCount);
        processingTime.record(duration);
    }
    
    public void recordError(String errorType) {
        processingErrors.increment(Tags.of("error.type", errorType));
    }
    
    public void recordQuality(double qualityScore) {
        // Store in metrics for gauge calculation
        qualityScores.add(qualityScore);
    }
    
    private final List<Double> qualityScores = new CopyOnWriteArrayList<>();
    
    private double calculateAverageQuality() {
        return qualityScores.isEmpty() ? 0.0 : 
            qualityScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}
```

## 8. Configuration

### 8.1 Document Processing Configuration

```yaml
spring:
  ai:
    document-processing:
      # File upload settings
      upload:
        max-file-size: 50MB
        max-request-size: 200MB
        supported-formats: ["pdf", "docx", "txt", "md", "html", "json", "csv", "xlsx"]
        
      # ETL Pipeline settings
      pipeline:
        default-transformers:
          - SmartTextSplitter
          - MetadataEnhancementPipeline
          - KeywordMetadataEnricher
          - SummaryMetadataEnricher
          - DocumentQualityScorer
          
        splitting:
          strategy: smart
          max-chunk-size: 800
          min-chunk-size: 200
          overlap-size: 50
          
        quality-control:
          enabled: true
          min-quality-score: 6.0
          reject-low-quality: false
          
      # Storage settings
      storage:
        vector-store:
          enabled: true
        search-index:
          enabled: true
        relational-db:
          enabled: true
        cache:
          enabled: true
          ttl: 24h
          
      # Performance settings
      performance:
        parallel-processing: true
        thread-pool-size: 10
        batch-size: 100
```

## 9. Migration Plan

### Phase 1: Reader Enhancement (Week 1-2)
- [ ] Implement DocumentReaderFactory
- [ ] Add hotel-specific readers
- [ ] Create validation service

### Phase 2: Transformer Pipeline (Week 3-4)
- [ ] Build smart text splitter
- [ ] Implement metadata enrichers
- [ ] Add quality scoring

### Phase 3: Advanced Processing (Week 5-6)
- [ ] Create configurable pipeline
- [ ] Implement multi-destination writer
- [ ] Add monitoring and metrics

### Phase 4: Integration & Testing (Week 7-8)
- [ ] End-to-end testing
- [ ] Performance optimization
- [ ] Documentation

## 10. Expected Benefits

1. **Format Support**: Handle 8+ document formats
2. **Quality**: 95% accuracy trong text extraction
3. **Performance**: 10x faster processing với parallel pipeline
4. **Intelligence**: Smart chunking và metadata extraction
5. **Scalability**: Process thousands of documents per hour
6. **Monitoring**: Real-time processing metrics
7. **Flexibility**: Configurable ETL pipelines

Hệ thống ETL mới sẽ làm cho document ingestion trở nên professional và scalable hơn đáng kể.
