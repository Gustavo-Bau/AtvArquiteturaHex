package br.com.atvarquitetura.dlqauditor.infrastructure.persistence;

import br.com.atvarquitetura.dlqauditor.domain.model.AuditStatus;
import br.com.atvarquitetura.dlqauditor.domain.model.Severity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_errors")
public class AuditRecordEntity {

    @Id
    private UUID errorId;

    private String queueName;

    @Column(columnDefinition = "CLOB")
    private String payload;

    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    private AuditStatus status;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    public UUID getErrorId() { return errorId; }
    public void setErrorId(UUID errorId) { this.errorId = errorId; }
    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public AuditStatus getStatus() { return status; }
    public void setStatus(AuditStatus status) { this.status = status; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
}
