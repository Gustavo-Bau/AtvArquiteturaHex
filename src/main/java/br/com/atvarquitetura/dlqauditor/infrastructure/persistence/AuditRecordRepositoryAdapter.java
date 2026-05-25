package br.com.atvarquitetura.dlqauditor.infrastructure.persistence;

import br.com.atvarquitetura.dlqauditor.domain.model.AuditErrorRecord;
import br.com.atvarquitetura.dlqauditor.domain.port.out.AuditRecordRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class AuditRecordRepositoryAdapter implements AuditRecordRepositoryPort {

    private final SpringDataAuditRecordRepository repository;

    public AuditRecordRepositoryAdapter(SpringDataAuditRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(AuditErrorRecord record) {
        AuditRecordEntity entity = new AuditRecordEntity();
        entity.setErrorId(record.errorId());
        entity.setQueueName(record.queueName());
        entity.setPayload(record.payload());
        entity.setTimestamp(record.timestamp());
        entity.setStatus(record.status());
        entity.setSeverity(record.severity());
        repository.save(entity);
    }
}
