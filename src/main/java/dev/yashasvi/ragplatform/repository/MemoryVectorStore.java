package dev.yashasvi.ragplatform.repository;

import dev.yashasvi.ragplatform.api.SourceSnippet;
import dev.yashasvi.ragplatform.domain.KnowledgeChunk;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@ConditionalOnProperty(name = "app.vector-store", havingValue = "memory", matchIfMissing = true)
public class MemoryVectorStore implements VectorStore {
    private final List<KnowledgeChunk> chunks = new CopyOnWriteArrayList<>();
    private final Set<UUID> documents = ConcurrentHashMap.newKeySet();

    @Override
    public void saveDocument(UUID documentId, String title, String source, String content, List<KnowledgeChunk> newChunks) {
        documents.add(documentId);
        chunks.addAll(newChunks);
    }

    @Override
    public List<SourceSnippet> search(float[] queryEmbedding, int limit) {
        return chunks.stream()
                .map(chunk -> new SourceSnippet(
                        chunk.documentId(),
                        chunk.title(),
                        chunk.source(),
                        chunk.chunkIndex(),
                        chunk.content(),
                        cosineSimilarity(queryEmbedding, chunk.embedding())))
                .sorted(Comparator.comparingDouble(SourceSnippet::score).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public long documentCount() {
        return documents.size();
    }

    private double cosineSimilarity(float[] first, float[] second) {
        double dot = 0;
        double firstMagnitude = 0;
        double secondMagnitude = 0;
        for (int i = 0; i < first.length; i++) {
            dot += first[i] * second[i];
            firstMagnitude += first[i] * first[i];
            secondMagnitude += second[i] * second[i];
        }
        if (firstMagnitude == 0 || secondMagnitude == 0) {
            return 0;
        }
        return dot / (Math.sqrt(firstMagnitude) * Math.sqrt(secondMagnitude));
    }
}
