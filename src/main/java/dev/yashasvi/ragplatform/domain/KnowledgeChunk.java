package dev.yashasvi.ragplatform.domain;

import java.util.UUID;

public record KnowledgeChunk(
        UUID documentId,
        String title,
        String source,
        int chunkIndex,
        String content,
        float[] embedding) {
}
