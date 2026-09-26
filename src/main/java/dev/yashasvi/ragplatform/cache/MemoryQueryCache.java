package dev.yashasvi.ragplatform.cache;

import dev.yashasvi.ragplatform.api.QueryResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(name = "app.cache", havingValue = "memory", matchIfMissing = true)
public class MemoryQueryCache implements QueryCache {
    private final ConcurrentHashMap<String, Entry> values = new ConcurrentHashMap<>();

    @Override
    public Optional<QueryResponse> get(String key) {
        Entry entry = values.get(key);
        if (entry == null) {
            return Optional.empty();
        }
        if (entry.expiresAt().isBefore(Instant.now())) {
            values.remove(key);
            return Optional.empty();
        }
        return Optional.of(entry.response());
    }

    @Override
    public void put(String key, QueryResponse response, Duration ttl) {
        values.put(key, new Entry(response, Instant.now().plus(ttl)));
    }

    private record Entry(QueryResponse response, Instant expiresAt) {}
}
