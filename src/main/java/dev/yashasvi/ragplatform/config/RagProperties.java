package dev.yashasvi.ragplatform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record RagProperties(
        String vectorStore,
        String cache,
        String audit,
        String answerProvider,
        int embeddingDimensions,
        int chunkSize,
        int chunkOverlap,
        Ollama ollama) {

    public record Ollama(String baseUrl, String chatModel) {}
}
