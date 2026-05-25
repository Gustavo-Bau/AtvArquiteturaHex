package br.com.atvarquitetura.dlqauditor.domain;

import br.com.atvarquitetura.dlqauditor.application.AuditDlqMessageService;
import br.com.atvarquitetura.dlqauditor.domain.model.AuditErrorRecord;
import br.com.atvarquitetura.dlqauditor.domain.model.Severity;
import br.com.atvarquitetura.dlqauditor.domain.port.out.AuditRecordRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuditDlqMessageServiceTest {

    @Test
    void shouldClassifySeverityAsHigh() {
        var payload = """
                {"zipCode":"80010000","customerId":1,"orderItems":[{"sku":1,"amount":101}],"origin":"SQS_QUEUE","occurredAt":"2024-05-20T14:30:00Z"}
                """;

        var captureRepo = new CaptureRepo();
        var service = new AuditDlqMessageService(captureRepo, new ObjectMapper());

        service.handle("fila", payload, "erro X");

        assertEquals(Severity.HIGH, captureRepo.saved.severity());
    }

    private static class CaptureRepo implements AuditRecordRepositoryPort {
        private AuditErrorRecord saved;

        @Override
        public void save(AuditErrorRecord record) {
            this.saved = record;
        }
    }
}
