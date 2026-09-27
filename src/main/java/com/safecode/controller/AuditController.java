package com.safecode.controller;

import com.safecode.model.AuditRequest;
import com.safecode.model.AuditResponse;
import com.safecode.model.FixRequest;
import com.safecode.model.FixResponse;
import com.safecode.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping("/audit")
    public ResponseEntity<AuditResponse> audit(@Valid @RequestBody AuditRequest request) {
        return ResponseEntity.ok(auditService.audit(request));
    }

    @GetMapping("/audit/{id}")
    public ResponseEntity<AuditResponse> getById(@PathVariable Long id) {
        return auditService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Returns a fix suggestion for a finding identified by its reference (e.g. "AUDIT-001").
     * Used by the Angular frontend to display a before/after diff.
     */
    @PostMapping("/audit/{id}/fix")
    public ResponseEntity<FixResponse> fix(@PathVariable Long id,
                                           @RequestBody FixRequest request) {
        return auditService.fix(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Applies an AI-generated fix for a specific finding, confirms the fix via re-scan,
     * and returns the updated audit with the recalculated score.
     *
     * <p>Supported categories: Secrets Handling (Category D).
     * Returns HTTP 400 for any other category.
     */
    @PostMapping("/audit/{id}/fix/{findingId}")
    public ResponseEntity<AuditResponse> applyFix(
            @PathVariable Long id,
            @PathVariable Long findingId) {
        return ResponseEntity.ok(auditService.applyFix(id, findingId));
    }
}
