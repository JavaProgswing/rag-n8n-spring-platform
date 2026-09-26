package dev.yashasvi.ragplatform.service;

import dev.yashasvi.ragplatform.api.SourceSnippet;
import dev.yashasvi.ragplatform.config.RagProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Component
@ConditionalOnProperty(name = "app.answer-provider", havingValue = "ollama")
public class OllamaAnswerGenerator implements AnswerGenerator {
    private final RestClient restClient;
    private final String model;

    public OllamaAnswerGenerator(RagProperties properties) {
        this.restClient = RestClient.builder().baseUrl(properties.ollama().baseUrl()).build();
        this.model = properties.ollama().chatModel();
    }

    @Override
    public String answer(String question, List<SourceSnippet> sources) {
        if (sources.isEmpty()) {
            return "I could not find relevant information in the knowledge base.";
        }

        String context = IntStream.range(0, sources.size())
                .mapToObj(index -> "[%d] %s".formatted(index + 1, sources.get(index).content()))
                .reduce((first, second) -> first + "\n\n" + second)
                .orElse("");
        String prompt = """
                Answer the question only from the supplied context.
                Cite supporting passages with [1], [2], and so on.
                If the context does not contain the answer, say so.

                Question: %s

                Context:
                %s
                """.formatted(question, context);

        OllamaResponse response = restClient.post()
                .uri("/api/chat")
                .body(Map.of(
                        "model", model,
                        "stream", false,
                        "messages", List.of(Map.of("role", "user", "content", prompt))))
                .retrieve()
                .body(OllamaResponse.class);

        if (response == null || response.message() == null || response.message().content() == null) {
            throw new IllegalStateException("Ollama returned an empty response");
        }
        return response.message().content();
    }

    public record OllamaResponse(OllamaMessage message) {}
    public record OllamaMessage(String role, String content) {}
}
