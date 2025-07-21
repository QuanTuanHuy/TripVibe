package huy.project.ai_service.core.service;

import huy.project.ai_service.core.domain.constant.DocumentStatus;
import huy.project.ai_service.core.domain.constant.ErrorCode;
import huy.project.ai_service.core.domain.dto.request.DocumentUploadRequest;
import huy.project.ai_service.core.domain.model.DocumentModel;
import huy.project.ai_service.core.exception.AppException;
import huy.project.ai_service.core.port.IDocumentRepoPort;
import huy.project.ai_service.core.port.IFileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentIngestionService implements IDocumentIngestionService {
    private final VectorStore vectorStore;
    private final TokenTextSplitter tokenSplitter;

    private final IFileStoragePort fileStorage;
    private final IDocumentRepoPort documentRepoPort;

    private final ExecutorService executorService;

    @Override
    public DocumentModel uploadDocument(DocumentUploadRequest request) {
        if (request.getFile() == null || request.getFile().isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        try {
            MultipartFile file = request.getFile();

            // 1. Store file in storage
            String filePath = fileStorage.storeFile(file);

            // 2. Save metadata in the database
            DocumentModel document = DocumentModel.builder()
                    .fileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .uploadedAt(Instant.now())
                    .status(DocumentStatus.PROCESSING)
                    .filePath(filePath)
                    .build();
            document = documentRepoPort.save(document);

            log.info("Uploading document: {} ({})", document.getFileName(), document.getId());

            // 3. Process asynchronously
            processDocumentAsync(document.getId());

            return document;
        } catch (Exception e) {
            log.error("Failed to upload document", e);
            throw new RuntimeException("Document upload failed", e);
        }
    }

    private void processDocumentAsync(Long documentId) {
        executorService.submit(() -> {
            try {
                processDocument(documentId);
            } catch (Exception e) {
                log.error("Error processing document: {}", documentId, e);
                documentRepoPort.getDocumentById(documentId).ifPresent(document -> {
                    document.setStatus(DocumentStatus.FAILED);
                    documentRepoPort.save(document);
                });
            }
        });
    }

    private void processDocument(Long documentId) {
        var document = documentRepoPort.getDocumentById(documentId).orElse(null);
        if (document == null) {
            throw new AppException(ErrorCode.DOCUMENT_NOT_FOUND);
        }

        List<Document> documents = readDocument(document);
        if (documents == null || documents.isEmpty()) {
            log.warn("No content extracted from document: {}", documentId);
            return;
        }

        List<Document> chunks = splitDocuments(documents);
        if (chunks == null || chunks.isEmpty()) {
            log.warn("No chunks created from document: {}", documentId);
            return;
        }

        addMetadataToChunks(chunks, document);
        vectorStore.add(chunks);

        document.setStatus(DocumentStatus.COMPLETED);
        documentRepoPort.save(document);

        log.info("Document processed successfully: {}, ({} chunks)", documentId, chunks.size());
    }

    private void addMetadataToChunks(List<Document> chunks, DocumentModel document) {
        for (Document chunk : chunks) {
            Map<String, Object> metadata = chunk.getMetadata();
            metadata.put("document_id", document.getId().toString());
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
        byte[] fileBytes = fileStorage.loadFile(document.getFilePath());

        ByteArrayResource resource = new ByteArrayResource(fileBytes);

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
        return documentRepoPort.getAllDocuments();
    }

    @Override
    public void deleteDocument(Long documentId) {
        DocumentModel document = documentRepoPort.getDocumentById(documentId).orElse(null);
        if (document == null) {
            throw new AppException(ErrorCode.DOCUMENT_NOT_FOUND);
        }

        try {
            FilterExpressionBuilder filterBuilder = new FilterExpressionBuilder();
            Filter.Expression filterExpression = filterBuilder
                    .eq("document_id", documentId)
                    .build();

            vectorStore.delete(filterExpression);
            fileStorage.deleteFile(document.getFilePath());
            documentRepoPort.deleteDocumentById(documentId);

        } catch (Exception e) {
            log.error("Error deleting document: {}, ({})", document.getFileName(), documentId, e);
            throw new RuntimeException("Document deletion failed: " + documentId, e);
        }
    }

    @Override
    public List<DocumentModel> searchDocuments(String query) {
        return getAllDocuments().stream()
                .filter(doc -> doc.getFileName().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }
}
