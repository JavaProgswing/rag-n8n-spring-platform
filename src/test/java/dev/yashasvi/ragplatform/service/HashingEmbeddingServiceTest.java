package dev.yashasvi.ragplatform.service;

import dev.yashasvi.ragplatform.config.RagProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashingEmbeddingServiceTest {
    private final HashingEmbeddingService service = new HashingEmbeddingService(new RagProperties(
            "memory", "memory", "memory", "extractive", 384, 120, 20,
            new RagProperties.Ollama("http://localhost:11434", "test")));

    @Test
    void createsDeterministicNormalizedEmbeddings() {
        float[] first = service.embed("Spring Boot connects the RAG service");
        float[] second = service.embed("Spring Boot connects the RAG service");

        assertThat(first).containsExactly(second);
        assertThat(first).hasSize(384);
        double magnitude = Math.sqrt(
                IntStreamSupport.sumOfSquares(first));
        assertThat(magnitude).isCloseTo(1.0, org.assertj.core.data.Offset.offset(0.0001));
    }

    private static final class IntStreamSupport {
        private static double sumOfSquares(float[] values) {
            double sum = 0;
            for (float value : values) {
                sum += value * value;
            }
            return sum;
        }
    }
}
