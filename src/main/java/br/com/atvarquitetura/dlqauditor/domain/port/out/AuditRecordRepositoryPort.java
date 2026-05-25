package br.com.atvarquitetura.dlqauditor.domain.port.out;

import br.com.atvarquitetura.dlqauditor.domain.model.AuditErrorRecord;

public interface AuditRecordRepositoryPort {
    void save(AuditErrorRecord record);
}
