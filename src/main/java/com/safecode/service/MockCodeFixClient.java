package com.safecode.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Mock implementation of {@link CodeFixClient} active under the {@code mock-ai} Spring profile.
 * Used for local testing without a real LLM API key.
 *
 * <p>Strategy: applies the same regex patterns as the audit scanner and replaces each
 * offending construct with a safe equivalent, so the re-scan in
 * {@code AuditService.applyFix()} always passes after the mock fix.
 */
@Component
@Profile("mock-ai")
public class MockCodeFixClient implements CodeFixClient {

    // Mirrors AuditService patterns exactly
    private static final Pattern HARDCODED_SECRET = Pattern.compile(
            "(?i)(?<!\\?)(?<!&)(password|passwd|pwd|secret|api_key|apikey|private_key|access_key)" +
            "\\s*=\\s*[\"'][^\"']{3,}",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern PRIVATE_KEY_BLOCK = Pattern.compile(
            "-----BEGIN (RSA |EC |DSA |OPENSSH )?PRIVATE KEY-----");

    private static final Pattern WEAK_HASH = Pattern.compile(
            "(?i)(MessageDigest\\.getInstance\\s*\\(\\s*[\"']MD5[\"']\\s*\\)" +
            "|MessageDigest\\.getInstance\\s*\\(\\s*[\"']SHA-1[\"']\\s*\\)" +
            "|hashlib\\.md5\\s*\\(" +
            "|new\\s+MD5\\s*\\(" +
            "|DigestUtils\\.md5" +
            "|String\\s+\\w*[Mm][Dd]5\\w*\\s*=)");

    private static final Pattern WEAK_CIPHER = Pattern.compile(
            "(?i)(AES.*ECB|Cipher\\.getInstance\\(\"AES\"\\))");

    /**
     * Applies deterministic regex-based substitutions instead of calling the LLM.
     * The result always passes the Category D re-scan, exercising the full fix pipeline.
     */
    @Override
    public String requestFix(String filePath, String fileContent,
                             String issue, String recommendedFix) {
        System.out.println("[MockCodeFixClient] Applying mock fix for: " + filePath);

        String[] lines = fileContent.split("\n", -1);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (HARDCODED_SECRET.matcher(line).find()) {
                line = HARDCODED_SECRET.matcher(line).replaceAll(m ->
                        m.group(1) + " = System.getenv(\"" + m.group(1).toUpperCase() + "\")");
                System.out.println("[MockCodeFixClient] Fixed hardcoded secret on line " + (i + 1));
            }

            if (PRIVATE_KEY_BLOCK.matcher(line).find()) {
                line = "// PRIVATE KEY REMOVED — load from secrets manager";
                System.out.println("[MockCodeFixClient] Removed private key block on line " + (i + 1));
            }

            if (WEAK_HASH.matcher(line).find()) {
                line = line
                        .replaceAll("(?i)MessageDigest\\.getInstance\\(\\s*\"MD5\"\\s*\\)",
                                "MessageDigest.getInstance(\"SHA-256\")")
                        .replaceAll("(?i)MessageDigest\\.getInstance\\(\\s*\"SHA-1\"\\s*\\)",
                                "MessageDigest.getInstance(\"SHA-256\")")
                        .replaceAll("(?i)hashlib\\.md5\\s*\\(", "hashlib.sha256(")
                        .replaceAll("(?i)new\\s+MD5\\s*\\(", "new SHA256(")
                        .replaceAll("(?i)DigestUtils\\.md5", "DigestUtils.sha256");
                System.out.println("[MockCodeFixClient] Replaced weak hash on line " + (i + 1));
            }

            if (WEAK_CIPHER.matcher(line).find()) {
                line = line
                        .replaceAll("(?i)Cipher\\.getInstance\\(\"AES\"\\)",
                                "Cipher.getInstance(\"AES/GCM/NoPadding\")")
                        .replaceAll("(?i)AES.*ECB", "AES/GCM/NoPadding");
                System.out.println("[MockCodeFixClient] Replaced weak cipher on line " + (i + 1));
            }

            result.append(line);
            if (i < lines.length - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }
}
