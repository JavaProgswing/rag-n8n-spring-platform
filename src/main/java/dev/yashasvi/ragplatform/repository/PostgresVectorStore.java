package dev.yashasvi.ragplatform.repository;

import dev.yashasvi.ragplatform.api.SourceSnippet;
import dev.yashasvi.ragplatform.domain.KnowledgeChunk;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "app.vector-store", havingValue = "postgres")
public class PostgresVectorStore implements VectorStore {
    private final JdbcTemplate jdbcTemplate;

    public PostgresVectorStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void saveDocument(UUID documentId, String title, String source, String content, List<KnowledgeChunk> chunks) {
        jdbcTemplate.update(
                "INSERT INTO documents (id, title, source, content) VALUES (?, ?, ?, ?)",
                documentId, title, source, content);

        for (KnowledgeChunk chunk : chunks) {
            jdbcTemplate.update(
                    """
                    INSERT INTO knowledge_chunks
                        (document_id, chunk_index, content, embedding)
                    VALUES (?, ?, ?, CAST(? AS vector))
                    """,
                    chunk.documentId(), chunk.chunkIndex(), chunk.content(), toVectorLiteral(chunk.embedding()));
        }
    }

    @Override
    public List<SourceSnippet> search(float[] queryEmbedding, int limit) {
        String vector = toVectorLiteral(queryEmbedding);
        return jdbcTemplate.query(
                """
                SELECT d.id, d.title, d.source, c.chunk_index, c.content,
                       1 - (c.embedding <=> CAST(? AS vector)) AS score
                FROM knowledge_chunks c
                JOIN documents d ON d.id = c.document_id
                ORDER BY c.embedding <=> CAST(? AS vector)
                LIMIT ?
                """,
                (resultSet, rowNumber) -> new SourceSnippet(
                        resultSet.getObject("id", UUID.class),
                        resultSet.getString("title"),
                        resultSet.getString("source"),
                        resultSet.getInt("chunk_index"),
                        resultSet.getString("content"),
                        resultSet.getDouble("score")),
                vector, vector, limit);
    }

    @Override
    public long documentCount() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM documents", Long.class);
        return count == null ? 0 : count;
    }

    private String toVectorLiteral(float[] embedding) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(embedding[i]);
        }
        return builder.append(']').toString();
    }
}
