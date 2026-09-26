package dev.yashasvi.ragplatform.service;

import dev.yashasvi.ragplatform.api.DocumentIngestRequest;
import dev.yashasvi.ragplatform.api.QueryRequest;
import dev.yashasvi.ragplatform.audit.LoggingAuditLog;
import dev.yashasvi.ragplatform.cache.MemoryQueryCache;
import dev.yashasvi.ragplatform.config.RagProperties;
import dev.yashasvi.ragplatform.repository.MemoryVectorStore;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RagServiceTest {
    private final RagProperties properties = new RagProperties(
            "memory", "memory", "memory", "extractive", 384, 30, 5,
            new RagProperties.Ollama("http://localhost:11434", "test"));
    private final RagService ragService = new RagService(
            new TextChunker(properties),
            new HashingEmbeddingService(properties),
            new MemoryVectorStore(),
            new MemoryQueryCache(),
            new ExtractiveAnswerGenerator(),
            new LoggingAuditLog());

    @Test
    void ingestsRetrievesAndCachesGroundedAnswers() {
        var ingestion = ragService.ingest(new DocumentIngestRequest(
                "Incident handbook",
                "runbook",
                "When Redis is unavailable, restart the Redis service and verify connectivity before retrying the request."));

        var first = ragService.query(new QueryRequest("How should Redis be recovered?", 2, "incident-42"));
        var second = ragService.query(new QueryRequest("How should Redis be recovered?", 2, "incident-42"));

        assertThat(ingestion.chunksCreated()).isPositive();
        assertThat(first.cached()).isFalse();
        assertThat(first.sources()).isNotEmpty();
        assertThat(first.sources().getFirst().title()).isEqualTo("Incident handbook");
        assertThat(first.answer()).contains("restart the Redis service");
        assertThat(second.cached()).isTrue();
    }
}
