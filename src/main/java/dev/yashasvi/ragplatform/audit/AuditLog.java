package dev.yashasvi.ragplatform.audit;

import java.util.Map;

public interface AuditLog {
    void record(String eventType, String conversationId, Map<String, Object> details);
}
