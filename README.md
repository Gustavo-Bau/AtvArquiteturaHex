# Serviço Auditor de DLQ

Serviço independente responsável por consumir mensagens da Dead Letter Queue (DLQ) e persisti-las em banco de auditoria para análise posterior.

## Arquitetura escolhida: Hexagonal (Ports and Adapters)

Escolhi **Arquitetura Hexagonal** porque este serviço é de integração e precisa ficar desacoplado da tecnologia de fila e de banco.

### Justificativa da escolha

1. **Domínio isolado da infraestrutura**
   - A regra de triagem de severidade (HIGH/MEDIUM/LOW) é a parte mais valiosa do negócio.
   - Essa regra não depende de AWS SQS, JPA, H2 ou qualquer framework.
   - Com isso, posso testar a regra em memória e trocar tecnologias sem quebrar a lógica.

2. **Facilidade para evoluir o serviço de apoio**
   - Hoje consumo SQS DLQ e salvo em H2/JPA.
   - Amanhã poderia trocar para RabbitMQ, Kafka, Postgres ou DynamoDB apenas criando novos adapters.
   - O caso de uso continua igual porque depende de portas, não de implementações concretas.

3. **Responsabilidade única por camada**
   - **Adapter de entrada (SQS Listener)**: apenas recebe mensagem e delega.
   - **Aplicação/Use Case**: executa fluxo transacional e regra de negócio.
   - **Adapter de saída (Repository Adapter)**: traduz domínio para persistência.
   - Essa separação evita classe “God Object” e melhora manutenção.

4. **Confiabilidade no fluxo da DLQ**
   - O `@SqsListener` só conclui a execução sem exceção depois do `save` no banco.
   - Se persistência falhar, uma exceção sobe e a mensagem não é confirmada como processada.
   - Assim, atende ao requisito de remover da DLQ somente após salvamento seguro.

---

## Estrutura do projeto

```text
src/main/java/br/com/atvarquitetura/dlqauditor
├── application
│   └── AuditDlqMessageService.java      # caso de uso
├── domain
│   ├── model                            # entidades/records de domínio
│   └── port
│       ├── in                           # entrada do caso de uso
│       └── out                          # saída para persistência
├── infrastructure
│   ├── sqs                              # adapter de entrada (consumer DLQ)
│   └── persistence                      # adapter de saída (JPA)
└── DlqAuditorApplication.java
```

---

## Regra de negócio (triagem de severidade)

Antes de persistir:

- `HIGH`: quantidade total de produtos > 100
- `MEDIUM`: quantidade total entre 50 e 100 (inclusive)
- `LOW`: quantidade total < 50

A soma é feita com base em `orderItems[].amount` do payload recebido da DLQ.

---

## Contrato persistido no banco

Cada registro segue o contrato exigido:

- `errorId`: UUID gerado pelo serviço
- `queueName`: nome da fila DLQ
- `payload`: conteúdo bruto da mensagem (string), enriquecido com erro original quando disponível
- `timestamp`: instante do processamento
- `status`: `PENDING_ANALYSIS`
- `severity`: `HIGH`, `MEDIUM` ou `LOW`

Tabela: `audit_errors`.

---

## Tecnologias

- Java 17
- Spring Boot 3.4.5
- Spring Cloud AWS SQS
- Spring Data JPA
- H2 Database

---

## Como executar

1. Configure variáveis:
   - `AWS_ACCESS_KEY_ID`
   - `AWS_SECRET_ACCESS_KEY`
   - `DLQ_NAME` (ex.: `T0XN_seu_nome_original_DLQ`)
2. Execute:

```bash
mvn spring-boot:run
```

---

## Evidência esperada (para entrega da atividade)

- Terminal mostrando recebimento da mensagem da DLQ.
- Registro persistido na tabela `audit_errors` com status `PENDING_ANALYSIS` e severidade calculada.

Sugestão para validar no H2 console:

```sql
SELECT error_id, queue_name, status, severity, timestamp FROM audit_errors;
```
