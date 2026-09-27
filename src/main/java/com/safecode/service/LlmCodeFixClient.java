package com.safecode.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Real implementation of {@link CodeFixClient} that calls an OpenAI-compatible LLM
 * (e.g. OpenAI, watsonx.ai) via Spring AI.
 *
 * <p>Active on all profiles except {@code mock-ai}.
 * The prompt instructs the model to return the complete fixed file content between
 * {@code <fixed_file>} tags for unambiguous extraction.
 */
@Component
@Profile("!mock-ai")
public class LlmCodeFixClient implements CodeFixClient {

    private static final String SYSTEM_PROMPT =
            "You are a security-focused code assistant. "
            + "When given a file and a specific security finding, you apply the minimal "
            + "change needed to resolve the finding. "
            + "Return ONLY the complete fixed file content wrapped exactly like this:\n"
            + "<fixed_file>\n"
            + "...complete fixed file content here...\n"
            + "</fixed_file>\n"
            + "No explanations, no markdown fences, no other text outside the tags.";

    private final ChatClient chatClient;

    public LlmCodeFixClient(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    @Override
    public String requestFix(String filePath, String fileContent,
                             String issue, String recommendedFix) {
        String userMessage = String.format(
                "File: %s%n%n"
                + "Security finding:%n"
                + "  Issue: %s%n"
                + "  Recommended fix: %s%n%n"
                + "Current file content:%n%s",
                filePath, issue, recommendedFix, fileContent);

        String response = chatClient.prompt()
                .user(userMessage)
                .call()
                .content();

        return extractFixedFile(response);
    }

    // -----------------------------------------------------------------------
    // Tag extraction
    // -----------------------------------------------------------------------

    private static String extractFixedFile(String response) {
        int start = response.indexOf("<fixed_file>");
        int end   = response.indexOf("</fixed_file>");
        if (start == -1 || end == -1 || end <= start) {
            throw new IllegalStateException(
                    "LLM response did not contain expected <fixed_file> tags. Raw response: "
                    + response.substring(0, Math.min(response.length(), 300)));
        }
        String content = response.substring(start + "<fixed_file>".length(), end);
        if (content.startsWith("\n")) {
            content = content.substring(1);
        }
        return content;
    }
}
