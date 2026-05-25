package br.com.atvarquitetura.dlqauditor.domain.port.in;

public interface AuditDlqMessageUseCase {
    void handle(String queueName, String payload, String originalError);
}
