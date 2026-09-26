package dev.yashasvi.ragplatform.cache;

import dev.yashasvi.ragplatform.api.QueryResponse;

import java.time.Duration;
import java.util.Optional;

public interface QueryCache {
    Optional<QueryResponse> get(String key);
    void put(String key, QueryResponse response, Duration ttl);
}
