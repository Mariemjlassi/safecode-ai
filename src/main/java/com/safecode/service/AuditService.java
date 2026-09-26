package com.safecode.service;

import com.safecode.model.AuditRequest;
import com.safecode.model.AuditResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuditService {

    /**
     * Runs a security audit against the given repository URL.
     * Returns a placeholder response until real analysis is wired in.
     */
    public AuditResponse audit(AuditRequest request) {
        // Placeholder: score 0, no findings yet
        return new AuditResponse(0, 0, Collections.emptyList());
    }
}
