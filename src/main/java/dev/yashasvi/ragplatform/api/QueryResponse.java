package dev.yashasvi.ragplatform.api;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public record QueryResponse(
        String answer,
        List<SourceSnippet> sources,
        boolean cached,
        String conversationId,
        Instant answeredAt) implements Serializable {

    public QueryResponse asCached() {
        return new QueryResponse(answer, sources, true, conversationId, answeredAt);
    }
}
