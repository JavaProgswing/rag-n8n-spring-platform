package dev.yashasvi.ragplatform.service;

import dev.yashasvi.ragplatform.api.SourceSnippet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
@ConditionalOnProperty(name = "app.answer-provider", havingValue = "extractive", matchIfMissing = true)
public class ExtractiveAnswerGenerator implements AnswerGenerator {
    @Override
    public String answer(String question, List<SourceSnippet> sources) {
        if (sources.isEmpty()) {
            return "I could not find relevant information in the knowledge base. Add a document and try again.";
        }

        String evidence = IntStream.range(0, Math.min(3, sources.size()))
                .mapToObj(index -> {
                    SourceSnippet source = sources.get(index);
                    return "[%d] %s".formatted(index + 1, source.content());
                })
                .reduce((first, second) -> first + "\n\n" + second)
                .orElse("");

        return "Here is the most relevant information for \"%s\":\n\n%s"
                .formatted(question, evidence);
    }
}
