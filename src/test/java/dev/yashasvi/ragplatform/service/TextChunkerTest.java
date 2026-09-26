package dev.yashasvi.ragplatform.service;

import dev.yashasvi.ragplatform.config.RagProperties;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class TextChunkerTest {
    @Test
    void splitsWithConfiguredOverlap() {
        RagProperties properties = properties(5, 2);
        TextChunker chunker = new TextChunker(properties);
        String text = String.join(" ", IntStream.rangeClosed(1, 11).mapToObj(i -> "word" + i).toList());

        var chunks = chunker.split(text);

        assertThat(chunks).containsExactly(
                "word1 word2 word3 word4 word5",
                "word4 word5 word6 word7 word8",
                "word7 word8 word9 word10 word11");
    }

    private RagProperties properties(int chunkSize, int overlap) {
        return new RagProperties(
                "memory", "memory", "memory", "extractive", 32, chunkSize, overlap,
                new RagProperties.Ollama("http://localhost:11434", "test"));
    }
}
