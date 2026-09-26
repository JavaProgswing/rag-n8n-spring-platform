package dev.yashasvi.ragplatform.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "app.audit", havingValue = "memory", matchIfMissing = true)
public class LoggingAuditLog implements AuditLog {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAuditLog.class);

    @Override
    public void record(String eventType, String conversationId, Map<String, Object> details) {
        LOGGER.info("RAG audit event={} conversationId={} details={}", eventType, conversationId, details);
    }
}
