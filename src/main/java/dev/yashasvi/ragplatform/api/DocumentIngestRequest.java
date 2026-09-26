package dev.yashasvi.ragplatform.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DocumentIngestRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 500) String source,
        @NotBlank @Size(max = 250_000) String content) {
}
