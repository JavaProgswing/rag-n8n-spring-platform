package dev.yashasvi.ragplatform.service;

import dev.yashasvi.ragplatform.config.RagProperties;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class HashingEmbeddingService implements EmbeddingService {
    private final int dimensions;

    public HashingEmbeddingService(RagProperties properties) {
        this.dimensions = properties.embeddingDimensions();
    }

    @Override
    public float[] embed(String text) {
        float[] vector = new float[dimensions];
        String[] tokens = text.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}]+");
        for (String token : tokens) {
            if (token.length() < 2) {
                continue;
            }
            int hash = token.hashCode();
            int index = Math.floorMod(hash, dimensions);
            vector[index] += ((hash >>> 8) & 1) == 0 ? 1.0f : -1.0f;
        }
        normalize(vector);
        return vector;
    }

    private void normalize(float[] vector) {
        double magnitude = 0;
        for (float value : vector) {
            magnitude += value * value;
        }
        if (magnitude == 0) {
            return;
        }
        double scale = Math.sqrt(magnitude);
        for (int i = 0; i < vector.length; i++) {
            vector[i] = (float) (vector[i] / scale);
        }
    }
}
