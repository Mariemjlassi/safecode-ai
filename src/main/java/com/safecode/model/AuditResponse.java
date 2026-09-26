package com.safecode.model;

import java.util.List;

public class AuditResponse {

    private int score;
    private int findingsCount;
    private List<Object> findings;

    public AuditResponse() {}

    public AuditResponse(int score, int findingsCount, List<Object> findings) {
        this.score = score;
        this.findingsCount = findingsCount;
        this.findings = findings;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getFindingsCount() {
        return findingsCount;
    }

    public void setFindingsCount(int findingsCount) {
        this.findingsCount = findingsCount;
    }

    public List<Object> getFindings() {
        return findings;
    }

    public void setFindings(List<Object> findings) {
        this.findings = findings;
    }
}
