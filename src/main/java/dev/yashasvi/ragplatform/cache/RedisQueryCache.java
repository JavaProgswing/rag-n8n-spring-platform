package dev.yashasvi.ragplatform.cache;

import dev.yashasvi.ragplatform.api.QueryResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "app.cache", havingValue = "redis")
public class RedisQueryCache implements QueryCache {
    private final RedisTemplate<String, QueryResponse> redisTemplate;

    public RedisQueryCache(RedisTemplate<String, QueryResponse> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<QueryResponse> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public void put(String key, QueryResponse response, Duration ttl) {
        redisTemplate.opsForValue().set(key, response, ttl);
    }
}
