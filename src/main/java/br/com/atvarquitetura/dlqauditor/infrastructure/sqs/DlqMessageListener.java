package br.com.atvarquitetura.dlqauditor.infrastructure.sqs;

import br.com.atvarquitetura.dlqauditor.domain.port.in.AuditDlqMessageUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class DlqMessageListener {

    private final AuditDlqMessageUseCase useCase;

    @Value("${app.dlq.name}")
    private String queueName;

    public DlqMessageListener(AuditDlqMessageUseCase useCase) {
        this.useCase = useCase;
    }

    @SqsListener("${app.dlq.name}")
    public void listen(String payload, @Header(name = "ErrorMessage", required = false) String errorMessage) {
        useCase.handle(queueName, payload, errorMessage);
    }
}
