package dev.yashasvi.ragplatform.repository;

import dev.yashasvi.ragplatform.api.SourceSnippet;
import dev.yashasvi.ragplatform.domain.KnowledgeChunk;

import java.util.List;
import java.util.UUID;

public interface VectorStore {
    void saveDocument(UUID documentId, String title, String source, String content, List<KnowledgeChunk> chunks);
    List<SourceSnippet> search(float[] queryEmbedding, int limit);
    long documentCount();
}
