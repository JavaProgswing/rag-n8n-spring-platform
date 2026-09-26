package dev.yashasvi.ragplatform.api;

import java.time.Instant;
import java.util.UUID;

public record DocumentIngestResponse(UUID documentId, int chunksCreated, Instant ingestedAt) {
}
