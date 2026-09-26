package dev.yashasvi.ragplatform.api;

import java.io.Serializable;
import java.util.UUID;

public record SourceSnippet(
        UUID documentId,
        String title,
        String source,
        int chunkIndex,
        String content,
        double score) implements Serializable {
}
