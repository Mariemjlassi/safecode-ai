package com.safecode.service;

/**
 * Contract for applying an AI-generated minimal fix to a source file.
 * Two implementations exist:
 * <ul>
 *   <li>{@link LlmCodeFixClient} — calls a real LLM via Spring AI (default profile)</li>
 *   <li>{@link MockCodeFixClient} — regex-based, no LLM needed (profile {@code mock-ai})</li>
 * </ul>
 */
public interface CodeFixClient {

    /**
     * Returns the complete patched content of {@code fileContent} with the described
     * security issue resolved using a minimal change.
     *
     * @param filePath       relative path of the file (context only)
     * @param fileContent    full current content of the file
     * @param issue          issue description from the finding
     * @param recommendedFix recommended fix from the finding
     * @return complete patched file content
     */
    String requestFix(String filePath, String fileContent,
                      String issue, String recommendedFix);
}
