package dev.yashasvi.ragplatform.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QueryRequest(
        @NotBlank @Size(max = 2_000) String question,
        @Min(1) @Max(10) Integer topK,
        @Size(max = 100) String conversationId) {

    public int resolvedTopK() {
        return topK == null ? 4 : topK;
    }
}
