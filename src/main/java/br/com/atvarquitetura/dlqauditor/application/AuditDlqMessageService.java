package br.com.atvarquitetura.dlqauditor.application;

import br.com.atvarquitetura.dlqauditor.domain.model.AuditErrorRecord;
import br.com.atvarquitetura.dlqauditor.domain.model.AuditStatus;
import br.com.atvarquitetura.dlqauditor.domain.model.FailedOrderEvent;
import br.com.atvarquitetura.dlqauditor.domain.model.Severity;
import br.com.atvarquitetura.dlqauditor.domain.port.in.AuditDlqMessageUseCase;
import br.com.atvarquitetura.dlqauditor.domain.port.out.AuditRecordRepositoryPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuditDlqMessageService implements AuditDlqMessageUseCase {

    private final AuditRecordRepositoryPort repository;
    private final ObjectMapper objectMapper;

    public AuditDlqMessageService(AuditRecordRepositoryPort repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void handle(String queueName, String payload, String originalError) {
        FailedOrderEvent event = parsePayload(payload);
        Severity severity = classify(event.totalItems());

        String enrichedPayload = payload;
        if (originalError != null && !originalError.isBlank()) {
            enrichedPayload = payload + "\noriginalError=" + originalError;
        }

        AuditErrorRecord record = new AuditErrorRecord(
                UUID.randomUUID(),
                queueName,
                enrichedPayload,
                Instant.now(),
                AuditStatus.PENDING_ANALYSIS,
                severity
        );

        repository.save(record);
    }

    Severity classify(int totalItems) {
        if (totalItems > 100) return Severity.HIGH;
        if (totalItems >= 50) return Severity.MEDIUM;
        return Severity.LOW;
    }

    private FailedOrderEvent parsePayload(String payload) {
        try {
            return objectMapper.readValue(payload, FailedOrderEvent.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Payload inválido recebido da DLQ", e);
        }
    }
}
