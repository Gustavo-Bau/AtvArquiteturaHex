package br.com.atvarquitetura.dlqauditor.domain.model;

import java.time.Instant;
import java.util.UUID;

public record AuditErrorRecord(
        UUID errorId,
        String queueName,
        String payload,
        Instant timestamp,
        AuditStatus status,
        Severity severity
) {
}
