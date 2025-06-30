package huy.project.ai_service.core.service;

import huy.project.ai_service.core.domain.dto.request.DocumentUploadRequest;
import huy.project.ai_service.core.domain.model.DocumentModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentIngestionService implements IDocumentIngestionService {
    private final VectorStore vectorStore;
    private final TokenTextSplitter tokenSplitter;

    private final Map<String, DocumentModel> documentStorage = new ConcurrentHashMap<>();

    private final ExecutorService executorService;

    @Override
    public DocumentModel uploadDocument(DocumentUploadRequest request) {
        try {
            MultipartFile file = request.getFile();
            String documentId = UUID.randomUUID().toString();

            DocumentModel document = DocumentModel.builder()
                    .id(documentId)
                    .fileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .uploadedAt(Instant.now())
                    .status("PROCESSING")
                    .metadata(request.getMetadata())
                    .build();

            documentStorage.put(documentId, document);

            log.info("Uploading document: {} ({})", document.getFileName(), document.getId());

            processDocumentAsync(documentId, file);

            return document;
        } catch (Exception e) {
            log.error("Failed to upload document", e);
            throw new RuntimeException("Document upload failed", e);
        }
    }

    private void processDocumentAsync(String documentId, MultipartFile file) {
        executorService.submit(() -> {
            try {
                String content = new String(file.getBytes());
                DocumentModel document = documentStorage.get(documentId);
                document.setContent(content);

                processDocument(documentId);
            } catch (Exception e) {
                log.error("Error processing document: {}", documentId, e);
                DocumentModel document = documentStorage.get(documentId);
                document.setStatus("FAILED");
            }
        });
    }

    private List<Document> processDocument(String documentId) {
        DocumentModel document = documentStorage.get(documentId);
        if (document == null) {
            throw new RuntimeException("Document not found {}");
        }

        try {
            List<Document> documents = readDocument(document);

            List<Document> chunks = splitDocuments(documents);

            addMetadataToChunks(chunks, document);

            vectorStore.add(chunks);

            document.setStatus("COMPLETED");

            log.info("Document processed successfully: {}, ({} chunks)", documentId, chunks.size());

            return chunks;
        } catch (Exception e) {
            log.error("Error processing document: {}", documentId, e);
            throw new RuntimeException("Document processing failed: ", e);
        }

    }

    private void addMetadataToChunks(List<Document> chunks, DocumentModel document) {
        for (Document chunk : chunks) {
            Map<String, Object> metadata = chunk.getMetadata();
            metadata.put("document_id", document.getId());
            metadata.put("filename", document.getFileName());
            metadata.put("uploaded_at", document.getUploadedAt().toString());
            metadata.put("content_type", document.getContentType());
        }
    }

    private List<Document> splitDocuments(List<Document> documents) {
        return tokenSplitter.apply(documents);
    }

    private List<Document> readDocument(DocumentModel document) {
        String contentType = document.getContentType();

        ByteArrayResource resource = new ByteArrayResource(document.getContent().getBytes());

        if (contentType != null && contentType.equals("application/pdf")) {
            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(
                    resource,
                    PdfDocumentReaderConfig.builder()
                            .withPageTopMargin(0)
                            .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                    .withNumberOfTopTextLinesToDelete(0)
                                    .build())
                            .build()
            );

            return pdfReader.get();
        } else {
            TikaDocumentReader tikaReader = new TikaDocumentReader(resource);
            return tikaReader.get();
        }
    }

    @Override
    public List<DocumentModel> getAllDocuments() {
        return new ArrayList<>(documentStorage.values());
    }

    @Override
    public void deleteDocument(String documentId) {
        DocumentModel document = documentStorage.get(documentId);
        if (document == null) {
            throw new RuntimeException("Document not found " + documentId);
        }

        try {
            FilterExpressionBuilder filterBuilder = new FilterExpressionBuilder();
            Filter.Expression filterExpression = filterBuilder
                    .eq("document_id", documentId)
                    .build();

            SearchRequest searchRequest = SearchRequest.builder()
                    .query("")
                    .filterExpression(filterExpression)
                    .topK(1000)
                    .build();

            List<Document> documentsToDelete = vectorStore.similaritySearch(searchRequest);
            if (!CollectionUtils.isEmpty(documentsToDelete)) {
                List<String> idsToDelete = documentsToDelete.stream()
                        .map(Document::getId)
                        .toList();
                vectorStore.delete(idsToDelete);
            }

            documentStorage.remove(documentId);

            assert documentsToDelete != null;
            log.info("Document deleted successfully: {} ({}) - Removed {} vectors",
                    document.getFileName(), documentId, documentsToDelete.size());
        } catch (Exception e) {
            log.error("Error deleting document: {}, ({})", document.getFileName(), documentId, e);
            throw new RuntimeException("Document deletion failed: " + documentId, e);
        }
    }

    @Override
    public List<DocumentModel> searchDocuments(String query) {
        return documentStorage.values().stream()
                .filter(doc -> doc.getFileName().toLowerCase().contains(query.toLowerCase())
                        || (doc.getContent() != null && doc.getContent().toLowerCase().contains(query.toLowerCase())))
                .toList();
    }
}
