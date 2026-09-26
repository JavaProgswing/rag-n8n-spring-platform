package dev.yashasvi.ragplatform.audit;

import org.bson.Document;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "app.audit", havingValue = "mongodb")
public class MongoAuditLog implements AuditLog {
    private final MongoTemplate mongoTemplate;

    public MongoAuditLog(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void record(String eventType, String conversationId, Map<String, Object> details) {
        Document event = new Document()
                .append("eventType", eventType)
                .append("conversationId", conversationId)
                .append("details", details)
                .append("createdAt", Instant.now());
        mongoTemplate.insert(event, "rag_audit_events");
    }
}
