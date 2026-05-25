package br.com.atvarquitetura.dlqauditor.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataAuditRecordRepository extends JpaRepository<AuditRecordEntity, UUID> {
}
