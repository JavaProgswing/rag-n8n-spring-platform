package dev.yashasvi.ragplatform.service;

import dev.yashasvi.ragplatform.api.DocumentIngestRequest;
import dev.yashasvi.ragplatform.api.DocumentIngestResponse;
import dev.yashasvi.ragplatform.api.QueryRequest;
import dev.yashasvi.ragplatform.api.QueryResponse;
import dev.yashasvi.ragplatform.api.SourceSnippet;
import dev.yashasvi.ragplatform.audit.AuditLog;
import dev.yashasvi.ragplatform.cache.QueryCache;
import dev.yashasvi.ragplatform.domain.KnowledgeChunk;
import dev.yashasvi.ragplatform.repository.VectorStore;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
public class RagService {
    private static final Duration QUERY_CACHE_TTL = Duration.ofMinutes(15);

    private final TextChunker chunker;
    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;
    private final QueryCache queryCache;
    private final AnswerGenerator answerGenerator;
    private final AuditLog auditLog;

    public RagService(
            TextChunker chunker,
            EmbeddingService embeddingService,
            VectorStore vectorStore,
            QueryCache queryCache,
            AnswerGenerator answerGenerator,
            AuditLog auditLog) {
        this.chunker = chunker;
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
        this.queryCache = queryCache;
        this.answerGenerator = answerGenerator;
        this.auditLog = auditLog;
    }

    public DocumentIngestResponse ingest(DocumentIngestRequest request) {
        UUID documentId = UUID.randomUUID();
        List<String> texts = chunker.split(request.content());
        List<KnowledgeChunk> chunks = IntStream.range(0, texts.size())
                .mapToObj(index -> new KnowledgeChunk(
                        documentId,
                        request.title(),
                        request.source(),
                        index,
                        texts.get(index),
                        embeddingService.embed(texts.get(index))))
                .toList();

        vectorStore.saveDocument(documentId, request.title(), request.source(), request.content(), chunks);
        Instant ingestedAt = Instant.now();
        auditLog.record("DOCUMENT_INGESTED", documentId.toString(), Map.of(
                "title", request.title(),
                "source", request.source(),
                "chunksCreated", chunks.size()));
        return new DocumentIngestResponse(documentId, chunks.size(), ingestedAt);
    }

    public QueryResponse query(QueryRequest request) {
        String conversationId = request.conversationId() == null || request.conversationId().isBlank()
                ? UUID.randomUUID().toString()
                : request.conversationId();
        String cacheKey = cacheKey(request.question(), request.resolvedTopK(), vectorStore.documentCount());
        var cached = queryCache.get(cacheKey);
        if (cached.isPresent()) {
            return cached.get().asCached();
        }

        float[] queryEmbedding = embeddingService.embed(request.question());
        List<SourceSnippet> sources = vectorStore.search(queryEmbedding, request.resolvedTopK());
        String answer = answerGenerator.answer(request.question(), sources);
        QueryResponse response = new QueryResponse(answer, sources, false, conversationId, Instant.now());
        queryCache.put(cacheKey, response, QUERY_CACHE_TTL);
        auditLog.record("QUESTION_ANSWERED", conversationId, Map.of(
                "question", request.question(),
                "sourcesReturned", sources.size(),
                "topK", request.resolvedTopK()));
        return response;
    }

    public long documentCount() {
        return vectorStore.documentCount();
    }

    private String cacheKey(String question, int topK, long documentVersion) {
        try {
            String value = question.strip().toLowerCase() + "|" + topK + "|" + documentVersion;
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return "rag:query:" + HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
