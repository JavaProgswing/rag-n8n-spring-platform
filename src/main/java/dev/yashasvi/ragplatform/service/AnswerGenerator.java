package dev.yashasvi.ragplatform.service;

import dev.yashasvi.ragplatform.api.SourceSnippet;

import java.util.List;

public interface AnswerGenerator {
    String answer(String question, List<SourceSnippet> sources);
}
