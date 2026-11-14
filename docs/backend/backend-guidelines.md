# Diretrizes do Backend

## Organização sugerida

Organizar por módulos de negócio, evitando uma estrutura global baseada apenas em tipos técnicos.

```text
src/
  modules/
    project-registry/
    collection/
    ingestion/
    data-platform/
    analytics/
    knowledge/
    ai-intelligence/
    evaluation/
    iam/
    audit/
  shared/
    contracts/
    errors/
    observability/
    security/
```

A estrutura final deve respeitar convenções do framework e decisões registradas no repositório.

## Regras de implementação

- Separar domínio, aplicação, infraestrutura e interfaces conforme complexidade real.
- Validar entradas na fronteira da aplicação.
- Aplicar autorização no servidor, não apenas na interface.
- Implementar idempotência na ingestão.
- Propagar correlation/request IDs.
- Usar transações para operações que exigem consistência.
- Evitar abstrações genéricas sem mais de um caso real.
- Não criar camada/service/repository sem responsabilidade clara.
- Não permitir que a camada de IA execute SQL arbitrário.
- Centralizar contratos compartilhados e versioná-los.

## Testes mínimos

- unitários para regras de domínio e cálculos;
- integração para persistência e transações;
- contrato para schemas de eventos;
- integração para ingestão/idempotência;
- autorização e isolamento entre projetos;
- testes de regressão para métricas;
- testes de avaliação para respostas da IA.

## Erros

Padronizar código, mensagem segura, detalhes de validação, correlation ID e classificação. Não expor stack traces ou dados sensíveis ao cliente.
