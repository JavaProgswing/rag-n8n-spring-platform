package dev.yashasvi.ragplatform.service;

import dev.yashasvi.ragplatform.config.RagProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class TextChunker {
    private final int chunkSize;
    private final int overlap;

    public TextChunker(RagProperties properties) {
        this.chunkSize = properties.chunkSize();
        this.overlap = properties.chunkOverlap();
        if (chunkSize <= 0 || overlap < 0 || overlap >= chunkSize) {
            throw new IllegalArgumentException("Chunk size must be positive and overlap smaller than chunk size");
        }
    }

    public List<String> split(String text) {
        String normalized = text.replaceAll("\\s+", " ").trim();
        if (normalized.isEmpty()) {
            return List.of();
        }

        List<String> words = Arrays.asList(normalized.split(" "));
        List<String> chunks = new ArrayList<>();
        int step = chunkSize - overlap;
        for (int start = 0; start < words.size(); start += step) {
            int end = Math.min(start + chunkSize, words.size());
            chunks.add(String.join(" ", words.subList(start, end)));
            if (end == words.size()) {
                break;
            }
        }
        return chunks;
    }
}
