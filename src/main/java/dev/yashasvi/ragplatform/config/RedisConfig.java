package dev.yashasvi.ragplatform.config;

import dev.yashasvi.ragplatform.api.QueryResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConditionalOnProperty(name = "app.cache", havingValue = "redis")
public class RedisConfig {
    @Bean
    RedisTemplate<String, QueryResponse> queryResponseRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, QueryResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
