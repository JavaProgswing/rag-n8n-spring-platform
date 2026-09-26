package dev.yashasvi.ragplatform.api;

import dev.yashasvi.ragplatform.config.RagProperties;
import dev.yashasvi.ragplatform.service.RagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class RagController {
    private final RagService ragService;
    private final RagProperties properties;

    public RagController(RagService ragService, RagProperties properties) {
        this.ragService = ragService;
        this.properties = properties;
    }

    @PostMapping("/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentIngestResponse ingest(@Valid @RequestBody DocumentIngestRequest request) {
        return ragService.ingest(request);
    }

    @PostMapping("/query")
    public QueryResponse query(@Valid @RequestBody QueryRequest request) {
        return ragService.query(request);
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "status", "ready",
                "documents", ragService.documentCount(),
                "vectorStore", properties.vectorStore(),
                "cache", properties.cache(),
                "audit", properties.audit(),
                "answerProvider", properties.answerProvider(),
                "timestamp", Instant.now().toString());
    }
}
